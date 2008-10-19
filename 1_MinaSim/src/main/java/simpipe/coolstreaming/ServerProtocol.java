package simpipe.coolstreaming;

import java.net.SocketAddress;
import java.util.ArrayList;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.IoSession;

import simpipe.SimPipeAcceptor;
import simpipe.coolstreaming.implementations.Partner;

public class ServerProtocol extends IoHandlerAdapter {

	private CentralNode node;
	ArrayList<Partner> temp=new ArrayList<Partner>();
	
	public ServerProtocol(SocketAddress serverAddress,CentralNode node){
		this.node = node; 
		IoServiceConfig config;
		IoAcceptor acceptor;
		acceptor = new SimPipeAcceptor();
		config = acceptor.getDefaultConfig();
		try{
		acceptor.bind(serverAddress, this, config);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void sessionCreated(IoSession session) throws Exception {
    	
    }

    public void sessionClosed(IoSession session) {
       node.members.clearPartners();
    }

    public void messageReceived(IoSession session, Object message) {

    	if(((String)message).charAt(0)== Constants.CONNECTION_REQUEST){ //i.e. another client writes to me because he wants to join the network
    		    String messageContent=(String)message;
    		    String []parameters=messageContent.split(Constants.MESSAGE_SEPARATOR);
    			int newPeer=Integer.parseInt(parameters[1]);
    		    ControlRoom.logger.info(newPeer+" is joining the network");
    			//System.out.println("got "+newPeer);
    		    int port=node.members.getAnotherDeputy(newPeer);
    			if(newPeer==port)
    		    node.members.addPartner(newPeer,session,node.isTracker);
    			else{
    				Partner p =new Partner(newPeer,0,session,null);
    				temp.add(p);
    			}
    			
    			session.write(""+Constants.DEPUTY_MESSAGE+port);
    	}
    	else if(((String)message).charAt(0)== Constants.DEPUTY_MESSAGE){ //i.e. another client writes to me because he wants to join the network
		    String messageContent=(String)message;
		    String []parameters=messageContent.split(Constants.MESSAGE_SEPARATOR);
			int peer=Integer.parseInt(parameters[1]);
		    System.out.println(peer+" requesting new friends");
		    int port=node.members.getAnotherDeputy(peer);
			session.write(""+Constants.DEPUTY_MESSAGE+port);
    	}
    	else if(((String)message).charAt(0)== Constants.STABLEIZED){ //i.e. another client writes to me because he wants to join the network
		    
    		String messageContent=(String)message;
		    String []parameters=messageContent.split(Constants.MESSAGE_SEPARATOR);
			int peer=Integer.parseInt(parameters[1]);
			ControlRoom.logger.debug(peer+" STABLEIZED");
			//System.err.println(peer+" STABLEIZZZZZZZZZED");
			rempovePartner(peer);
			node.members.addPartner(peer,session,node.isTracker);
			
    	}
    }

    Partner rempovePartner(int port){
    	Partner p=null;
    	for(int i=0;i<temp.size();i++)
    		if(temp.get(i).getPort()==port){
    			temp.remove(i);
    			break;
    		}
    	
    	return p;
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
