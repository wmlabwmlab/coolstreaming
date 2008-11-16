package se.peertv.peertvsim.executor;

import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import se.peertv.peertvsim.executor.SchedulingExecutor.RealIterativeState;

public class RealSchedulingExecutorFuture implements ScheduledFuture<Object> {
	private boolean cancel = false;
	private RealIterativeState state;
	private TimeUnit unit;
	private long delay;

	public RealSchedulingExecutorFuture(RealIterativeState state, long delay, TimeUnit unit) {
		this.state = state;
		this.unit = unit;
		this.delay = delay;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long d = unit.convert(delay - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		return d;
	}

	@Override
	public int compareTo(Delayed other) {
		if (other == this)
			return 0;
		Delayed x = (Delayed) other;
		long diff = delay - x.getDelay(unit);
		if (diff < 0)
			return -1;
		else if (diff > 0)
			return 1;
		else
			return 1;
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		try {
			state.cancel(mayInterruptIfRunning);
			cancel = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean isCancelled() {
		return cancel;
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

}
