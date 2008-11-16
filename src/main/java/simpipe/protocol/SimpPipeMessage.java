package simpipe.protocol;

import java.io.Serializable;

import se.peertv.peertvsim.network.Message;
import simpipe.base.support.MinaEvent;

public class SimpPipeMessage extends Message implements Serializable{

	private static final long serialVersionUID = 8291058387782312029L;
	
	private MinaEvent minaEvent;

	public SimpPipeMessage(MinaEvent minaEvent) {
		this.minaEvent = minaEvent;
	}

	public MinaEvent getMinaEvent() {
		return minaEvent;
	}

	public void setMinaEvent(MinaEvent minaEvent) {
		this.minaEvent = minaEvent;
	}

	public Object getCarriedData() {
		return minaEvent.getData();
	}

	@Override
	public String toString() {
		String data = "";
		// if (minaEvent == null)
		// return "[" + data + "]" + "<" + seqNum + ">";
		// if (minaEvent.getData() != null && !(minaEvent.getData() instanceof SimpPipeMessage))
		// data = minaEvent.getData().toString();

		return data;
		// return minaEvent.getType() + "[" + minaEvent.getData() + "]" + "<" + seqNum + ">";
	}

}
