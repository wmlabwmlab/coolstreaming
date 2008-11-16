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
package simpipe.base;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.ExceptionMonitor;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.IoSessionConfig;
import org.apache.mina.common.support.AbstractIoFilterChain;
import org.apache.mina.common.support.BaseIoConnector;
import org.apache.mina.common.support.BaseIoConnectorConfig;
import org.apache.mina.common.support.BaseIoSessionConfig;
import org.apache.mina.common.support.DefaultConnectFuture;

import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.utils.P;
import simpipe.base.support.SimPipeAddress;
import simpipe.base.support.peers.PeerManager;

/**
 * Connects to {@link IoHandler}s which is bound on the specified {@link SimPipeAddress}.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 587373 $, $Date: 2007-10-23 11:54:05 +0900 ( 2007) $
 */
public class SimPipeConnector extends BaseIoConnector {

	protected final HashSet<Integer> myBoundedPorts = new HashSet<Integer>();

	protected static int nextId = 10;

	private static final IoSessionConfig CONFIG = new BaseIoSessionConfig() {
	};

	private final IoServiceConfig defaultConfig = new BaseIoConnectorConfig() {
		public IoSessionConfig getSessionConfig() {
			return CONFIG;
		}
	};

	/**
	 * Creates a new instance.
	 */
	public SimPipeConnector() {
	}

	public ConnectFuture connect(final SocketAddress address, final IoHandler handler, final IoServiceConfig config) {
		return connect(address, null, handler, config);
	}

	public synchronized ConnectFuture connect(final SocketAddress address, SocketAddress localAddress, final IoHandler handler, IoServiceConfig config) {
		if (address == null)
			throw new NullPointerException("address");
		if (handler == null)
			throw new NullPointerException("handler");
		if (!(address instanceof SimPipeAddress))
			throw new IllegalArgumentException("address must be SimPipeAddress.");

		if (config == null) {
			config = getDefaultConfig();
		}

		final SimPipe entry = PeerManager.getBinding((SimPipeAddress) address);
		if (entry == null) {
			return DefaultConnectFuture.newFailedFuture(new IOException("Endpoint unavailable: " + address));
		}

		if (localAddress == null) {
			localAddress = new SimPipeAddress(nextId++);
			((SimPipeAddress) localAddress).setOwnerGroup(Reflection.getExecutingGroup());
		}

		if (((SimPipeAddress) localAddress).getOwnerGroup() == null) {
			((SimPipeAddress) localAddress).setOwnerGroup(Reflection.getExecutingGroup());
		}
		final SimPipeSessionImpl localSession = createSession(localAddress, handler, config, entry);
		// final DefaultConnectFuture future = new DefaultConnectFuture();

		myBoundedPorts.add(((SimPipeAddress) localAddress).getPort());

		final ConnectFuture future = new SimPipeConnectFuture(localSession);

		future.setSession(localSession);

		final InetSocketAddress x;

		// ---------------------------------------------------------------
		// MINA SIM
		// ---------------------------------------------------------------
		// final int accAddress = ((SimPipeAddress) address).getPort();

		final int connAddress = ((SimPipeAddress) localAddress).getPort();
		// final Peer minaPeerAcc = new Peer(accAddress, 1, 1);

		// final Peer minaPeerConn = new Peer(connAddress, 100000, 100000);
		// try {
		// PeerManager.createTCPPeer();
		// // PeerManager.bindConnector(connAddress);
		// // if (Network.getInstance().contains(connAddress))
		// // return DefaultConnectFuture.newFailedFuture(new IOException("Trying to reconnect on the same local port: " + address));
		// // else
		// // Network.getInstance().add(minaPeerConn);
		// // if (!Network.getInstance().contains(accAddress))
		// // Network.getInstance().add(minaPeerAcc);
		// } catch (final Exception e) {
		// P.rint("Can't add node");
		// }
		// ---------------------------------------------------------------
		// ---------------------------------------------------------------

		// initialize connector session
		try {
			// -------- Sameh: these are turn off for now, may be we will need
			// to use them later
			// IoFilterChain filterChain = localSession.getFilterChain();
			// this.getFilterChainBuilder().buildFilterChain(filterChain);
			// config.getFilterChainBuilder().buildFilterChain(filterChain);

			// -------- Sameh: this one has to remain closed!!
			// config.getThreadModel().buildFilterChain(filterChain);

			// The following sentences don't throw any exceptions.
			localSession.setAttribute(AbstractIoFilterChain.CONNECT_FUTURE, future);
			getListeners().fireSessionCreated(localSession);
			SimPipeIdleStatusChecker.getInstance().addSession(localSession);
		} catch (final Throwable t) {
			future.setException(t);
			return future;
		}

		// initialize acceptor session
		final SimPipeSessionImpl remoteSession = localSession.getRemoteSession();
		try {
			// //IoFilterChain filterChain = remoteSession.getFilterChain();
			// //entry.getAcceptor().getFilterChainBuilder().buildFilterChain(
			// // filterChain);
			// //entry.getConfig().getFilterChainBuilder().buildFilterChain(
			// // filterChain);
			// //entry.getConfig().getThreadModel().buildFilterChain(filterChain);

			// // The following sentences don't throw any exceptions.
			entry.getListeners().fireSessionCreated(remoteSession);
			SimPipeIdleStatusChecker.getInstance().addSession(remoteSession);
		} catch (final Throwable t) {
			ExceptionMonitor.getInstance().exceptionCaught(t);
			remoteSession.close();
		}

		// // Start chains, and then allow and messages read/written to be processed. This is to ensure that
		// // sessionOpened gets received before a messageReceived
		// ((SimPipeFilterChain) localSession.getFilterChain()).start();
		// ((SimPipeFilterChain) remoteSession.getFilterChain()).start();

		return future;
	}

	protected SimPipeSessionImpl createSession(SocketAddress localAddress, final IoHandler handler, IoServiceConfig config, final SimPipe entry) {
		return new SimPipeSessionImpl(this, config, getListeners(), localAddress /* new AnonymousSocketAddress() */, handler, entry);
	}

	public IoServiceConfig getDefaultConfig() {
		return defaultConfig;
	}

	private void unbind(final int port) {
		SimPipe pipe;

		synchronized (PeerManager.class) {
			if (PeerManager.getBinding(new SimPipeAddress(port)) == null) {
				throw new IllegalArgumentException("Address not bound: " + port);
			}
			pipe = PeerManager.getBinding(new SimPipeAddress(port));
			PeerManager.unbind(port, true);
		}

		getListeners().fireServiceDeactivated(this, pipe.getAddress(), pipe.getHandler(), pipe.getConfig());
	}

	public void unbindAll() {
		// PeerManager.unbindAll();
		for (Integer port : myBoundedPorts) {
			unbind(port);
		}
		// synchronized (boundHandlers) {
		// for (final SocketAddress address : new ArrayList<SocketAddress>(boundHandlers.keySet())) {
		// unbind(address);
		// }
		// }
	}
}