package simpipe.protocol;

import se.peertv.peertvsim.network.Message;
import se.peertv.peertvsim.network.MessageDeliveryEvent;
import simpipe.support.Event;



public class SimpPipeMessage extends Message{
	Event minaEvent;

	public SimpPipeMessage(Event minaEvent) {
		this.minaEvent = minaEvent;
	}

	public Event getMinaEvent() {
		return minaEvent;
	}

	@Override
	public String toString(){
		return minaEvent.getType()+":"+source+"-->"+dest+" <"+seqNum+">";
	}
	
	
}
