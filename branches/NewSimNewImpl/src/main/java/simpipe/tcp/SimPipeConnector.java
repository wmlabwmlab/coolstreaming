/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package simpipe.tcp;

import java.net.SocketAddress;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;

import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.network.tcp.bw.gen.BandwidthDistributionGenerator;
import se.peertv.peertvsim.network.tcp.bw.gen.BandwidthDistributionGenerator.NodeBandwith;
import se.peertv.peertvsim.utils.P;
import simpipe.base.SimPipe;
import simpipe.base.SimPipeSessionImpl;
import simpipe.base.support.SimPipeAddress;
import simpipe.base.support.peers.PeerManager;
import simpipe.tcp.support.SimPipeTCPSessionImpl;

/**
 * Connects to {@link IoHandler}s which is bound on the specified {@link SimPipeAddress}.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 587373 $, $Date: 2007-10-23 11:54:05 +0900 ( 2007) $
 */
public class SimPipeConnector extends simpipe.base.SimPipeConnector {

	private static BandwidthDistributionGenerator bwGen;
	
	@Override
	protected SimPipeSessionImpl createSession(SocketAddress localAddress, IoHandler handler, IoServiceConfig config, SimPipe entry) {
		return new SimPipeTCPSessionImpl(this, config, getListeners(), localAddress /* new AnonymousSocketAddress() */, handler, entry);
	}
	

	public synchronized ConnectFuture connect(final SocketAddress address, SocketAddress localAddress, final IoHandler handler, IoServiceConfig config) {

		PeerManager.setHandlingTCP(true);

		String peerName = Reflection.getExecutingGroup();

		// NodeBandwith bw = null;
		//
		// try {
		// bw = getBwGen().getNewNodeBandwith();
		// } catch (Exception e) {
		// System.err.println("Can't create the bandwidth generator");
		// }
		//
		// final Node peer;

		// if (peerName.contains("Source")) {
		// peer = new TCPPeer(nextId(), 0, Integer.parseInt(Conf.SOURCES_BANDWIDTH));
		// } else {
		// peer = new TCPPeer(nextId(), bw.getDown(), bw.getUp());
		// }

		long upBw=0;
		long downBw=0;

		// if (peerName.contains("Source")) {
		// upBw = Integer.parseInt(""+Conf.SOURCES_BANDWIDTH);
		// downBw = 0;
		// } else {
		// upBw = bw.getUp();
		// downBw = bw.getDown();
		// }

		try {
			PeerManager.createTCPPeer(downBw, upBw);
		} catch (final Exception e) {
			P.rint("Can't add node");
		}

		return super.connect(address, localAddress, handler, config);
	}

	public void unbindAll() {
		PeerManager.setHandlingTCP(true);
		super.unbindAll();
	}

	private static BandwidthDistributionGenerator getBwGen() throws Exception {
		if (bwGen == null)
			bwGen = new BandwidthDistributionGenerator(Conf.PEERS_BANDWIDTH);

		return bwGen;
	}
}