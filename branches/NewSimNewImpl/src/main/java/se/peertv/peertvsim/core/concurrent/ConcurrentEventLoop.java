package se.peertv.peertvsim.core.concurrent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.EventLoopInt;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.SchedulerInt;
import se.peertv.peertvsim.utils.A;

public class ConcurrentEventLoop implements EventLoopInt {

	private static final int POOL_SIZE = 2;
	private static final int MAX_POOL_SIZE = 2;

	final static Set<String> alreadyRunning = new HashSet<String>();

	CandidatesMap currentCandidatesEntry = null;

	long start;

	// The following stuff is reported in stats
	protected String causeOfTermination;
	protected long simTime;
	public static long realTime;

	private long pendingCounter = 0;
	public long currentSimTime = 0;

	private boolean isTerminated = false;

	private static ConcurrentEventLoop signleton;

	private ConcurrentScheduler scheduler;

	final ThreadPoolExecutor executor = new ThreadPoolExecutor(POOL_SIZE,
			MAX_POOL_SIZE, 1, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

	private EventLoop eventLoop;

	private ConcurrentEventLoop(EventLoop eventLoop) {

		Scheduler.schedulerType = Scheduler.CONCURRENT_SCHEDULING;

		this.eventLoop = eventLoop;

		scheduler = new ConcurrentScheduler();
	}

	public static ConcurrentEventLoop getInstance(EventLoop loop) {

		if (signleton == null) {
			signleton = new ConcurrentEventLoop(loop);
		}

		return signleton;
	}

	public void preSimulationLoop() {
		causeOfTermination = null;
		start = System.currentTimeMillis();
	}

	public void postSimulationLoop() {
		if (causeOfTermination == null)
			causeOfTermination = "Max Simulation Time Reached";

		realTime = System.currentTimeMillis() - start;
		simTime = scheduler.getNow();

		// P.rint(causeOfTermination);
	}

	public void prematureTermination(Throwable t) {
		causeOfTermination = t.getMessage();
		realTime = System.currentTimeMillis() - start;
		simTime = scheduler.getNow();

		// P.rint(causeOfTermination);
		// P.rint(t);
		t.printStackTrace();
	}

	protected void preEventExecution() {
		eventLoop.preEventExecution();
	}

	protected boolean postEventExecution() {
		return eventLoop.postEventExecution();
	}

	public boolean run() {
		try {
			preSimulationLoop();
			eventLoop.preSimulationLoop();

			synchronized (this) {
				do {
					currentCandidatesEntry = scheduler.dequeue();

					if (currentCandidatesEntry == null) {
						break;
					}

					// System.out.println("Dequue: "+currentCandidatesEntry);
					currentSimTime = currentCandidatesEntry.getTime();

					boolean runAtleastOne = executeCandidates(currentCandidatesEntry
							.getGroups());

					// wait until all events of this point in time have been
					// processed
					if (runAtleastOne) {
						// System.out.println("wait");
						wait();
					}
					// System.out.println("Finished time: " + currentSimTime);

				} while (scheduler.getNow() < Conf.MAX_SIMULATION_TIME);
			}

			System.out.println("Exited");

			executor.shutdownNow();

			postSimulationLoop();
			eventLoop.postSimulationLoop();

			return true;

		} catch (Throwable t) {
			prematureTermination(t);
			return false;
		}
	}

	public void terminationSignal() {
		isTerminated = true;
	}

	public void eventTerminated(String group,long time) {
		try {
			A.ssert(time == currentSimTime);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		synchronized (this) {

			alreadyRunning.remove(group);

			pendingCounter--;
			// System.out.println("PC-- " + pendingCounter);

			if (!currentCandidatesEntry.getGroups().isEmpty()) {

				executeCandidates(currentCandidatesEntry.getGroups());

			} else {

				if (pendingCounter == 0) {
					notify();
				}
			}

		}

	}

	Set<String> removedCandidateGroups = new HashSet<String>();

	private boolean executeCandidates(Set<String> candidateGroups) {

		boolean runAtleastOne = false;

		removedCandidateGroups.clear();

		for (String group : candidateGroups) {

			if (!alreadyRunning.contains(group)) {

				Event e = currentCandidatesEntry.pollGroup(group);

				if (currentCandidatesEntry.isGroupEmpty(group)) {
					removedCandidateGroups.add(group);
				}

				alreadyRunning.add(group);

				RunnableEvent runnableEvent = new RunnableEvent(e, group, this);

				pendingCounter++;
				// System.out.println("PC++ " + pendingCounter);
				// System.out.println("execute,pc=" + pendingCounter);

				runAtleastOne = true;
				executor.execute(runnableEvent);
			}

		}

		for (String string : removedCandidateGroups) {
			candidateGroups.removeAll(removedCandidateGroups);
		}

		return runAtleastOne;

	}

	public synchronized long getCurrentSimTime() {
		return currentSimTime;
	}

	public static SchedulerInt getScheduler() {
		return signleton.scheduler;
	}

	public static long getSimulationTime() {
		return realTime;
	}

}
