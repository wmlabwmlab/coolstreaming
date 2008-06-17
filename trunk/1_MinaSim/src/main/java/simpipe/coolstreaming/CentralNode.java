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
package simpipe.coolstreaming;
import java.net.SocketAddress;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.Timer;

public class CentralNode  {
    
    Membership members;
    ServerProtocol protocol;
    private int mSize = 20;
    int deleteTime=60000; // time to delete the members from the mCache
    public CentralNode(SocketAddress serverAddress){
    	protocol = new ServerProtocol(serverAddress,this);
    	members=new Membership(mSize,Constants.SERVER_PORT,deleteTime);
    }
}