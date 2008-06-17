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

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.support.BaseIoConnector;

import se.peertv.peertvsim.core.Callbackable;
import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.Timer;
import simpipe.SimPipeAcceptor;
import simpipe.SimPipeAddress;
import simpipe.SimPipeConnector;

public class PeerNode extends Node {
    
	boolean searching=true;
    PeerProtocol protocol;
    int time=0;
    boolean requesting=false;
    
    PeerNode(boolean source,SocketAddress serverAdderess){
    	isSource=source;
		protocol = new PeerProtocol(serverAdderess,this); 
    }
    
    public void reboot(int dull){ 
    	
    	if(this.searching||partners.getLength()==0){
    		this.deputyHops=4;
    		protocol.connectTo(0);	
    	}
    	else{
    		partners.clearPartners();
    		if(requesting){
    			requesting=false;
    			int diff=(scheduler.timeSlot - scheduler.startTime)/1000;
    			
    			// is the movie finished
    			if(diff >= videoSize)
    				return;
    			
    			BitField field=scheduler.beginscheduling();
    			
    			int loc = 0;
    			for(int i=0;i<windowSize;i++){
    				// is the movie finished
    				if(diff+i>=videoSize)
    					return;
    				
    				if(scheduler.wholeBits[diff+i]==1 || field.bits[i]==0)
    					continue;

    				loc = partners.getIndex(field.bits[i]);
    				if(loc==-1)
    					continue;
    				int sum=diff+i;
    				partners.pCache[loc].session.write("x"+sum);
    			}
    		}
    	}
    	
    	try{
			new Timer(bootTime,this,"reboot",0);
		}
    		catch(Exception e){
		}
		
    }
    
    void beginSceduling(){
    	try{
    		if(!isSource){
    			new Timer(scheduler.exchangeTime,scheduler,"exchangeBM",0);
    		}
    	}
		catch(Exception e){
		}
    }
    
 	public void initalizeNode() throws Exception{
    	
    	//This is the constructor of the PeerNode , its constructor cant be fired when initializing the object , but in stead I waited till first session is created (bootStraping flag) to set some settings
		members=new Membership(mSize,port,deleteTime);
		partners=new Partnership(pSize,port,windowSize,defaultBandwidth); 
		scheduler = new Schedule(this,100);
		gossip= new Gossip(this);
		bandwidth=(int)((Math.random()*512)+100);
		new Timer(bootTime,this,"reboot",0);
    }

    public void sessionClosed() {
    	partners.clearPartners();
     }
 }