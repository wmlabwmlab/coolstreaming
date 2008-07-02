package se.peertv.peertvsim.examples.Tennis;

import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.network.Network;

public class ToySimulator extends EventLoop {

	public ToySimulator() throws Exception{
		
		ToyPeer server = new ToyPeer(10, 1, 1);
		Network.getInstance().add(server);

		ToyPeer client = new ToyPeer(20, 1, 1);
		Network.getInstance().add(client);
		
		//Network.getInstance().add(new ToyPeer(30, 1, 1));

	}

	@Override
	protected boolean postEventExecution() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new ToySimulator().run();
	}

}
