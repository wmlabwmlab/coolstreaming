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
package simpipe;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

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
import org.apache.mina.util.AnonymousSocketAddress;

import se.peertv.peertvsim.network.Network;
import se.peertv.peertvsim.utils.P;
import simpipe.support.Peer;
import simpipe.support.SimPipe;
import simpipe.support.SimPipeFilterChain;
import simpipe.support.SimPipeSessionImpl;

/**
 * Connects to {@link IoHandler}s which is bound on the specified
 * {@link SimPipeAddress}.
 *
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 587373 $, $Date: 2007-10-23 11:54:05 +0900 (화, 23 10월 2007) $
 */
public class SimPipeConnector extends BaseIoConnector {
	
	static int nextId =10; 

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

	public ConnectFuture connect(SocketAddress address, IoHandler handler,
			IoServiceConfig config) {
		return connect(address, null, handler, config);
	}

	public ConnectFuture connect(SocketAddress address,
			SocketAddress localAddress, IoHandler handler,
			IoServiceConfig config) {
		if (address == null)
			throw new NullPointerException("address");
		if (handler == null)
			throw new NullPointerException("handler");
		if (!(address instanceof SimPipeAddress))
			throw new IllegalArgumentException("address must be SimPipeAddress.");

		if (config == null) {
			config = getDefaultConfig();
		}

		SimPipe entry = SimPipeAcceptor.boundHandlers.get(address);
		if (entry == null) {
			return DefaultConnectFuture.newFailedFuture(new IOException(
					"Endpoint unavailable: " + address));
		}

		localAddress =   new SimPipeAddress(nextId++);
		DefaultConnectFuture future = new DefaultConnectFuture();
		SimPipeSessionImpl localSession = new SimPipeSessionImpl(this,
				config,getListeners(),
				localAddress /*new AnonymousSocketAddress()*/,
				handler,
				entry);
		
		
		InetSocketAddress x;
		
		
        //---------------------------------------------------------------
        // MINA SIM
        //---------------------------------------------------------------
		int accAddress = ((SimPipeAddress)address).getPort();
		int connAddress = ((SimPipeAddress)localAddress).getPort();
        Peer minaPeerAcc = new Peer(accAddress , 1, 1);
        Peer minaPeerConn = new Peer(connAddress , 1, 1);
        try {
        	if (!Network.getInstance().contains(connAddress))
        		Network.getInstance().add(minaPeerConn);        	
        	if (!Network.getInstance().contains(accAddress))
        		Network.getInstance().add(minaPeerAcc);
		} catch (Exception e) {
			P.rint("Can't add node");
		}
        //---------------------------------------------------------------
        //---------------------------------------------------------------                

		// initialize connector session
		try {
			//-------- Sameh: these are turn off for now, may be we will need
			//                to use them later
			//IoFilterChain filterChain = localSession.getFilterChain();
			//this.getFilterChainBuilder().buildFilterChain(filterChain);
			//config.getFilterChainBuilder().buildFilterChain(filterChain);

			//-------- Sameh: this one has to remain closed!!
			//config.getThreadModel().buildFilterChain(filterChain);

			// The following sentences don't throw any exceptions.
			localSession.setAttribute(AbstractIoFilterChain.CONNECT_FUTURE, future);
			getListeners().fireSessionCreated(localSession);
			//SimPipeIdleStatusChecker.getInstance().addSession(localSession);
		} catch (Throwable t) {
			future.setException(t);
			return future;
		}

		// initialize acceptor session
		SimPipeSessionImpl remoteSession = localSession.getRemoteSession();
		try {
//			//IoFilterChain filterChain = remoteSession.getFilterChain();
//			//entry.getAcceptor().getFilterChainBuilder().buildFilterChain(
//			//        filterChain);
//			//entry.getConfig().getFilterChainBuilder().buildFilterChain(
//			//        filterChain);
//			//entry.getConfig().getThreadModel().buildFilterChain(filterChain);

//			// The following sentences don't throw any exceptions.
			entry.getListeners().fireSessionCreated(remoteSession);
//			SimPipeIdleStatusChecker.getInstance().addSession(remoteSession);
		} catch (Throwable t) {
			ExceptionMonitor.getInstance().exceptionCaught(t);
			remoteSession.close();
		}


//		// Start chains, and then allow and messages read/written to be processed. This is to ensure that
//		// sessionOpened gets received before a messageReceived
		//((SimPipeFilterChain) localSession.getFilterChain()).start();
		//((SimPipeFilterChain) remoteSession.getFilterChain()).start();

		return future;
	}

	public IoServiceConfig getDefaultConfig() {
		return defaultConfig;
	}
}