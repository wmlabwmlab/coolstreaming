
package simpipe.coolstreaming;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicScrollPaneUI.VSBChangeListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.executor.SchedulingExecutor;
//import se.peertv.peertvsim.thread.Thread;
import simpipe.base.support.SimPipeAddress;
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
import org.hamcrest.core.IsAnything;
import org.springframework.scheduling.SchedulingException;


public class ControlRoom extends EventLoop{

	
	//Static automated variables
	public static boolean isAutomated=false;
	public static int peers=25;
	public static int seeds=30;
	public static int windowSize=30;
	public static int slack=3;
	public static int segment=1;
	public static int exchange=30000;
	public static String filename="outputs/CI.properties";
	public static String input="exp";
	
	
	static Logger logger;
	int peerNumber=20;
	int sourceNumber=5;
	int creationRate = 500; // 1 client per 0.5 minute 
	int portStart=15;
	int trackerCapacity=300;
	ArrayList<Double> ciBuffer = new ArrayList<Double>();
	int snapShotRate=1000; //1 sec
	
	
	int time=(int)se.peertv.peertvsim.conf.Conf.MAX_SIMULATION_TIME/1000;
	double average[]=new double[time+10];
	double varience[]=new double[time+10];
	int index=0;
	
	SocketAddress serverAddress;
	PeerNode client[];
	CentralNode tracker;
	Visualization visual[];
	private SchedulingExecutor executor;
	
	int maxPeers=0;
	int[]empty={};
	
	public static int counts=0;
	
	public ControlRoom() {
		
		//init logger
		Logger.getRootLogger().removeAllAppenders();
		logger =Logger.getLogger("debugging_logger");
		logger.setLevel((Level)Level.ERROR);
		executor = new SchedulingExecutor(1234);
/*
 * this section is modified to import new sim
*/
		
//		try{
//			Timer timer = new Timer(creationRate,this,"createClient",(int)SimulableSystem.currentTimeMillis()+creationRate);
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
		executor.scheduleAtFixedRate(new Runnable(){	public void run(){
																	createClient();
										}},
										creationRate,
										creationRate,
										TimeUnit.MICROSECONDS,peerNumber+sourceNumber);
		displayBegin();
	}
	
	public static void main(String[] args) throws Exception {
		
		ControlRoom m = new ControlRoom();		
		int size = args.length;
		if(size>0){
			m.isAutomated=true;
			Properties properties = new Properties();
		    try {
		        properties.load(new FileInputStream(args[0]));
		        peers=Integer.parseInt((String)properties.get("peers"));
		        seeds=Integer.parseInt((String)properties.get("seeds"));
		        windowSize=Integer.parseInt((String)properties.get("window"));
		        slack=Integer.parseInt((String)properties.get("slack"));
		        exchange=Integer.parseInt((String)properties.get("exchangeTime"));
		        segment=Integer.parseInt((String)properties.get("segment"));
		        input=args[0];
		        
		        m.peerNumber=peers;
		        m.sourceNumber=seeds;
		        
		    } 
		    catch (IOException e) {
		    }
		    
		}
		m.createServer();
		m.client=new PeerNode[m.peerNumber+m.sourceNumber];
		m.run();
		
		if(!m.isAutomated){
			logger.setLevel(Level.TRACE);
			logger = Logger.getLogger("logger222");
		}
	}	
	
	public void createClient(){
		tracker.members.clearPartners();
		if(maxPeers<peerNumber){
			client[maxPeers]= new PeerNode(false,serverAddress,portStart+maxPeers);
			maxPeers++;
			
		}
		else{
			if(maxPeers<(peerNumber+sourceNumber)){
				client[maxPeers]= new PeerNode(true,serverAddress,portStart+maxPeers);
				maxPeers++;
				
			}
		}
		/*
		 * this section is modified to import new sim
		*/
		
//		if(maxPeers<peerNumber+sourceNumber)
//		try{
//			Timer timer = new Timer(creationRate,this,"createClient",(int)SimulableSystem.currentTimeMillis()+creationRate);
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
	}
	
	
	public void createServer() throws Exception{			
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
		serverAddress = new SimPipeAddress(Constants.SERVER_PORT);
		tracker = new CentralNode(serverAddress,trackerCapacity);
		
	}
	
	
	public  void displayBegin(){
		 
		 System.out.println("Begin");
	   if(!isAutomated){
		 visual = new Visualization[7];
		 visual[0] = new MCacheOverPeers();
		 visual[1] = new PScoreOverPeers();
		 visual[2] = new PScoreOverNetwork();
		 visual[3] = new PCacheOverPeers();
		 visual[4] = new ContinuityIndex();
		 visual[5]= new PAverageOverTime();
		 visual[6] = new NetworkVisualization();	
		}
	 }
	 
	 @Override
	 public void postSimulationLoop() {
		 super.preSimulationLoop();
		 System.out.println(SimulableSystem.currentTimeMillis());
		 displayEnd();
	 }
	 
