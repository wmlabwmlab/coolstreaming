package simpipe.protocol;

import simpipe.base.support.MinaEvent;

public class SimpPipeUDPMessage extends SimpPipeMessage {
	private static final long serialVersionUID = 3003673175952856053L;

	private Object bb;

	public SimpPipeUDPMessage(Object bb) {
		this(null, bb);
	}

	public SimpPipeUDPMessage(MinaEvent minaEvent, Object bb) {
		super(minaEvent);
		this.bb = bb;
	}

	public Object getPayload() {
		return bb;
	}

	public void setPayload(byte[] bb) {
		this.bb = bb;
	}

}
