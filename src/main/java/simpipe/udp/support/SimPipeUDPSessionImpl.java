package simpipe.udp.support;

import java.net.SocketAddress;

import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoService;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.support.IoServiceListenerSupport;

import simpipe.base.SimPipe;
import simpipe.base.SimPipeFilterChain;
import simpipe.base.SimPipeSessionImpl;

public class SimPipeUDPSessionImpl extends SimPipeSessionImpl {

	public SimPipeUDPSessionImpl(IoService service, IoServiceConfig serviceConfig, IoServiceListenerSupport serviceListeners, SocketAddress localAddress, IoHandler handler, SimPipe remoteEntry) {
		super(service, serviceConfig, serviceListeners, localAddress, handler,
				remoteEntry);		
	}
	
	public SimPipeUDPSessionImpl(SimPipeUDPSessionImpl impl, SimPipe remoteEntry) {
		super(impl,remoteEntry);
	}

	@Override
	protected SimPipeFilterChain createFilterChain() {
		return new SimPipeUDPFilterChain(this);
	}
	
	@Override
	protected SimPipeSessionImpl createRemoteSession(SimPipe remoteEntry) {
		return new SimPipeUDPSessionImpl(this, remoteEntry);
	}
	
	@Override
	public String toString() {
		return "(UDP SIM_PIPE, R: " + getRemoteAddress()
        + ", L: " + getLocalAddress() + ", S: " + getServiceAddress()
        + ')';
	}

}