	 public  void displayEnd(){
		 System.out.println("END");
		 double threshold=1;
		 if(!isAutomated){
		 for(int i=0;i<client.length;i++){
			 if(client[i]==null)
				 continue;
			 if(!client[i].isSource)
				 logger.info("[Peer "+client[i].port+"] : Not source ");
			 else
				 logger.info("[Peer "+client[i].port+"] : Source");
			 	 
			 double CI=((double)client[i].continuityIndex)/((double)client[i].allIndex);
			 if(CI>threshold)
				 CI=threshold;
			 logger.info("continuity index = "+CI);
			 String partners = "My Partners are  : ";
			 for(int j=0;j<client[i].pSize;j++){
	    		if(client[i].partners.getPartner(j)!=null)
	    			partners+="  [ "+(client[i].partners.getPartner(j).getPort())+" ]";
	    	 }
			 logger.info(partners);
			
			String buffer="My Buffer Map : " ;
	    	for(int j=0;j<client[i].videoSize;j++)
	    	buffer+=client[i].scheduler.getWholeBits(j);
	    	logger.info(buffer);
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
		 System.err.println("---> "+slotcount);
		 tracker.protocol.acceptor.unbindAll();
		 for(int i=0;i<client.length;i++){
			 client[i].protocol.disconnect();
		 }
		 }
		 else{
			 double sum=0;
			 for(int i=0;i<ciBuffer.size();i++){
				 Double ci= ciBuffer.get(i);
				 sum+=ci.doubleValue();
			 }
			 double AVG = sum/ciBuffer.size();
			 System.err.println(AVG);
			 Properties properties= new Properties();
			 try {
				 properties.load(new FileInputStream(filename));
			     properties.setProperty(input,new Double(AVG).toString());
			     properties.store(new FileOutputStream(filename),"append");
			        
			    } 
			 catch (IOException e) {
			   }

			 
		 }
	 }
	 boolean postTimer=true;
	@Override
	public boolean postEventExecution() {
		
		int time =(int)SimulableSystem.currentTimeMillis();
		if(time%1000==0)
		if(postTimer){
			postTimer=false;
			gatherInfo();
		}
		return false;
	}
	int slotcount=0;
	public void gatherInfo(){
			slotcount++;
			for(int i=0;i<client.length;i++){
				if(client[i]==null)
					continue;
				int now=(int)SimulableSystem.currentTimeMillis();
				int missed=(client[i].joinTime-client[i].startTime);
				if(i==8){
					
					//System.err.println("now="+SimulableSystem.currentTimeMillis()+" , join= "+client[i].joinTime+" , vsize+starttime= "+((client[i].videoSize*1000)+(client[i].startTime)));
				}
				if(now>((client[i].videoSize*1000)+(client[i].startTime))){
				
						System.err.println("BREAAAAKKKKKK");
					break;
					//client[i].allIndex=client[i].videoSize-(missed/1000);
					//continue;
				}
				int mustHave=now-(client[i].joinTime);
				client[i].allIndex=mustHave/1000;
			}
			if(!isAutomated){
			fillMCacheOverPeers();
			fillPCacheOverPeers();
			fillPScoreOverPeers();
			fillPScoreOverNetwork();
			fillContinuityIndex();
			partnershipStat();
			}
			else{
				collectCI();
			}
			
			/*
			 * this section is modified to import new sim
			*/			
//			try{
//				Timer timer = new Timer(snapShotRate,this,"gatherInfo",0);
//			}
//			catch(Exception e){
//				e.printStackTrace();
//			}
			executor.schedule(new Runnable(){
											public void run(){
												gatherInfo();
											}},
											snapShotRate, TimeUnit.MICROSECONDS);
	}

	void collectCI(){
		double threshold=1;
		TimeSlot sSlot1 = new TimeSlot();
		double CIs[] = new double[client.length]; 
		for(int i=0;i<client.length;i++)
			if(client[i]!=null){
				double CI=(((double)client[i].continuityIndex)/((double)client[i].allIndex));
				if(Double.isNaN(CI))
					CI=1;
				if(CI>threshold)
					CI=threshold;
				
				CIs[i]=CI;
			}
			else {
				CIs[i]=-1;
			}
		double sum=0;
		int count=0;
		for(int i=0;i<CIs.length;i++)
		{
			if(CIs[i]!=-1){
				sum+=CIs[i];
				count++;
			}
		}
		if (count==0)
			return;
		Double avg = new Double(sum/count);
		ciBuffer.add(avg);

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
		double threshold=1;
		TimeSlot sSlot1 = new TimeSlot();
		double CIs[] = new double[client.length]; 
		for(int i=0;i<client.length;i++)
			if(client[i]!=null){
				double CI=(((double)client[i].continuityIndex)/((double)client[i].allIndex));
				if(Double.isNaN(CI))
					CI=1;
				if(CI>threshold)
					CI=threshold;
				
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
