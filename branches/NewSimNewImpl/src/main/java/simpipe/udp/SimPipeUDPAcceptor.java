package simpipe.udp;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;

import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.network.udp.bw.BandwidthManager;
import se.peertv.peertvsim.network.udp.bw.NodeBandwidth;
import simpipe.base.SimPipe;
import simpipe.base.SimPipeAcceptor;
import simpipe.base.support.SimPipeAddress;
import simpipe.base.support.peers.PeerManager;

public class SimPipeUDPAcceptor extends SimPipeAcceptor {

	public void bind(SocketAddress address, final IoHandler handler, IoServiceConfig config) throws IOException {

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

		super.bind(address, handler, config);
	}

	public void unbind(final SocketAddress address) {

		PeerManager.setHandlingTCP(false);

		super.unbind(address);
	}

}
