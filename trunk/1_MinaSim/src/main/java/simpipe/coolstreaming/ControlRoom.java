
package simpipe.coolstreaming;

import java.net.SocketAddress;
import java.util.Arrays;

import javax.swing.plaf.basic.BasicScrollPaneUI.VSBChangeListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.Timer;
import simpipe.SimPipeAddress;
import simpipe.coolstreaming.implementations.Partner;
import simpipe.coolstreaming.visualization.ContinuityIndex;
import simpipe.coolstreaming.visualization.DataStructure;
import simpipe.coolstreaming.visualization.MCacheOverPeers;
import simpipe.coolstreaming.visualization.NetworkVisualization;
import simpipe.coolstreaming.visualization.PAverageOverTime;
import simpipe.coolstreaming.visualization.PCacheOverPeers;
import simpipe.coolstreaming.visualization.PScoreOverNetwork;
import simpipe.coolstreaming.visualization.PScoreOverPeers;
import simpipe.coolstreaming.visualization.TimeSlot;
import simpipe.coolstreaming.visualization.ViewApp;
import simpipe.coolstreaming.visualization.Visualization;

import org.apache.commons.math.stat.*;


public class ControlRoom extends EventLoop{

	
	static int peerNumber=20;
	static int sourceNumber=5;
	static int creationRate = 500; // 1 client per 0.5 minute 
	static int portStart=15;
	final static boolean 	SIM_MODE = true;
	int trackerCapacity=300;
	
	int time=(int)se.peertv.peertvsim.conf.Conf.MAX_SIMULATION_TIME/1000;
	double average[]=new double[time+10];
	double varience[]=new double[time+10];
	int index=0;
	
	SocketAddress serverAddress;
	static PeerNode client[];
	static CentralNode tracker;
	Visualization visual[];
	
	static int maxPeers=0;
	int[]empty={};
	
	public static int counts=0;
	
	public static void main(String[] args) throws Exception {
		ControlRoom m = new ControlRoom();		
		m.createServer();
		client=new PeerNode[peerNumber+sourceNumber];

		
		//int portStart=15;
		//for(int i=0;i<sourceNumber;i++)	  //source
		//	client[i]= new PeerNode(true,m.serverAddress,portStart+i); 
		//portStart+=sourceNumber;
		//for(int i=0;i < peerNumber;i++)
		//	client[sourceNumber+i]= new PeerNode(false,m.serverAddress,portStart+i); //not source
		
		m.run();		
	}	
	
	public void createClient(int time){
		System.err.println("FIREEEEEEEEEEEEEEEEEEEE");
		tracker.members.clearPartners();
//		int peers[]=tracker.members.toArray();
//		System.out.println("==========================================");
//		System.out.println("My children are : ");
//		for(int i=0;i<peers.length;i++)
//			if(peers[i]!=0)
//				System.out.println(peers[i]);
//		try{
//			Thread.sleep(3000);
//		}
//		catch(Exception e){}
//		
		if(maxPeers<peerNumber){
			client[maxPeers]= new PeerNode(false,serverAddress,portStart+maxPeers);
			maxPeers++;
			System.err.println("New Peer "+portStart+maxPeers+" trying to connect at time: "+time);
		}
		else{
			if(maxPeers<(peerNumber+sourceNumber)){
				client[maxPeers]= new PeerNode(true,serverAddress,portStart+maxPeers);
				maxPeers++;
				System.err.println("New Source "+portStart+maxPeers+" trying to connect at time: "+time);
			}
		}
		if(maxPeers<peerNumber+sourceNumber)
		try{
			Timer timer = new Timer(creationRate,this,"createClient",(int)Scheduler.getInstance().now+creationRate);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public ControlRoom() {
		try{
			Timer timer = new Timer(creationRate,this,"createClient",(int)Scheduler.getInstance().now+creationRate);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		displayBegin();
	}
	
	public void createServer() throws Exception{			
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.OFF);
		serverAddress = new SimPipeAddress(Constants.SERVER_PORT);
		tracker = new CentralNode(serverAddress,trackerCapacity);
	}
	
	
	public  void displayBegin(){
		 
		 System.out.println("Begin");
		 visual = new Visualization[7];
		 visual[0] = new MCacheOverPeers();
		 visual[1] = new PScoreOverPeers();
		 visual[2] = new PScoreOverNetwork();
		 visual[3] = new PCacheOverPeers();
		 visual[4] = new ContinuityIndex();
		 visual[5]= new PAverageOverTime();
		 visual[6] = new NetworkVisualization();	
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
	    				System.out.print(" - "+(client[i].partners.getPartner(j).getPort()));
	    		}
	    		System.out.println("\n");
	    		System.out.print("My Buffer Map : ");
	    		for(int j=0;j<client[i].videoSize;j++)
	    		System.out.print(client[i].scheduler.getWholeBits(j));
	    		System.out.println("\n");
	    		System.out.println("\n-----------------------------------------------");
	   		 
		 }
		 
		 visual[0].set("Membership's cache visualization","Peer ID","Number of Members");
		 visual[1].set("Score at Each Peer", "Peer ID", "Score");
		 visual[2].set("Score Distribution Over Network","Score","Peers Number");
		 visual[3].set("Partner's Cache Visualization", "Peers","Number of Partners");
		 visual[4].set("Continuity Index at Each Peer", "Peer ID", "Continuity Index");
		 visual[5].set("Average Score Visualization over time", "Time(Sec)", "Average Score");
		 
		 
		 for(int k=0;k<visual.length;k++){
			 if(!visual[k].isDependent())
				 visual[k].init();
			 else
				 visual[k].init(average);
				 
			 visual[k].view(0); 
		 }
		 new ViewApp(visual);
		 System.err.println("---> "+counts);
	 }
	 
