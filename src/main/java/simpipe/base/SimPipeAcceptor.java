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
import java.net.SocketAddress;
import java.util.HashSet;

import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.IoSessionConfig;
import org.apache.mina.common.support.BaseIoAcceptor;
import org.apache.mina.common.support.BaseIoAcceptorConfig;
import org.apache.mina.common.support.BaseIoSessionConfig;

import se.peertv.peertvsim.core.Reflection;
import simpipe.base.support.SimPipeAddress;
import simpipe.base.support.peers.PeerManager;

/**
 * Binds the specified {@link IoHandler} to the specified {@link SimPipeAddress}.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 587373 $, $Date: 2007-10-23 11:54:05 +0900 $
 */
public class SimPipeAcceptor extends BaseIoAcceptor {
	// static final Map<Integer, SimPipe> boundHandlers = new HashMap<Integer, SimPipe>();
	protected final HashSet<Integer> myBoundedPorts = new HashSet<Integer>();

	private static final IoSessionConfig CONFIG = new BaseIoSessionConfig() {
	};

	private final IoServiceConfig defaultConfig = new BaseIoAcceptorConfig() {
		public IoSessionConfig getSessionConfig() {
			return CONFIG;
		}
	};

	public void bind(SocketAddress address, final IoHandler handler, IoServiceConfig config) throws IOException {
		if (handler == null)
			throw new NullPointerException("handler");
		if (address != null && !(address instanceof SimPipeAddress))
			throw new IllegalArgumentException("address must be SimPipeAddress.");

		if (config == null) {
			config = getDefaultConfig();
		}

		((SimPipeAddress) address).setOwnerGroup(Reflection.getExecutingGroup());

		int port = ((SimPipeAddress) address).getPort();

		synchronized (PeerManager.class) {
			if (address == null || (port == 0)) {
				for (int i = 1; i < Integer.MAX_VALUE; i++) {
					address = new SimPipeAddress(i);
					if (PeerManager.getBinding((SimPipeAddress) address) == null) {
						break;
					}
				}
			} else if (PeerManager.getBinding(new SimPipeAddress(port)) != null) {
				throw new IOException("Address already bound: " + address);
			}

			try {
				PeerManager.bind(new SimPipe(this, (SimPipeAddress) address, handler, config, getListeners()));
			} catch (Exception e) {
				throw new IOException("Error creating and/or binding the peer", e);
			}
		}

		myBoundedPorts.add(port);

		getListeners().fireServiceActivated(this, address, handler, config);
	}

	public void unbind(final SocketAddress address) {
		if (address == null)
			throw new NullPointerException("address");
		int port = ((SimPipeAddress) address).getPort();
		unbind(port);
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

		// getListeners().fireServiceDeactivated(this, pipe.getAddress(), pipe.getHandler(), pipe.getConfig());
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

	public IoServiceConfig getDefaultConfig() {
		return defaultConfig;
	}
}
