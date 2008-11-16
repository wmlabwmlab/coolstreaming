package se.peertv.peertvsim.executor;

import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.core.NodeIdInt;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.Timer;
import simpipe.base.support.peers.PeerManager;

public class SimSchedulingExecutorFuture implements ScheduledFuture<Object>, NodeIdInt, Serializable {

	private static final long serialVersionUID = -1479165000582993935L;

	private static final Logger log = LoggerFactory.getLogger(SimSchedulingExecutorFuture.class);

	private final long delay;
	private final Runnable activity;
	private final long id;
	private final String group;
	private final boolean isNoGroup;
	private long timestamp;
	private int howManyTimes;
	private Timer timer;
	private boolean cancel = false;

	/*
	 * Used for scheduleAtFixedRate a number of times and by the other constructors
	 */
	public SimSchedulingExecutorFuture(long id, Runnable activity, long delay, Timer timer, int numberOfTimes, boolean isNoGroup) {
		this.id = id;
		group = Reflection.getExecutingGroup();
		this.activity = activity;
		this.delay = delay;
		this.isNoGroup = isNoGroup;
		this.timer = timer;
		howManyTimes = numberOfTimes;
		timestamp = SimulableSystem.currentTimeMillis();
	}

	/*
	 * Used for schedule
	 */
	public SimSchedulingExecutorFuture(long id, Runnable activity, long delay, boolean isNoGroup) {
		this(id, activity, delay, null, 1, isNoGroup);
	}

	/*
	 * Used for scheduleAtFixedRate
	 */
	public SimSchedulingExecutorFuture(long id, Runnable activity, long delay, Timer timer, boolean isNoGroup) {
		this(id, activity, delay, timer, -1, isNoGroup);
	}

	@Override
	public long getDelay(TimeUnit unit) {
		final TimeUnit lUnit = TimeUnit.MILLISECONDS;
		long d = unit.convert((timestamp + delay - SimulableSystem.currentTimeMillis()), TimeUnit.MILLISECONDS);
		return d;
	}

	@Override
	public int compareTo(Delayed other) {
		if (other == this)
			return 0;
		Delayed x = other;
		long diff = delay - x.getDelay(TimeUnit.MILLISECONDS);
		if (diff < 0)
			return -1;
		else if (diff > 0)
			return 1;
		else
			return 1;
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		try {
			cancel = true;
			try {
				timer.reset();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void run() {
		if (SimulableSystem.isSimPipeTraceEnabled())
			log.info("T(" + Scheduler.getInstance().getNow() + ")," + Reflection.getExecutingGroup() + " STARTED " + activity.getClass().getName());

		boolean execute = false;

		// If TCP node
		PeerManager.setHandlingTCP(true);
		if (isNoGroup || PeerManager.peerExists(Reflection.getExecutingGroup())) {

			execute = true;

		} else {

			// If UDP Node
			PeerManager.setHandlingTCP(false);
			if (isNoGroup || PeerManager.peerExists(Reflection.getExecutingGroup())) {

				execute = true;

			} else {

				/*
				 * Peer doesn't exist
				 */

			}

		}

		if (execute) {
			activity.run();

			howManyTimes--;
			if (!cancel && (howManyTimes >= 1 || howManyTimes < 0)) {
				try {
					// TestCase.assertEquals(Reflection.getExecutingNode(), id);
					timer.set(delay, this, "run");
					timestamp = SimulableSystem.currentTimeMillis();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					timer.reset();
				} catch (Exception e) {
				}
			}
			if (SimulableSystem.isSimPipeTraceEnabled())
				log.info("-------------------------------- ENDED -------------------");
		}

	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		return new Long(delay);
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return null;
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public int getId() {
		return (int) id;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public String getGroup() {
		return group;
	}

	@Override
	public String toString() {
		return "Grp:[" + getGroup() + "]" + activity.getClass().getSimpleName() + " times: " + howManyTimes;
	}

}
