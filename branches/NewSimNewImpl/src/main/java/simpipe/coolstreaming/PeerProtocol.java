package simpipe.coolstreaming;

import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.support.BaseIoConnector;

import se.peertv.peertvsim.SimulableSystem;

import simpipe.tcp.SimPipeAcceptor;
import simpipe.base.support.SimPipeAddress;
import simpipe.tcp.SimPipeConnector;

public class PeerProtocol extends IoHandlerAdapter{

	IoAcceptor acceptor;
	//BaseIoConnector connector;
	
	private PeerNode node;
    private boolean bootStraping=true;
    public int committed=0;
	
	public PeerProtocol(SocketAddress serverAddress,PeerNode node){
		this.node=node;
		
		BaseIoConnector connector = new SimPipeConnector();
		connector.connect(serverAddress, this);
	}

	void disconnect(){
		acceptor.unbindAll();
	}
	
	public void sessionCreated(IoSession session) throws Exception {
		
    	if(bootStraping){
//			System.out.println("node: "+node.getPort()+" is sessionCreated......with bootstraping = "+bootStraping);
			bootStraping=false;
    		node.initalizeNode();
			//node.port=Integer.parseInt(session.getLocalAddress().toString());
			node.serverBond=session;
    		int listenPort=node.port+ Constants.SERVER_PORT;
			IoServiceConfig config;
			acceptor = new SimPipeAcceptor();
			config = acceptor.getDefaultConfig();
			SocketAddress serverAddress = new SimPipeAddress(listenPort);
			acceptor.bind(serverAddress, this, config); 
    	}
	}
	
	public void sessionOpened(IoSession session) {
		
		//is someone connecting to my server or is pCache full
		if((((SimPipeAddress)session.getLocalAddress()).getPort() > Constants.SERVER_PORT)||(node.partners.getLength()==node.pSize))
			return;

		// is the node still searching for a deputy?
		if(node.searching){
			node.deputyHops--;
			session.write(""+Constants.CONNECTION_REQUEST+node.deputyHops+"-"+node.port);
			return;
		}

		session.write(""+Constants.PARTNERSHIP_REQUEST+node.port);
		node.committed.add(session);
		ControlRoom.counts++;
		committed++;

	}

	
    public void sessionClosed(IoSession session) {
    	node.sessionClosed();
     }

    public synchronized void messageReceived(IoSession session, Object message) {

        String msg=(String)message;
        String msgPart2=msg.substring(1);
        char operation = msg.charAt(0);
        
        switch(operation){
	        // your deputy is X
	        case Constants.DEPUTY_MESSAGE :
	        	ControlRoom.logger.debug("me "+node.port+" got "+msg+" , remainder"+node.deputyHops);
	        	//System.out.println("me "+node.port+" got "+msg+" , remainder"+node.deputyHops);
	        	deputyMessage(msgPart2, session);
	        	break;
	        
	       	//I want to connect to you , my id is X and I've jumped M times to get to you        	
	        case Constants.CONNECTION_REQUEST : 
	        	connectMessage(msgPart2, session);
	        	break;
	        
	       	//I accept you as a partner of mine and you can have another partners which are [A-B-C-....]        	
	        case Constants.PARTNERSHIP_ACCEPTANCE :
	        	ControlRoom.logger.debug("me "+node.port+" got "+msg+" , remainder"+node.deputyHops);
	        	//System.out.println("me "+node.port+" got "+msg+" , remainder"+node.deputyHops);
	        	node.joinTime=(int)SimulableSystem.currentTimeMillis();
	        	node.beginSceduling();
	        	partnersMessage(msgPart2, session);	
	        	break;
	        
	        //I am already in the network but I want you to be my friend
	        case Constants.PARTNERSHIP_REQUEST :
		    	handlePartnershipRequest(session, msgPart2);
	        	break;
	        	
	        case Constants.PARTNERSHIP_RESPONSE :
	        	receivePartnershipResponse(msgPart2, session);
	        	break;
	        	
	        //I want from you to send me your bandwidth in order to use it while calculating
	        case Constants.BANDWIDTH_REQUEST :
	        	sendBandwidth(session);
	        	break;
	        
	        // My bandwidth is B
	        case Constants.BANDWIDTH_RESPONSE :
	        	receiveBandwidth(msgPart2,session);
	        	break;
	        
	        //I am leaving now the network
	        case Constants.LEAVE_NETWORK :
	        	terminateConnection(msgPart2, session);
	        	break;
	 
	        //I am gossiping to node P and this message jumped N times
	        case Constants.GOSSIPING :
	        	handleGossipingRequest(msgPart2);
	        	break;
	        
	        // I want from you the buffer map that begins from time T
	        case Constants.BUFFERMAP_REQUEST :
	        	handleBitMapRequest(msgPart2, session);
	        	break;
	        
	        // Here is my buffer map [01111100001......]
	        case Constants.BUFFERMAP_RESPONSE :
	        	getBitMapResponse(msgPart2);
	        	break;
	        
	        // I want from you to send me segment X
	        case Constants.SEGMENT_REQUEST :
	        	handlePingMessage(msgPart2, session);
	        	break;
	        
	        // Here are the segment that you have requested
	        case Constants.SEGMENT_RESPONSE :
	        	handlePongMessage(msgPart2);
	        	break;
	        
	        default:
	        	System.err.println("MINE-------"+message);
	        	System.exit(0);
	        }
    }

