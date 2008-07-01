
package simpipe.coolstreaming;

import java.net.SocketAddress;
import java.util.Arrays;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.Scheduler;
import simpipe.SimPipeAddress;
import simpipe.coolstreaming.visualization.ContinuityIndex;
import simpipe.coolstreaming.visualization.DataStructure;
import simpipe.coolstreaming.visualization.MCacheOverPeers;
import simpipe.coolstreaming.visualization.PAverageOverTime;
import simpipe.coolstreaming.visualization.PCacheOverPeers;
import simpipe.coolstreaming.visualization.PScoreOverNetwork;
import simpipe.coolstreaming.visualization.PScoreOverPeers;
import simpipe.coolstreaming.visualization.TimeSlot;
import simpipe.coolstreaming.visualization.ViewApp;
import org.apache.commons.math.stat.*;


public class ControlRoom extends EventLoop{

	int time=(int)se.peertv.peertvsim.conf.Conf.MAX_SIMULATION_TIME/1000;
	double average[]=new double[time+10];
	double varience[]=new double[time+10];
	int index=0;
	
	final static boolean 	SIM_MODE = true;
	SocketAddress serverAddress;
	static PeerNode client[];
	MCacheOverPeers mCacheOverPeers = new MCacheOverPeers();
	PScoreOverPeers pScoreOverPeers = new PScoreOverPeers();
	PScoreOverNetwork pScoreOverNetwork = new PScoreOverNetwork();
	PCacheOverPeers pCacheOverPeers = new PCacheOverPeers();
	ContinuityIndex continuityIndex = new ContinuityIndex();
	PAverageOverTime pAverageOverTime= new PAverageOverTime();
	
	public static int counts=0;
	
	public static void main(String[] args) throws Exception {
		ControlRoom m = new ControlRoom();		
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
				 System.out.println("[Peer "+client[i].port+"] : Not source "+client[i].protocol.committed);
			 else
				 System.out.println("[Peer "+client[i].port+"] : Source"+client[i].protocol.committed);
			 	 
			 double CI=((double)client[i].continuityIndex)/((double)client[i].allIndex);
			 System.out.println("continuity index = "+CI);
			 System.out.print("My Partners are  : ");
	    		for(int j=0;j<client[i].pSize;j++){
	    			if(client[i].partners.getPartner(j)!=null)
	    				System.out.print(" - "+(client[i].partners.getPartner(j).port));
	    		}
	    		System.out.println("\n");
	    		System.out.print("My Buffer Map : ");
	    		for(int j=0;j<client[i].videoSize;j++)
	    		System.out.print(client[i].scheduler.getWholeBits(j));
	    		System.out.println("\n");
	    		System.out.println("\n-----------------------------------------------");
	   		 
		 }
		 
		 new ViewApp(mCacheOverPeers, pCacheOverPeers,pScoreOverNetwork,pScoreOverPeers,continuityIndex,pAverageOverTime);
		 
		 mCacheOverPeers.init();
		 mCacheOverPeers.view(0);
		 mCacheOverPeers.set("Membership's cache visualization","Peer ID","Number of Members");
		 
		 pScoreOverPeers.init();
		 pScoreOverPeers.view(0);
		 pScoreOverPeers.set("Score at Each Peer", "Peer ID", "Score");
		 
		 pScoreOverNetwork.init();
		 pScoreOverNetwork.view(0);
		 pScoreOverNetwork.set("Score Distribution Over Network","Score","Peers Number");
		 
		 pCacheOverPeers.init();
		 pCacheOverPeers.view(0);
		 pCacheOverPeers.set("Partner's Cache Visualization", "Peers","Number of Partners");
			
		 continuityIndex.init();
		 continuityIndex.view(0);
		 continuityIndex.set("Continuity Index at Each Peer", "Peer ID", "Continuity Index");
		 
		 pAverageOverTime.init(average);
		 pAverageOverTime.view(0);
		 pAverageOverTime.set("Average Score Visualization over time", "Time(Sec)", "Average Score");
		 
