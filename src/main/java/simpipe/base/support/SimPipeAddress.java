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
package simpipe.base.support;

import java.net.SocketAddress;

/**
 * A {@link SocketAddress} which represents in-VM pipe port number.
 *
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 587373 $, $Date: 2007-10-23 11:54:05 +0900 (화, 23 10월 2007) $
 */
public class SimPipeAddress extends SocketAddress implements Comparable<SimPipeAddress> {
    private static final long serialVersionUID = 3257844376976830515L;

    private final int port;

	private String ownerGroup;

    /**
     * Creates a new instance with the specifid port number.
     */
    public SimPipeAddress(int port) {
        this.port = port;
    }

    /**
     * Returns the port number.
     */
    public int getPort() {
        return port;
    }

    @Override
	public int hashCode() {
        return port;
    }

    @Override
	public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (o instanceof SimPipeAddress) {
            SimPipeAddress that = (SimPipeAddress) o;
            return port == that.port;
        }

        return false;
    }

    public int compareTo(SimPipeAddress o) {
        return port - o.port;
    }

    @Override
	public String toString() {
    	if(ownerGroup==null)
    		return port+"";
    	else
    		return port+" "+ownerGroup;
    }

	public void setOwnerGroup(String group) {
		ownerGroup = group;		
	}
	
	public String getOwnerGroup()
	{
		return ownerGroup;
	}
}