package simpipe.tcp.support;

import java.net.SocketAddress;

import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoService;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.support.IoServiceListenerSupport;

import simpipe.base.SimPipe;
import simpipe.base.SimPipeFilterChain;
import simpipe.base.SimPipeSessionImpl;

public class SimPipeTCPSessionImpl extends SimPipeSessionImpl {

	public SimPipeTCPSessionImpl(IoService service, IoServiceConfig serviceConfig, IoServiceListenerSupport serviceListeners, SocketAddress localAddress, IoHandler handler, SimPipe remoteEntry) {
		super(service, serviceConfig, serviceListeners, localAddress, handler,
				remoteEntry);		
	}
	
	public SimPipeTCPSessionImpl(SimPipeTCPSessionImpl impl, SimPipe remoteEntry) {
		super(impl,remoteEntry);
	}

	@Override
	protected SimPipeFilterChain createFilterChain() {
		return new SimPipeTCPFilterChain(this);
	}
	
	@Override
	protected SimPipeSessionImpl createRemoteSession(SimPipe remoteEntry) {
		return new SimPipeTCPSessionImpl(this, remoteEntry);
	}
	
	@Override
	public String toString() {
		return "(UDP SIM_PIPE, R: " + getRemoteAddress()
        + ", L: " + getLocalAddress() + ", S: " + getServiceAddress()
        + ')';
	}

}
