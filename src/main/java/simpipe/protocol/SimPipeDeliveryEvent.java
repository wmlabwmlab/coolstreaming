package simpipe.protocol;

import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.Message;
import se.peertv.peertvsim.network.MessageDeliveryEvent;
import se.peertv.peertvsim.network.Network;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.utils.A;
import se.peertv.peertvsim.utils.P;

public class SimPipeDeliveryEvent extends MessageDeliveryEvent {

	public SimPipeDeliveryEvent(long time, SimpPipeMessage args) {
		super(time, args);
	}

	@Override
	public void handle() throws Throwable {
		SimpPipeMessage msg = (SimpPipeMessage) args;
		P.rint("t["+msg.getSource()+"->"+msg.getDest()+"]("+Scheduler.getInstance().now+") RECV "+msg.toString());

		super.handle();
	}
		
}
