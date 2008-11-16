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

import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashSet;

import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.IoSessionConfig;
import org.apache.mina.common.support.BaseIoAcceptor;
import org.apache.mina.common.support.BaseIoAcceptorConfig;
import org.apache.mina.common.support.BaseIoSessionConfig;

import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.network.tcp.bw.gen.BandwidthDistributionGenerator;
import se.peertv.peertvsim.network.tcp.bw.gen.BandwidthDistributionGenerator.NodeBandwith;
import simpipe.base.SimPipe;
import simpipe.base.support.SimPipeAddress;
import simpipe.base.support.peers.PeerManager;

/**
 * Binds the specified {@link IoHandler} to the specified {@link SimPipeAddress}.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 587373 $, $Date: 2007-10-23 11:54:05 +0900 $
 */
public class SimPipeAcceptor extends simpipe.base.SimPipeAcceptor {
	private static BandwidthDistributionGenerator bwGen;

	public void bind(SocketAddress address, final IoHandler handler, IoServiceConfig config) throws IOException {

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
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Now that we created the peer we can proceed to execute the base acceptor
		super.bind(address, handler, config);

	}

	public void unbind(final SocketAddress address) {
		PeerManager.setHandlingTCP(true);
		super.unbind(address);
	}

	private static BandwidthDistributionGenerator getBwGen() throws Exception {
		if (bwGen == null)
			bwGen = new BandwidthDistributionGenerator(Conf.PEERS_BANDWIDTH);

		return bwGen;
	}
}
