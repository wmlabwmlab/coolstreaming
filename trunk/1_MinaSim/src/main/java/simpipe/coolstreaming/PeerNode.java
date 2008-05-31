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
    boolean bootStraping=true;
    Protocol protocol;
    
    
    PeerNode(boolean source){
    	isSource=source;
    }
    
    public void delete(int port){
    	deleteMember(port);
    }
   
 // the gossiping timer's firing function
    public void regossip(int dull){  
    		try {
    			Timer t = new Timer(gossipTime,this,"regossip",0);
    		} catch (Exception e) {
			e.printStackTrace();
		}
    	gossip.initiate(hops);
    }
    
 // the BitMap's timer's firing function 
    public void exchangeBM(int dull){ 
    	
    	try {
    			Timer t = new Timer(exchangeTime,this,"exchangeBM",0);
    		} catch (Exception e) {
			e.printStackTrace();
		}
    	int milliesNow=(int)Scheduler.getInstance().now;	
    	int secondNow=milliesNow/1000;
    	int time=milliesNow;
    	for(int i=0;i<this.videoSize;i++)
    	if(scheduler.wholeBits[i]==0&&scheduler.deadLine[i]<secondNow){
    	time=Schedule.startTime+(i*1000);
    	break;
    	}
    	
    	for(int i=0;i<pSize;i++)
    		if(partners.pCache[i]!=null){
    		partners.pCache[i].session.write("r"+time);
    		
    		}
    }
    
    void beginSceduling(){
    	try{
    		if(!isSource){
    		Timer t = new Timer(exchangeTime,this,"exchangeBM",0);
    		}
    	}
		catch(Exception e){
		}
    }
    
    @Override
	public void sessionCreated(IoSession session) throws Exception {
    	
    	//This is the constructor of the PeerNode , its constructor cant be fired when initializing the object , but in stead I waited till first session is created (bootStraping flag) to set some settings
    	if(bootStraping){
    		this.port=Integer.parseInt(session.getLocalAddress().toString());
    		partners=new Partnership(pSize,port,windowSize,defaultBandwidth); 
    		bandwidth=(int)((Math.random()*512)+100);
    		scheduler = new Schedule(this);
    		bootStraping=false;
    		protocol = new Protocol(this); 
    		gossip= new Gossip(this);
    		Timer t = new Timer(gossipTime,this,"regossip",0);
    		gossip.initiate(hops);
    		int listenPort=Integer.parseInt(session.getLocalAddress().toString())+CentralNode.PORT;
    		IoServiceConfig config;
    		IoAcceptor acceptor;
    		acceptor = new SimPipeAcceptor();
    		config = acceptor.getDefaultConfig();
    		SocketAddress serverAddress = new SimPipeAddress(listenPort);
    		acceptor.bind(serverAddress, this, config); 
    	}
    }

	public void sessionOpened(IoSession session) {
		
		if(Integer.parseInt(session.getLocalAddress().toString())>CentralNode.PORT)
		{
			return;
		}
	
		if(searching){
			deputyHops--;
			session.write("c"+deputyHops+"-"+this.port);
			
		}
		else{
			if(partners.getLength()==pSize)
				return;
			boolean added=partners.addPartner(Integer.parseInt(session.getRemoteAddress().toString()), session);
			if(added)
			session.write("a"+this.port);
			
		}
		
    }

	
    public void sessionClosed(IoSession session) {
    	partners.clearPartners();
     }

    public void messageReceived(IoSession session, Object message) {
    	//System.err.println("[T="+Scheduler.getInstance().now+"]Peer "+session.getLocalAddress().toString()+" says : \" received from ( "+session.getRemoteAddress().toString()+") : "+ message);
        String msg=(String)message;
        String msgPart2=msg.substring(1);
        
        if(msg.charAt(0)=='d'){ // your deputy is X 
        	protocol.deputyMessage(msgPart2, session);
        }
        else if(msg.charAt(0)=='c'){ //I want to connect to you , my id is X and I've jumped M times to get to you 
        	protocol.connectMessage(msgPart2, session);
        }
        else if(msg.charAt(0)=='p'){ //I accept you as a partner of mine and you can have another partners which are [A-B-C-....]
            beginSceduling();
        	protocol.partnersMessage(msgPart2, session);	
        }
        if(msg.charAt(0)=='a'){ //I am already in the network but I want you to be my friend
        	if(partners.getLength()!=pSize)
        		protocol.acceptMessage(msgPart2, session);
        	else{
        		session.write("t"+this.port);
        		session.close();
        	}
        }
        else if(msg.charAt(0)=='m'){ //I want from you to send me your bandwidth in order to use it while calculating
        	protocol.sendBandwidth(session);
        }
        else if(msg.charAt(0)=='n'){// My bandwidth is B
        	protocol.receiveBandwidth(msgPart2,session);
        }
        else if(msg.charAt(0)=='t'){ //I am leaving now the network
        	protocol.terminateConnectionMessage(msgPart2, session);
        }
        else if(msg.charAt(0)=='g'){ //I am gossiping to node P and this message jumped N times
        	protocol.gossipMessage(msgPart2);
        }
        else if(msg.charAt(0)=='r'){ // I want from you the buffer map that begins from time T
        	protocol.requestBitMapMessage(msgPart2, session);
        }
        else if(msg.charAt(0)=='b'){ // Here is my buffer map [01111100001......]
        	protocol.bitMapMessage(msgPart2);
        }
        else if(msg.charAt(0)=='x'){ // I want from you to send me segment X
        	protocol.pingMessage(msgPart2, session);
        }
        else if(msg.charAt(0)=='y'){ // Here are the segment that you have requested
        	protocol.pongMessage(msgPart2);
        }
        
    }

    
    public void messageSent(IoSession session, Object message) {
        
    }
    
    // this function is used to make my peernode connect to another peer
    void connectTo(int port){
    	BaseIoConnector connector= new SimPipeConnector();
    	SocketAddress serverPort= new SimPipeAddress(CentralNode.PORT+port);
    	ConnectFuture future = connector.connect(serverPort,this);
    }

    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        session.close();
    }
   
}