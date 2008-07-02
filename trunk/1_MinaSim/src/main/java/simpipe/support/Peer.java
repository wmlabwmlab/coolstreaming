package simpipe.support;

import org.apache.mina.common.IoFilterChain;
import org.apache.mina.common.support.BaseIoService;

import se.peertv.peertvsim.network.Node;
import simpipe.protocol.SimpPipeMessage;

public class Peer extends Node{

	BaseIoService minaPeer;
	
	public Peer(int id, long downCapacity, long upCapacity) {
		super(id, downCapacity, upCapacity);
	}

	public BaseIoService getMinaPeer() {
		return minaPeer;
	}


	public void setMinaPeer(BaseIoService minaPeer) {
		this.minaPeer = minaPeer;
	}
	

	public void handle(SimpPipeMessage msg){
		SimPipeFilterChain chain = (SimPipeFilterChain)msg.getMinaEvent().getTargetSession().getFilterChain();
		chain.fireEvent(msg.getMinaEvent());
		//chain = minaPeer.getFilterChain();
	}
	
}
