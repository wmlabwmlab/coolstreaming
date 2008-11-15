package se.peertv.peertvsim.thread.examples;

import se.peertv.peertvsim.thread.Thread;

public class Runner extends Thread {

	@Override
	public void run() {

		System.out.println("Start pause at: " + System.currentTimeMillis());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("End pause at: " + System.currentTimeMillis());

	}

}