	private void handlePartnershipRequest(IoSession session, String msgPart2) {
		ControlRoom.counts--;
		int destination = Integer.parseInt(msgPart2);
		node.members.addMember(destination);
		boolean added = node.partners.addPartner(destination, session,node.isTracker);
		if(added){
			session.write(""+Constants.PARTNERSHIP_RESPONSE+node.port+Constants.MESSAGE_SEPARATOR+Constants.REQUEST_ACCEPTED);
			session.write(Constants.BANDWIDTH_REQUEST+"");
			return;
		}
		else{
			session.write(""+Constants.PARTNERSHIP_RESPONSE+node.port+Constants.MESSAGE_SEPARATOR+Constants.REQUEST_REJECTED);
			session.close();
		}
	}

    
    // this function is used to make my peernode connect to another peer
    void connectTo(int port){
    	SocketAddress serverPort= new SimPipeAddress(Constants.SERVER_PORT+port);
    	BaseIoConnector connector = new SimPipeConnector(); 
    	connector.connect(serverPort,this);
    }

    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        session.close();
    }    
    
	public void deputyMessage(String msgPart2,IoSession session){
		if(((SimPipeAddress)session.getRemoteAddress()).getPort() != Constants.SERVER_PORT)
			session.close();
		String myPort=String.valueOf(node.port);
		if(!msgPart2.equals(myPort)){
			connectTo(Integer.parseInt(msgPart2));
			node.members.addMember(Integer.parseInt(msgPart2));
		}
    	else {
			node.beginSceduling();
			node.joinTime=(int)SimulableSystem.currentTimeMillis();
			node.searching=false;
		}
	}
	
	public void connectMessage(String msgPart2,IoSession session){
		String []parameters=msgPart2.split(Constants.MESSAGE_SEPARATOR);
		int destination=Integer.parseInt(parameters[1]);

		if(node.partners.getLength()==node.pSize){
			int hops=0;
			try{
				hops=Integer.parseInt(parameters[0]);
			}
			catch(Exception e){
				
			}
			
			if(hops==0&&node.partners.getLength()==node.pSize){
				System.err.println(destination+" is dying");
				node.partners.forceAddPartner(destination,session);
				session.write(Constants.BANDWIDTH_REQUEST+"");
    			node.members.addMember(destination);
    			String ports=node.partners.getPartners();
    			session.write(""+Constants.PARTNERSHIP_ACCEPTANCE+node.port+Constants.MESSAGE_SEPARATOR+ports);
    		}
    		else{
    			int port=node.members.getAnotherDeputy(destination);
    			session.write(""+Constants.DEPUTY_MESSAGE+port);
    		}
    	}
    	else{
			if(node.partners.addPartner(destination,session,node.isTracker)){
				node.members.addMember(destination);
				session.write(Constants.BANDWIDTH_REQUEST+"");
				String ports=node.partners.getPartners();
				
				session.write(""+Constants.PARTNERSHIP_ACCEPTANCE+node.port+Constants.MESSAGE_SEPARATOR+ports);
			}
		}
	}
	
	public void partnersMessage(String msgPart2,IoSession session){
		node.searching=false;
		String []partners=msgPart2.split(Constants.MESSAGE_SEPARATOR);
		if(node.partners.addPartner(Integer.parseInt(partners[0]),session,node.isTracker))
			session.write(Constants.BANDWIDTH_REQUEST+"");
		node.serverBond.write(Constants.STABLEIZED+""+Constants.MESSAGE_SEPARATOR+""+node.port);
	}
	
	public void receivePartnershipResponse(String msgPart2,IoSession session){
		String []bandwidthParam=msgPart2.split(Constants.MESSAGE_SEPARATOR);
		int port=Integer.parseInt(bandwidthParam[0]);
		int confirm=Integer.parseInt(bandwidthParam[1]);
		
		if(confirm == Constants.REQUEST_ACCEPTED && node.partners.addPartner(port, session,node.isTracker))
			session.write(Constants.BANDWIDTH_REQUEST+"");
		else{
			session.close();
			node.partners.clearPartners();
		}
		committed--;
	}
	
	public void terminateConnection(String msgPart2,IoSession session){
		int destination=Integer.parseInt(msgPart2);
		node.partners.deletePartner(destination);
		session.close();
	}
	
	public void sendBandwidth(IoSession session){
		session.write(""+Constants.BANDWIDTH_RESPONSE+node.port+"-"+node.bandwidth);
	}
	
	public void receiveBandwidth(String msgPart2,IoSession session){
		String []bandwidthParam=msgPart2.split("-");
		int port=Integer.parseInt(bandwidthParam[0]);
		int bandwidth=Integer.parseInt(bandwidthParam[1]);
		node.partners.setBandwidth(port,bandwidth);
	}
	
	public void handleGossipingRequest(String msgPart2){
		//System.err.println("me "+node.port+" got g:"+msgPart2);
		String []gParam=msgPart2.split("-");
		int hops=Integer.parseInt(gParam[1]);
		int originalPort=Integer.parseInt(gParam[0]);
		node.members.addMember(originalPort);
		
		
		if(hops>0){
    		node.gossip.bridge(originalPort,hops);
    	}
	}
	
	public void handleBitMapRequest(String msgPart2,IoSession session){
		int time=Integer.parseInt(msgPart2);
    	if(node.scheduler!=null){
		BitField bitField=node.scheduler.getWindow(time);
    	session.write(""+Constants.BUFFERMAP_RESPONSE+time+"-"+node.port+"-"+bitField.toString());
    	}
    	else{
    		BitField zeros = new BitField(node.windowSize);
    		session.write(""+Constants.BUFFERMAP_RESPONSE+time+"-"+node.port+"-"+zeros);
    	}
	}

	public void getBitMapResponse(String msgPart2){
		String []bParam=msgPart2.split("-");
		int src=Integer.parseInt(bParam[1]);
		int time=Integer.parseInt(bParam[0]);
		int index=node.partners.getIndex(src);
		if(index==-1)
			return;
		node.partners.getPartner(index).getBufferMap().setBits(bParam[2],time);
		
	}
	
	public void handlePingMessage(String msgPart2,IoSession session){
		session.write(""+Constants.SEGMENT_RESPONSE+msgPart2);
	}
	
	public void handlePongMessage(String msgPart2){
		int index=Integer.parseInt(msgPart2);
		
		if(node.scheduler.getWholeBits(index)!=1){
		//	if(node.port==39)
		//		System.err.println("segment: "+index+" ( now= "+SimulableSystem.currentTimeMillis()/1000+" - deadline= "+node.scheduler.getDeadLine(index)+" )");
		if(node.scheduler.getDeadLine(index)>=(SimulableSystem.currentTimeMillis()/1000)){
			node.continuityIndex++;
			node.scheduler.setWholeBits(index,1);
		}
		}
	}
	
}
