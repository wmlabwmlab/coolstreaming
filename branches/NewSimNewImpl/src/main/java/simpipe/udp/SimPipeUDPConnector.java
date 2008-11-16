package simpipe.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.ExceptionMonitor;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.support.AbstractIoFilterChain;
import org.apache.mina.common.support.DefaultConnectFuture;

import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.network.udp.bw.BandwidthManager;
import se.peertv.peertvsim.network.udp.bw.NodeBandwidth;
import simpipe.base.SimPipe;
import simpipe.base.SimPipeConnectFuture;
import simpipe.base.SimPipeConnector;
import simpipe.base.SimPipeIdleStatusChecker;
import simpipe.base.SimPipeSessionImpl;
import simpipe.base.support.SimPipeAddress;
import simpipe.base.support.peers.PeerManager;
import simpipe.udp.support.SimPipeUDPSessionImpl;

public class SimPipeUDPConnector extends SimPipeConnector {
	@Override
	protected SimPipeSessionImpl createSession(SocketAddress localAddress, IoHandler handler, IoServiceConfig config, SimPipe entry) {
		return new SimPipeUDPSessionImpl(this, config, getListeners(), localAddress /* new AnonymousSocketAddress() */, handler, entry);
	}

	public synchronized ConnectFuture connect(final SocketAddress address, SocketAddress localAddress, final IoHandler handler, IoServiceConfig config) {

		PeerManager.setHandlingTCP(false);

		try {
			if (Conf.USE_BANDWIDTH_MODEL) {

				NodeBandwidth nodeBandwidth = BandwidthManager.getNodeBandwidth(Reflection.getExecutingGroup());

				PeerManager.createUDPPeer(nodeBandwidth.getDownBandwidth(), nodeBandwidth.getDownWindowSize(), nodeBandwidth.getUpBandwidth(), nodeBandwidth.getUpWindowSize());

			} else {
				PeerManager.createUDPPeer();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return super.connect(address, localAddress, handler, config);
	}

	public void unbindAll() {

		PeerManager.setHandlingTCP(false);

		super.unbindAll();
	}

	// @Override
	// public synchronized ConnectFuture connect(final SocketAddress address, SocketAddress localAddress, final IoHandler handler, IoServiceConfig
	// config) {
	//
	// try {
	// if (Conf.USE_BANDWIDTH_MODEL) {
	//
	// NodeBandwidth nodeBandwidth = BandwidthManager.getNodeBandwidth(Reflection.getExecutingGroup());
	//
	// PeerManager.createUDPPeer(nodeBandwidth.getDownBandwidth(), nodeBandwidth.getDownWindowSize(), nodeBandwidth.getUpBandwidth(),
	// nodeBandwidth.getUpWindowSize());
	//
	// } else {
	// PeerManager.createUDPPeer();
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return super.connect(address, localAddress, handler, config);
	// }
}
