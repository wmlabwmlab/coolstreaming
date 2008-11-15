package se.peertv.peertvsim.thread;

import se.peertv.peertvsim.core.Callbackable;
import se.peertv.peertvsim.core.NodeIdInt;

public class SleepState extends Callbackable implements NodeIdInt{
	public Object pauser = new Object();
	public Callbackable callbackableImpl = new Callbackable();
	private int id;
	private String threadName;

	public SleepState(int id,String threadName) {
		super();
		this.id=id;
		this.threadName=threadName;
	}

	public void timeOut() {
		synchronized (pauser) {
			pauser.notify();
		}
	}

	@Override
	public int getId() {
		return id;
	}

	public String toString() {
		return " SleepState for thread: "+threadName;
	}
	
	

}
