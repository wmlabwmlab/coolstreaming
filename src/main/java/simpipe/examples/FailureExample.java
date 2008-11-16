package simpipe.examples;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.executor.SchedulingExecutor;
import simpipe.base.support.peers.PeerManager;
import simpipe.protocol.SimPipeKillEvent;

public class FailureExample extends EventLoop {

	final static int PORT = 5000;

	public FailureExample() {
		super();
	}

	public static void main(String[] args) throws Exception {

		SimulableSystem.setSimulation(true);
		BasicConfigurator.configure();

		FailureExample m = new FailureExample();
		Reflection.setExecutingGroup("peer");
		m.createPeer();

		Scheduler.getInstance().enqueue(new SimPipeKillEvent(150, "peer"));
		m.run();
	}

	class Hello implements Runnable {

		@Override
		public void run() {
			System.out.println("hello");
		}

	}

	public void createPeer() throws Exception {

		PeerManager.createUDPPeer();
		new SchedulingExecutor(111).scheduleAtFixedRate(new Hello(), false, 1, 50, TimeUnit.MILLISECONDS);

	}

	@Override
	public boolean postEventExecution() {
		// TODO Auto-generated method stub
		return false;
	}

}
