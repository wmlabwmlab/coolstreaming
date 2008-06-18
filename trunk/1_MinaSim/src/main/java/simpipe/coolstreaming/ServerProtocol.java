package simpipe.coolstreaming;

import java.net.SocketAddress;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.IoSession;

import simpipe.SimPipeAcceptor;

public class ServerProtocol extends IoHandlerAdapter {

	private CentralNode node;
	
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

    	System.out.println("Server says : \" new peer number "+session.getRemoteAddress().toString()+" Created !! \"");	
    }

    public void sessionClosed(IoSession session) {
       
    }

    public void messageReceived(IoSession session, Object message) {

    	if(((String)message).charAt(0)== Constants.CONNECTION_REQUEST){ //i.e. another client writes to me because he wants to join the network
    		    int client=Integer.parseInt(session.getRemoteAddress().toString());
    		    int port=node.members.getAnotherDeputy(client);
    			node.members.addMember(client);
    			session.write(""+Constants.DEPUTY_MESSAGE+port);
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
