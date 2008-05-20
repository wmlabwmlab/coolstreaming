
package simpipe.coolstreaming;

import java.net.SocketAddress;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.support.BaseIoConnector;

import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.Scheduler;
import simpipe.SimPipeAcceptor;
import simpipe.SimPipeAddress;
import simpipe.SimPipeConnector;


public class Main extends EventLoop{

	final static boolean 	SIM_MODE = true;
	final static int 		PORT = 30000;
	SocketAddress serverAddress;
	static PeerNode client[];
	MembershipVisualization visualization = new MembershipVisualization();
	
	public static void main(String[] args) throws Exception {
		Main m = new Main();		
		m.createServer();
		int peerNumber=20;
		int sourceNumber=5;
		client=new PeerNode[peerNumber+sourceNumber];
		for(int i=0;i<peerNumber;i++)
			m.startClient(false,i); //not source
	
		for(int i=0;i<sourceNumber;i++)	  //source
			m.startClient(true,peerNumber+i);
		Schedule schedule = new Schedule(100);
		displayBegin();
		m.run();		
	}	
	
	public void createServer() throws Exception{			
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.OFF);
		IoHandlerAdapter handler = new CentralNode();
		IoServiceConfig config;
		IoAcceptor acceptor;
		acceptor = new SimPipeAcceptor();
		config = acceptor.getDefaultConfig();
		serverAddress = new SimPipeAddress(PORT);
		acceptor.bind(serverAddress, handler, config);
				
	}
	
	 void startClient(boolean isSource,int pos)throws Exception{
		 
		PeerNode handler = new PeerNode(isSource); 
		client[pos]=handler;
		BaseIoConnector connector;
		connector = new SimPipeConnector();
		ConnectFuture future = connector.connect(serverAddress, handler);
		
	 }
	 
	 
	 public static void displayBegin(){
		 System.out.println("Begin");
		 
	 }
	 
	 @Override
	 protected void postSimulationLoop() {
		 super.preSimulationLoop();
		 displayEnd();
	 }
	 
	 public  void displayEnd(){
		 System.out.println("END");
		 /*
		 for(int i=0;i<client.length;i++){
			 if(client[i]==null)
				 continue;
			 System.out.print(client[i].allIndex+"-->"+client[i].continuityIndex+" [Peer "+client[i].port+"] : now my friends are  : ");
	    		for(int j=0;j<client[i].pSize;j++){
	    			if(client[i].pCache[j]!=0)
	    				System.out.print(" - "+(client[i].pCache[j]));
	    		}
	    		
	    		for(int j=0;j<client[i].videoSize;j++)
	    		System.out.print(client[i].scheduler.wholeBits[j]);
	    		System.out.println("\n");
		 }
		 */
		 
		 for(int i=0;i<6;i++){
			 System.out.println("\n-----------------------------------------------");
			 System.out.println(" For peer "+client[i].port+" My members are : ");
			 for(int j=0;j<client[i].mSize;j++){
				 System.out.print(client[i].mCache[j]+" - ");
			 }
		 }
		 
		 visualization.init();
		 visualization.view(0);
		 
			
	 }
  boolean start=true;
  boolean start2=true;
	@Override
	protected boolean postEventExecution() {
		// TODO Auto-generated method stub
		
		TimeSlot slot=new TimeSlot();
		if(Scheduler.getInstance().now%1000==0){
			int[]empty={};
			for(int i=0;i<client.length;i++)
				if(client[i]!=null)
				slot.add(new MembersStructure(client[i].mCache,String.valueOf(client[i].port)));
				else
				slot.add(new MembersStructure(empty,String.valueOf(client[i].port)));
			visualization.add(slot);
		}
		
		if(Scheduler.getInstance().now>10000&&start){
			start=false;
			for(int i=0;i<6;i++){
				 System.out.println("\n-----------------------------------------------");
				 System.out.println(" For peer "+client[i].port+" My members are : ");
				 for(int j=0;j<client[i].mSize;j++){
					 System.out.print(client[i].mCache[j]+" - ");
				 }
			 }
		}
		if(Scheduler.getInstance().now>20000&&start2){
			start2=false;
			for(int i=0;i<6;i++){
				 System.out.println("\n-----------------------------------------------");
				 System.out.println(" For peer "+client[i].port+" My members are : ");
				 for(int j=0;j<client[i].mSize;j++){
					 System.out.print(client[i].mCache[j]+" - ");
				 }
			 }
		}
		
		return false;
	}
	
	
}
