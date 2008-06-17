
package simpipe.examples;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.support.BaseIoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketConnector;

import se.peertv.peertvsim.core.EventLoop;
import simpipe.SimPipeAcceptor;
import simpipe.SimPipeAddress;
import simpipe.SimPipeConnector;
import simpipe.coolstreaming.CentralNode;
import simpipe.coolstreaming.PeerNode;


public class Main extends EventLoop{

	final static boolean 	SIM_MODE = true;
	final static int 		PORT = 30000;
	SocketAddress serverAddress;

	public static void main(String[] args) throws Exception {
		Main m = new Main();		
		m.createServer();
		
		m.startClient();
		m.startClient();
		
		m.run();		
	}	
	
	public void createServer() throws Exception{			
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.OFF);

		new CentralNode(serverAddress);

				
	}
	
	 void startClient(){
//		BaseIoConnector connector;
//		IoHandlerAdapter handler = new PeerNode(false);
//		connector = new SimPipeConnector();
//		ConnectFuture future = connector.connect(serverAddress, handler);
//		
	 }

	@Override
	protected boolean postEventExecution() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
}