		 System.err.println("---> "+counts);
	 }
	 int[]empty={};
	@Override
	protected boolean postEventExecution() {
	if(Scheduler.getInstance().now%1000==0){
	
			fillMCacheOverPeers();
			fillPCacheOverPeers();
			fillPScoreOverPeers();
			fillPScoreOverNetwork();
			fillContinuityIndex();
			partnershipStat();
	
	}
		
		return false;
	}
	
	void fillMCacheOverPeers(){
		TimeSlot mSlot1=new TimeSlot();
		int[]empty={};
		for(int i=0;i<client.length;i++)
			if(client[i]!=null){
				int mCache[]=client[i].members.toArray();
				mSlot1.add(new DataStructure(mCache,String.valueOf(client[i].port)));	
			}
			else
			mSlot1.add(new DataStructure(empty,String.valueOf(client[i].port)));
		mCacheOverPeers.add(mSlot1);
		
	}
	void fillPCacheOverPeers(){
		TimeSlot pSlot3=new TimeSlot();
		for(int i=0;i<client.length;i++)
			if(client[i]!=null){
				int pCache[]=client[i].partners.toArray();
				pSlot3.add(new DataStructure(pCache,String.valueOf(client[i].port)));
			}
			else
			pSlot3.add(new DataStructure(empty,String.valueOf(client[i].port)));
		pCacheOverPeers.add(pSlot3);
		
	}
	void fillPScoreOverPeers(){
		TimeSlot pSlot1 = new TimeSlot();
		
		pScoreOverPeers.set("Partnership Visualization", "Peers","Partner's Points");
		
		for(int i=0;i<client.length;i++)
			if(client[i]!=null){
				int sum=bandwidthSum(client[i].partners.getCache());
				int bandwidth[]={sum};
				pSlot1.add(new DataStructure(bandwidth,String.valueOf(client[i].port)));
				
			}
			else
			pSlot1.add(new DataStructure(empty,String.valueOf(client[i].port)));
		pScoreOverPeers.add(pSlot1);
	}
	void fillPScoreOverNetwork(){
		TimeSlot pSlot2 = new TimeSlot();
		pScoreOverNetwork.set("Partnership Visualization", "Partner's Score","Weight");
		int scores[] = new int[client.length]; 
		for(int i=0;i<client.length;i++)
			if(client[i]!=null){
				int sum=bandwidthSum(client[i].partners.getCache());
				scores[i]=sum;
			}
		Arrays.sort(scores);
		pSlot2.add(new DataStructure(scores,""));
		pScoreOverNetwork.add(pSlot2);
		
	}
	void fillContinuityIndex(){
		TimeSlot sSlot1 = new TimeSlot();
		continuityIndex.set("Continuity Index", "Peers","CI");
		double CIs[] = new double[client.length]; 
		for(int i=0;i<client.length;i++)
			if(client[i]!=null){
				double CI=(((double)client[i].continuityIndex)/((double)client[i].allIndex));
				if(Double.isNaN(CI)){
					CI=1;
				}
				CIs[i]=CI;
			}
		sSlot1.add(new DataStructure(CIs,""));
		continuityIndex.add(sSlot1);
	}
	void partnershipStat(){
		if(this.index>=average.length)
			return;
	    double bandwidth[]=new double[getLength()];
	    int index=0;
	    for(int i=0;i<client.length;i++){
	    	if(client[i]!=null){
	    		double sum=0;
	    		for(int j=0;j<client[i].partners.getLength();j++){
	    			if(client[i].partners.getPartner(j)!=null)
	    			sum+=client[i].partners.getPartner(j).bandwidth;
	    		}
	    		bandwidth[index++]=sum/(double)client[i].partners.getLength();
	    	}
	    }
	    this.average[this.index]=StatUtils.mean(bandwidth);
	    this.varience[this.index++]=StatUtils.variance(bandwidth);
	}
	
	int getLength(){
		int sum=0;
		for(int i=0;i<client.length;i++)
			if(client[i]!=null)
				sum++;
		return sum;
	}
	
	int bandwidthSum(Partner[] partner){
		int sum=0;
		for(int i=0;i<partner.length;i++)
		if(partner[i]!=null)
			sum+=partner[i].bandwidth;
		return sum;
	}
	
}
