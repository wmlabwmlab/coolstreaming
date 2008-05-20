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
import org.apache.mina.common.IoSession;

import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.Timer;

public class CentralNode extends Node {
    
	private final int id = 0;
    public static int PORT = 30000;
    boolean firstConn=true;
    
    public void delete(int port){
    	deletePartner(port);
    	//System.err.println("id("+this.PORT+") : Member("+port+") timeout ");
    }
    
    @Override
	public void sessionCreated(IoSession session) throws Exception {
    	port=PORT;
    	System.out.println("Server says : \" new peer number "+session.getRemoteAddress().toString()+" Created !! \"");	
    }

    public void sessionClosed(IoSession session) {
        //System.out.println("SERVER : ");
        //for(int i=0;i<mCache.length;i++)
        //	System.out.println("=> "+mCache[i]);
    }

    public void messageReceived(IoSession session, Object message) {
    	String msg=(String)message;
        String msgPart2=msg.substring(1);
    	if(msg.charAt(0)=='c'){
    		    int client=Integer.parseInt(session.getRemoteAddress().toString());
    		    int port=this.getAnotherDeputy(client);
    			addMember(client);
    			session.write("d"+port);
    			
    	}
    	
    }

    public void sessionOpened(IoSession session) {
	 }
    
    public void messageSent(IoSession session, Object message) {
     }

    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        session.close();
    }
}