	@Override
	protected boolean postEventExecution() {
		
		int time =(int)Scheduler.getInstance().now;
		
		
		if(time%1000==0){
			/*
			for(int i=0;i<client.length;i++){
				if(client[i]==null)
					continue;
				int now=(int)Scheduler.getInstance().now/1000;
				int missed=(client[i].joinTime-client[i].startTime)/1000;
				if(now>client[i].videoSize+(client[i].startTime/1000)){
					client[i].allIndex=client[i].videoSize-missed;
					continue;
				}
				int shouldHave=now-(client[i].startTime/1000);
				int musthave=shouldHave-missed;
				client[i].allIndex=musthave;
			}
			*/
			for(int i=0;i<client.length;i++){
				if(client[i]==null)
					continue;
				int now=(int)Scheduler.getInstance().now;
				int missed=(client[i].joinTime-client[i].startTime);
				if(now>(client[i].videoSize*1000)+(client[i].startTime)){
					client[i].allIndex=client[i].videoSize-(missed/1000);
					continue;
				}
				int mustHave=now-(client[i].joinTime);
				client[i].allIndex=mustHave/1000;
			}
		
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
		for(int i=0;i<client.length;i++)
			if(client[i]!=null&&client[i].members!=null){
				int mCache[]=client[i].members.toArray();
				mSlot1.add(new DataStructure(mCache,String.valueOf(client[i].port)));	
			}
			else
			continue;
		visual[0].add(mSlot1);
		
	}
	
	void fillPCacheOverPeers(){
	
		TimeSlot pSlot3=new TimeSlot();
		for(int i=0;i<client.length;i++)
			if(client[i]!=null&&client[i].partners!=null){
				int pCache[]=client[i].partners.toArray();
				pSlot3.add(new DataStructure(pCache,String.valueOf(client[i].port)));
			}
			else
			continue;
		visual[3].add(pSlot3);
		visual[6].add(pSlot3);
		
	}
	void fillPScoreOverPeers(){
		
		TimeSlot pSlot1 = new TimeSlot();
		for(int i=0;i<client.length;i++)
			if(client[i]!=null&&client[i].partners!=null){
				int sum=bandwidthSum(client[i].partners.getCache());
				int bandwidth[]={sum};
				pSlot1.add(new DataStructure(bandwidth,String.valueOf(client[i].port)));
				
			}
			else
			continue;
		visual[1].add(pSlot1);
	}
	
	void fillPScoreOverNetwork(){
		
		TimeSlot pSlot2 = new TimeSlot();
		int scores[] = new int[client.length]; 
		for(int i=0;i<client.length;i++)
			if(client[i]!=null&&client[i].partners!=null){
				int sum=bandwidthSum(client[i].partners.getCache());
				scores[i]=sum;
			}
			else 
				continue;
		Arrays.sort(scores);
		pSlot2.add(new DataStructure(scores,""));
		visual[2].add(pSlot2);
		
	}
	
	void fillContinuityIndex(){
		
		TimeSlot sSlot1 = new TimeSlot();
		double CIs[] = new double[client.length]; 
		for(int i=0;i<client.length;i++)
			if(client[i]!=null){
				double CI=(((double)client[i].continuityIndex)/((double)client[i].allIndex));
				if(Double.isNaN(CI))
					CI=1;
				CIs[i]=CI;
			}
		sSlot1.add(new DataStructure(CIs,""));
		visual[4].add(sSlot1);
	}
void partnershipStat(){
		
		if(this.index>=average.length)
			return;
	    double bandwidth[]=new double[getLength()];
	    int index=0;
	    for(int i=0;i<client.length;i++){
	    	if(client[i]!=null&&client[i].partners!=null){
	    		double sum=0;
	    		for(int j=0;j<client[i].partners.getLength();j++){
	    			if(client[i].partners.getPartner(j)!=null)
	    			sum+=client[i].partners.getPartner(j).getBandwidth();
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
			sum+=partner[i].getBandwidth();
		return sum;
	}
	
}
