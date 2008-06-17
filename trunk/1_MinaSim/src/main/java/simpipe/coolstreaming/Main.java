
package simpipe.coolstreaming;

import java.net.SocketAddress;
import java.util.Arrays;

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
import simpipe.coolstreaming.visualization.ContinuityIndex;
import simpipe.coolstreaming.visualization.DataStructure;
import simpipe.coolstreaming.visualization.MCacheOverPeers;
import simpipe.coolstreaming.visualization.PCacheOverPeers;
import simpipe.coolstreaming.visualization.PScoreOverNetwork;
import simpipe.coolstreaming.visualization.PScoreOverPeers;
import simpipe.coolstreaming.visualization.TimeSlot;
import simpipe.coolstreaming.visualization.ViewApp;



public class Main extends EventLoop{

	final static boolean 	SIM_MODE = true;
	SocketAddress serverAddress;
	static PeerNode client[];
	MCacheOverPeers mCacheOverPeers = new MCacheOverPeers();
	PScoreOverPeers pScoreOverPeers = new PScoreOverPeers();
	PScoreOverNetwork pScoreOverNetwork = new PScoreOverNetwork();
	PCacheOverPeers pCacheOverPeers = new PCacheOverPeers();
	ContinuityIndex continuityIndex = new ContinuityIndex();
	
	
	public static int counts=0;
	
	public static void main(String[] args) throws Exception {
		Main m = new Main();		
		m.createServer();
		int peerNumber=20;
		int sourceNumber=5;
		client=new PeerNode[peerNumber+sourceNumber];

		for(int i=0;i<sourceNumber;i++)	  //source
			client[i]= new PeerNode(true,m.serverAddress); 
		
		for(int i=0;i < peerNumber;i++)
			client[sourceNumber+i]= new PeerNode(false,m.serverAddress); //not source
	
		displayBegin();
		m.run();		
	}	
	
	public void createServer() throws Exception{			
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.OFF);
		serverAddress = new SimPipeAddress(Constants.SERVER_PORT);
		new CentralNode(serverAddress);
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
				 System.out.println("[Peer "+client[i].port+"] : Not source "+client[i].partners.committed);
			 else
				 System.out.println("[Peer "+client[i].port+"] : Source"+client[i].partners.committed);
			 	 
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
		 
		 ViewApp view = new ViewApp(mCacheOverPeers, pCacheOverPeers,pScoreOverNetwork,pScoreOverPeers);
		 mCacheOverPeers.init();
		 mCacheOverPeers.view(0);
		 pScoreOverPeers.init();
		 pScoreOverPeers.view(0);
		 pScoreOverNetwork.init();
		 pScoreOverNetwork.view(0);
		 pCacheOverPeers.init();
		 pCacheOverPeers.view(0);	
		 System.err.println("---> "+counts);
	 }
  
	@Override
	protected boolean postEventExecution() {
		// TODO Auto-generated method stub
		
		TimeSlot mSlot1=new TimeSlot();
		TimeSlot pSlot1=new TimeSlot();
		TimeSlot pSlot2 = new TimeSlot();
		TimeSlot pSlot3 = new TimeSlot();
		
		if(Scheduler.getInstance().now%1000==0){
			//Membership cache
			int[]empty={};
			for(int i=0;i<client.length;i++)
				if(client[i]!=null){
					int mCache[]=client[i].members.toArray();
					mSlot1.add(new DataStructure(mCache,String.valueOf(client[i].port)));
					
				}
				else
				mSlot1.add(new DataStructure(empty,String.valueOf(client[i].port)));
			mCacheOverPeers.add(mSlot1);
			
			
			//Partnership cache
			pCacheOverPeers.set("Partner's Cache Visualization", "Peers","Partners");
			for(int i=0;i<client.length;i++)
				if(client[i]!=null){
					int pCache[]=client[i].partners.toArray();
					pSlot3.add(new DataStructure(pCache,String.valueOf(client[i].port)));
				}
				else
				pSlot3.add(new DataStructure(empty,String.valueOf(client[i].port)));
			pCacheOverPeers.add(pSlot3);
			
			//partnership score over peers
			pScoreOverPeers.set("Partnership Visualization", "Peers","Partner's Points");
			
			for(int i=0;i<client.length;i++)
				if(client[i]!=null){
					int sum=bandwidthSum(client[i].partners.pCache);
					int bandwidth[]={sum};
					pSlot1.add(new DataStructure(bandwidth,String.valueOf(client[i].port)));
					
				}
				else
				pSlot1.add(new DataStructure(empty,String.valueOf(client[i].port)));
			pScoreOverPeers.add(pSlot1);
			
			//Partnership score over network
			pScoreOverNetwork.set("Partnership Visualization", "Partner's Score","Weight");
			int scores[] = new int[client.length]; 
			for(int i=0;i<client.length;i++)
				if(client[i]!=null){
					int sum=bandwidthSum(client[i].partners.pCache);
					scores[i]=sum;
				}
			Arrays.sort(scores);
			pSlot2.add(new DataStructure(scores,""));
			pScoreOverNetwork.add(pSlot2);
			
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
