/**
 * =============================================== 
 *  File     : $Id: EventLoop.java,v 1.7 2007/06/06 09:22:49 sameh Exp $
 *  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
 *  Version  :$Revision: 1.7 $
 *  Tag	  : $Name:  $
 *  Last edited by   : $Author: sameh $
 *  Last updated:    $Date: 2007/06/06 09:22:49 $
 *===============================================
 */
package se.peertv.peertvsim.core.sequential;

import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.EventLoopInt;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.recorder.Recorder;

public class SequentialEventLoop implements EventLoopInt {

	public static boolean RECORD = false;

	private EventLoop parentEventLoop;

	private static SequentialScheduler scheduler;

	// The following stuff is reported in stats
	protected String causeOfTermination;
	protected long start;
	protected long simTime;
	protected long elapsedTime;

	public SequentialEventLoop(EventLoop eventLoop, int schedulerType) {

		Scheduler.schedulerType = schedulerType;

		scheduler = (SequentialScheduler) Scheduler.getInternalSchedulerInstance();

		this.parentEventLoop = eventLoop;
	}

	public void preSimulationLoop() {
		causeOfTermination = null;
		start = System.currentTimeMillis();
	};

	public void postSimulationLoop() {
		if (causeOfTermination == null)
			causeOfTermination = "Max Simulation Time Reached";

		elapsedTime = System.currentTimeMillis() - start;
		simTime = scheduler.getNow();

		// P.rint(causeOfTermination);
	}

	public void prematureTermination(Throwable t) {
		causeOfTermination = t.getMessage();
		elapsedTime = System.currentTimeMillis() - start;
		simTime = scheduler.getNow();

		// P.rint(causeOfTermination);
		// P.rint(t);
		t.printStackTrace();
	}

	public boolean run() {
		try {
			preSimulationLoop();
			parentEventLoop.preSimulationLoop();
			while (true) {
				Event e = scheduler.dequeue();
				if (e != null && scheduler.getNow() < Conf.MAX_SIMULATION_TIME) {
					parentEventLoop.preEventExecution();
					e.handle();

					if (RECORD) {
						Recorder.getInstance().record(e);
					}

					if (parentEventLoop.postEventExecution()) {
						break;
					}
				} else {
					break; // simulationEnded
				}
			}
			postSimulationLoop();
			parentEventLoop.postSimulationLoop();
			return true;
		} catch (Throwable t) {
			prematureTermination(t);
			return false;
		}
	}

	public long getSimulationTime() {
		return elapsedTime;
	}

}
