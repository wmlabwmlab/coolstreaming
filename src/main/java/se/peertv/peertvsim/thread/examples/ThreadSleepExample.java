package se.peertv.peertvsim.thread.examples;

import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.thread.Thread;

public class ThreadSleepExample extends EventLoop{

	private Runner runner;

	public ThreadSleepExample() {
		this.runner=new Runner();
	}

	public static void main(String[] args){
		
		ThreadSleepExample ex=new ThreadSleepExample();
		Thread.setSimulation(true);
		Thread.setID(0);
		ex.runner.start();
		ex.run();
		
	}

	@Override
	protected boolean postEventExecution() {
		return true;
	}

}
