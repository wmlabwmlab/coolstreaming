
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
import simpipe.coolstreaming.visualization.MembersStructure;
import simpipe.coolstreaming.visualization.MembershipVisualization;
import simpipe.coolstreaming.visualization.PartnershipVisualization;
import simpipe.coolstreaming.visualization.TimeSlot;


public class Main extends EventLoop{

	final static boolean 	SIM_MODE = true;
	final static int 		PORT = 30000;
	SocketAddress serverAddress;
	static PeerNode client[];
	MembershipVisualization mVisualization = new MembershipVisualization();
	PartnershipVisualization pVisualization = new PartnershipVisualization();
	
	
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
		 System.out.println(Scheduler.getInstance().now);
		 displayEnd();
	 }
	 
	 public  void displayEnd(){
		 System.out.println("END");
		 
		 for(int i=0;i<client.length;i++){
			 if(client[i]==null)
				 continue;
			 if(!client[i].isSource)
				 System.out.println("[Peer "+client[i].port+"] : Not source");
			 else
				 System.out.println("[Peer "+client[i].port+"] : Source");
			 	 
			 double CI=((double)client[i].continuityIndex)/((double)client[i].allIndex);
			 System.out.println("continuity index = "+CI);
			 System.out.print("My Partners are  : ");
	    		for(int j=0;j<client[i].pSize;j++){
	    			if(client[i].partners.pCache[j]!=null)
	    				System.out.print(" - "+(client[i].partners.pCache[j].port));
	    		}
	    		System.out.println("\n");
	    		System.out.print("My Buffer Map : ");
	    		for(int j=0;j<client[i].videoSize;j++)
	    		System.out.print(client[i].scheduler.wholeBits[j]);
	    		System.out.println("\n");
	    		System.out.println("\n-----------------------------------------------");
	   		 
		 }
		 
		 
		 mVisualization.init();
		 mVisualization.view(0);
		 pVisualization.init();
		 pVisualization.view(0);
		 
			
	 }
  
	@Override
	protected boolean postEventExecution() {
		// TODO Auto-generated method stub
		
		TimeSlot slot=new TimeSlot();
		if(Scheduler.getInstance().now%1000==0){
			//Membership
			int[]empty={};
			for(int i=0;i<client.length;i++)
				if(client[i]!=null)
				slot.add(new MembersStructure(client[i].mCache,String.valueOf(client[i].port)));
				else
				slot.add(new MembersStructure(empty,String.valueOf(client[i].port)));
			mVisualization.add(slot);
			
			//partnership
			pVisualization.set("Partnership Visualization", "Peers","Partner's Points");
			
			for(int i=0;i<client.length;i++)
				if(client[i]!=null){
					int sum=bandwidthSum(client[i].partners.pCache);
					int bandwidth[]={sum};
					slot.add(new MembersStructure(bandwidth,String.valueOf(client[i].port)));
					
				}
				else
				slot.add(new MembersStructure(empty,String.valueOf(client[i].port)));
			pVisualization.add(slot);
		}
		
		return false;
	}
	
	int bandwidthSum(Partner[] partner){
		int sum=0;
		for(int i=0;i<partner.length;i++)
		if(partner[i]!=null)
			sum+=partner[i].bandwidth;
		return sum;
	}
	
}
