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
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.IoSessionConfig;
import org.apache.mina.common.support.BaseIoAcceptor;
import org.apache.mina.common.support.BaseIoAcceptorConfig;
import org.apache.mina.common.support.BaseIoSessionConfig;

import se.peertv.peertvsim.network.Network;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.utils.P;
import simpipe.support.Peer;
import simpipe.support.SimPipe;

/**
 * Binds the specified {@link IoHandler} to the specified
 * {@link SimPipeAddress}.
 *
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 587373 $, $Date: 2007-10-23 11:54:05 +0900 (화, 23 10월 2007) $
 */
public class SimPipeAcceptor extends BaseIoAcceptor {
    static final Map<SocketAddress, SimPipe> boundHandlers = new HashMap<SocketAddress, SimPipe>();

    private static final IoSessionConfig CONFIG = new BaseIoSessionConfig() {
    };

    private final IoServiceConfig defaultConfig = new BaseIoAcceptorConfig() {
        public IoSessionConfig getSessionConfig() {
            return CONFIG;
        }
    };

    public void bind(SocketAddress address, IoHandler handler,
            IoServiceConfig config) throws IOException {
        if (handler == null)
            throw new NullPointerException("handler");
        if (address != null && !(address instanceof SimPipeAddress))
            throw new IllegalArgumentException("address must be SimPipeAddress.");

        if (config == null) {
            config = getDefaultConfig();
        }

        synchronized (boundHandlers) {
            if (address == null || ((SimPipeAddress) address).getPort() == 0) {
                for (int i = 1; i < Integer.MAX_VALUE; i++) {
                    address = new SimPipeAddress(i);
                    if (!boundHandlers.containsKey(address)) {
                        break;
                    }
                }
            } else if (boundHandlers.containsKey(address)) {
                throw new IOException("Address already bound: " + address);
            }

     
           
            boundHandlers.put(address, new SimPipe(this,
                   (SimPipeAddress) address, handler, config, getListeners()));
            
            			
        }
        
        
        getListeners().fireServiceActivated(this, address, handler, config);
    }

    public void unbind(SocketAddress address) {
        if (address == null)
            throw new NullPointerException("address");

        SimPipe pipe;
        synchronized (boundHandlers) {
            if (!boundHandlers.containsKey(address)) {
                throw new IllegalArgumentException("Address not bound: "
                        + address);
            }

            pipe = boundHandlers.remove(address);
        }

        getListeners().fireServiceDeactivated(this, pipe.getAddress(),
                pipe.getHandler(), pipe.getConfig());
    }

    public void unbindAll() {
        synchronized (boundHandlers) {
            for (SocketAddress address : new ArrayList<SocketAddress>(
                    boundHandlers.keySet())) {
                unbind(address);
            }
        }
    }

    public IoServiceConfig getDefaultConfig() {
        return defaultConfig;
    }
}
