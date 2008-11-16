package simpipe.multiplexor;

import simpipe.protocol.SimpPipeUDPMessage;


public interface SimPipeMultiplexorInt {

	public void messageReceived(SimpPipeUDPMessage msg) throws Exception;

}
