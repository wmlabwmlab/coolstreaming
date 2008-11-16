package se.peertv.peertvsim.executor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.core.Timer;

public class SchedulingExecutor {

	private final long id;

	// FIXME: sameh: why is realExcutor static?
	// magnus: Because all schedulers in the system should share the same worker threads
	private static ScheduledThreadPoolExecutor realExecutor;
	private static Object realExecutorMutex = new Object();

	private static class ForgivableTask implements Runnable {
		private final Runnable mBlackSheep;

		private ForgivableTask(final Runnable blackSheep) {
			mBlackSheep = blackSheep;
		}

		public void run() {
			try {
				mBlackSheep.run();
			} catch (final Exception e) {
				final StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				System.err.println("Black sheep task \"" + mBlackSheep.toString() + "\" threw an unhandled exception: " + sw.toString());
			}
		}
	}

	public SchedulingExecutor(final long id) {
		this.id = id;

		if (!SimulableSystem.isSimulation()) {

			synchronized (realExecutorMutex) {
				if (realExecutor == null) {
					final int threads = Runtime.getRuntime().availableProcessors() + 1;
					realExecutor = new ScheduledThreadPoolExecutor(threads);
					realExecutor.setMaximumPoolSize(threads);
					realExecutor.allowCoreThreadTimeOut(false);
				}
			}
		}

	}

	/**
	 * Schedules a {@link Runnable} after an initial delay
	 * 
	 * @return a {@link ScheduledFuture} which you can cancel the timer with
	 */
	public ScheduledFuture<?> schedule(final Runnable command, boolean isNoGroup, final long delay, final TimeUnit unit) {
		if (!SimulableSystem.isSimulation()) {
			return realExecutor.schedule(new ForgivableTask(command), delay, unit);
		} else {
			try {

				/*
				 * Transform time in millisecs
				 */
				final TimeUnit lUnit = TimeUnit.MILLISECONDS;
				long msDelay = lUnit.convert(delay, unit);
				// long msDelay = delay;
				if(msDelay==0){
					msDelay++;
				}
				final SimSchedulingExecutorFuture future = new SimSchedulingExecutorFuture(id, command, msDelay, isNoGroup);

				final Timer timer = new Timer();
				timer.set(msDelay, future, "run");
				future.setTimer(timer);

				return future;
			} catch (final Exception e) {
				return null;
			}
		}

	}

	public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
		return schedule(command, false, delay, unit);
	}

	public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period, final TimeUnit unit) {
		return scheduleAtFixedRate(command, false, initialDelay, period, unit);
	}

	/**
	 * Schedules a {@link Runnable} at fixed rate and after an initial delay
	 * 
	 * @return a {@link ScheduledFuture} which you can cancel the timer with
	 */
	public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final boolean isNoGroup, final long initialDelay, final long period, final TimeUnit unit) {
		if (!SimulableSystem.isSimulation()) {
			return realExecutor.scheduleAtFixedRate(new ForgivableTask(command), initialDelay, period, unit);
		} else {
			try {

				final TimeUnit lUnit = TimeUnit.MILLISECONDS;
				final long msDelay = lUnit.convert(initialDelay, unit);
				final long msPeriod = lUnit.convert(period, unit);

				final Timer timer = new Timer();

				final SimSchedulingExecutorFuture future = new SimSchedulingExecutorFuture(id, command, msPeriod, timer, isNoGroup);
				timer.set(msDelay, future, "run");

				return future;
			} catch (final Exception e) {
				return null;
			}
		}

	}

	public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period, final TimeUnit unit, final int numberOfTimes) {
		return scheduleAtFixedRate(command, false, initialDelay, period, unit, numberOfTimes);
	}

	/**
	 * Schedules a {@link Runnable} instance a specific number of times, at fixed rate and with an initial delay
	 * 
	 * @return a {@link ScheduledFuture} which you can cancel the timer with
	 */
	public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final boolean isNoGroup, final long initialDelay, final long period, final TimeUnit unit, final int numberOfTimes) {
		if (!SimulableSystem.isSimulation()) {
			final RealIterativeState state = new RealIterativeState(id, new ForgivableTask(command), period, unit);
			final RealSchedulingExecutorFuture future = new RealSchedulingExecutorFuture(state, period, unit);
			realExecutor.schedule(state, initialDelay, unit);
			return future;
		} else {
			try {

				final TimeUnit lUnit = TimeUnit.MILLISECONDS;
				final long msDelay = lUnit.convert(initialDelay, unit);
				final long msPeriod = lUnit.convert(period, unit);

				final Timer timer = new Timer();

				final SimSchedulingExecutorFuture future = new SimSchedulingExecutorFuture(id, command, msPeriod, timer, numberOfTimes, isNoGroup);
				timer.set(msDelay, future, "run");

				return future;
			} catch (final Exception e) {
				return null;
			}
		}

	}

	public long getId() {
		return id;
	}

	public class RealIterativeState implements Runnable {

		long id;
		private final Runnable activity;
		private final long sleepTime;
		private final TimeUnit unit;
		private ScheduledFuture<?> future;
		private boolean cancel = false;
		private final String name;

		public RealIterativeState(final long id, final Runnable activity, final long sleepTime, final TimeUnit unit) {
			this.activity = activity;
			this.sleepTime = sleepTime;
			this.unit = unit;
			name = "it" + System.currentTimeMillis();
		}

		public void run() {
			System.out.println("Executing real iterative activity " + name);
			if (!cancel) {
				activity.run();
				future = realExecutor.schedule(this, sleepTime, unit);
			}
		}

		public void cancel(final boolean mayInterruptIfRunning) {
			future.cancel(mayInterruptIfRunning);
			cancel = true;
		}

	}

	public interface SimState {

		public void cancel();

	}

}
