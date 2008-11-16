package se.peertv.peertvsim.core;

public interface SchedulerInt {

	public void enqueue(Event e) throws Exception;

	public boolean remove(Event e);

	public void reset();

	public void setTime(long currentTime);

	public long getNow();
	
	public void setIngnoreEnqueuing(boolean isIgnoreEnueueing);
}
