package se.peertv.peertvsim.thread;

import se.peertv.peertvsim.core.Timer;

public class Thread extends java.lang.Thread {

	public static boolean SIMULATION = false;
	public static int ID = -1;

	public Thread() {
		super();
	}

	public Thread(Runnable target, String name) {
		super(target, name);
	}

	public Thread(Runnable target) {
		super(target);
	}

	public Thread(String name) {
		super(name);
	}

	public Thread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
	}

	public Thread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
	}

	public Thread(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	public Thread(ThreadGroup group, String name) {
		super(group, name);
	}

	/* Sleep */

	public static void sleep(long millis) throws InterruptedException {

		if (SIMULATION) {
			SleepState sleepState = new SleepState(ID,Thread.currentThread().getName());
			try {
				new Timer(millis, sleepState, "timeOut");
			} catch (Exception e) {
				e.printStackTrace();
			}
			synchronized (sleepState.pauser) {
				sleepState.pauser.wait();
			}
		} else {
			java.lang.Thread.sleep(millis);
		}

	}

	public static void setSimulation(boolean simulated) {
		SIMULATION = simulated;
	}

	public static void setID(int id) {
		ID = id;
	}
}
