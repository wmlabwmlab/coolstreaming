package se.peertv.peertvsim.examples.Tennis;

import org.apache.log4j.BasicConfigurator;

import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.KillEvent;
import se.peertv.peertvsim.network.Network;

public class ToySimulator extends EventLoop {

	public ToySimulator() throws Exception {

		ToyPeer server = new ToyPeer(10, 1, 1);
		Network.getInstance().add(server);

		ToyPeer client = new ToyPeer(20, 1, 1);
		Network.getInstance().add(client);

		Network.getInstance().add(new ToyPeer(30, 1, 1));

		int id = 10;
		Scheduler.getInstance().enqueue(new KillEvent(500,id));
	}

	@Override
	public boolean postEventExecution() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();

		new ToySimulator().run();
	}

}
