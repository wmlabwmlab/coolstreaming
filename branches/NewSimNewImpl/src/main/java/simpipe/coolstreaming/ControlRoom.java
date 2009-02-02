
package simpipe.coolstreaming;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math.stat.StatUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import se.peertv.peertvsim.SimulableSystem;

import se.peertv.peertvsim.core.EventLoop;

import se.peertv.peertvsim.executor.SchedulingExecutor;
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

public class ControlRoom extends EventLoop{

	
	//Static automated variables
	public static boolean isAutomated=false;
	public static int peers=25;
	public static int seeds=1;
	public static int leaving=0;
	public static int peerLimit=5; // nonleaving number of peers in the network before declaring that the next peer is leaving
	public static int leavingrate=-1;
	public static int windowSize=120;
	public static int slack=30;
	public static int segment=1;
	public static int exchange=30000;

	public static String filename="outputs/CI.properties";
	public static String input="exp";
	
	public static int leadFactor = windowSize * 2;
	public static int refreshVideoBufferRate = (leadFactor/2) * 1000;
	
	static Logger logger;
	int peerNumber=25;
	int sourceNumber=1;
	int creationRate = 500; // 1 client per 0.5 second 
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
	PeerNode sources[];
	CentralNode tracker;
	Visualization visual[];
	
	
	int maxPeers=0;
	int currentSources=0;
	int[]empty={};
	
	public static int counts=0;
	
	public ControlRoom() {
		
		//init logger
		Logger.getRootLogger().removeAllAppenders();
		logger =Logger.getLogger("debugging_logger");
		logger.setLevel((Level)Level.DEBUG);
		
		displayBegin();
	}
	void init(){
		if(leaving>0)
			leavingrate = (int)Math.ceil((double)(peerNumber-peerLimit)/(double)leaving);
		else
			peerLimit=Integer.MAX_VALUE;
	}
	
	public static void main(String[] args) throws Exception {
		
		final ControlRoom m = new ControlRoom();		
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
		        leaving = Integer.parseInt((String)properties.get("leave"));
		        input=args[0];
		        m.peerNumber=peers;
		        m.sourceNumber=seeds;
		    	leadFactor = windowSize * 2;
		    	refreshVideoBufferRate = (leadFactor/4) * 1000;		        
		        m.init();
		        
		    } 
		    catch (IOException e) {
		    	System.out.println("Config file failed while loading");
		    	try {
					Thread.sleep(10000);
				} catch (Exception e2) {
					// TODO: handle exception
				}
		    }
		    
		}
		else{
			m.init();
		}
		m.createServer();
		
		m.client = new PeerNode[m.peerNumber+m.sourceNumber+m.leaving];
		m.sources = new PeerNode[m.sourceNumber];
		new SchedulingExecutor(System.currentTimeMillis()).scheduleAtFixedRate(
															new Runnable(){	public void run(){m.createClient();}},
															m.creationRate,m.creationRate,TimeUnit.MILLISECONDS,m.peerNumber+m.sourceNumber);
		
		m.run();
		
		if(!m.isAutomated){
			logger.setLevel(Level.TRACE);
			logger = Logger.getLogger("logger222");
		}
	}	
	
	public void addClient(){
		if(maxPeers<peerNumber+sourceNumber+leaving){
			client[maxPeers]= new PeerNode(false,serverAddress,portStart+maxPeers,false,0,this);
			maxPeers++;
		}
	}
	
	public void createClient(){
		tracker.members.clearPartners();
		double rand=Math.random();
		
		if(maxPeers<sourceNumber){
			client[maxPeers]= new PeerNode(true,serverAddress,portStart+maxPeers,false,0,this);
			sources[maxPeers] = client[maxPeers];
			sources[maxPeers].scheduler.setWholeBits(0, leadFactor,1);
			maxPeers++;
			currentSources++;
			if(maxPeers == sourceNumber){
				new SchedulingExecutor(System.currentTimeMillis()).scheduleAtFixedRate(
						new Runnable(){	public void run(){refreshVideoBuffer();}},
						refreshVideoBufferRate,refreshVideoBufferRate,TimeUnit.MILLISECONDS);				
			}
		}
		else{
			if(maxPeers<(peerNumber+sourceNumber)){
				int currentPeers=maxPeers-currentSources;
				if(currentPeers%leavingrate==0&& currentPeers>=peerLimit){
					int range = Math.abs((int)SimulableSystem.currentTimeMillis()-(Node.videoSize*1000));
					int time =(int)((Math.random()*range)+2000);
					client[maxPeers]= new PeerNode(false,serverAddress,portStart+maxPeers,true,time,this);
					
				}
				else
					client[maxPeers]= new PeerNode(false,serverAddress,portStart+maxPeers,false,0,this);
				
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
	
	private void refreshVideoBuffer(){
		int now = (int)SimulableSystem.currentTimeMillis();
		int diff= 0;
		PeerNode aSource = null;
		for(int i = 0; i < sourceNumber;i++){
			aSource = sources[i];
			
			diff = (now - 0)/1000;
			System.out.println(now+" - "+aSource.startTime+"/1000 = "+diff);
			aSource.scheduler.setWholeBits(0, (diff+leadFactor),1);
		}
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
	 
	
	 public  void displayEnd(){
		 System.out.println("END");
		 if(!isAutomated){
			 TimeSlot sSlot1 = new TimeSlot();
			 double CIs[] = new double[client.length]; 		 
			 for(int i=0;i<client.length;i++){
			 if(client[i]==null)
				 continue;
			 if(!client[i].isSource)
				 logger.info("[Peer "+client[i].port+"] : Not source ");
			 else
				 logger.info("[Peer "+client[i].port+"] : Source");
			 	 
			 double CI=((double)client[i].continuityIndex)/((double)client[i].allIndex);
			 
			 if(Double.isNaN(CI))
					CI=1;
			 CIs[i]=CI;
			 sSlot1.add(new DataStructure(CIs,""));
			 visual[4].add(sSlot1);
			 			 
			 logger.info("continuity index = "+CI);
			 logger.info("ALL got = "+(double)client[i].continuityIndex);
			 logger.info("Must get = "+(double)client[i].allIndex);
			 logger.info("Joined at time = "+client[i].joinTime);
			 logger.info("Must play from = "+client[i].playTime);
			 String partners = "My Partners are  : ";
			 int x = client[i].partners.getCache().length;
			 for(int j=0;j < x;j++){
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
		 double AVG=collectCI();
		 
		 System.err.println("counts    ---> "+counts);
		 System.err.println("slotcount ---> "+slotcount);
		 System.err.println("CI ---> "+AVG);
		 tracker.protocol.acceptor.unbindAll();
		 for(int i=0;i<client.length;i++){
			 client[i].protocol.disconnect();
		 }
		 }
		 else{
			 /*
			 double sum=0;
			 for(int i=0;i<ciBuffer.size();i++){
				 Double ci= ciBuffer.get(i);
				 sum+=ci.doubleValue();
			 }
			 double AVG = sum/ciBuffer.size();
			 */
			 double AVG=collectCI();
			 System.err.println("CI = "+AVG);
			 Properties properties= new Properties();
			 try {
				 properties.load(new FileInputStream(filename));
			     properties.setProperty(input,new Double(AVG).toString());
			     properties.store(new FileOutputStream(filename),"append");
			        
			    } 
			 catch (Exception e) {
			 				 System.err.println(AVG);
			   }

			 
		 }
	 }
	 
	 @Override
	 public void postSimulationLoop() {
		 super.preSimulationLoop();
		 System.out.println(SimulableSystem.currentTimeMillis());
		 displayEnd();
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
				int missed=(client[i].playTime-client[i].startTime);
				/*if(i==8){
					
					System.err.println("now="+Scheduler.getInstance().now+" , join= "+client[i].joinTime+" , vsize+starttime= "+((client[i].videoSize*1000)+(client[i].startTime)));
				}*/
				if(now>((client[i].videoSize*1000)+(client[i].startTime))){
				
					System.err.println("BREAAAAKKKKKK");
					break;
					//client[i].allIndex=client[i].videoSize-(missed/1000);
					//continue;
				}
				int mustHave=now-(client[i].playTime);
				client[i].allIndex=(mustHave/1000)+1;
				
			}
			if(!isAutomated){
			fillMCacheOverPeers();
			fillPCacheOverPeers();
			fillPScoreOverPeers();
			fillPScoreOverNetwork();
			//fillContinuityIndex();
			partnershipStat();
			}
			else{
				//collectCI();
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
			new SchedulingExecutor(System.currentTimeMillis()).schedule(new Runnable(){
											public void run(){
												gatherInfo();
											}},
											snapShotRate, TimeUnit.MILLISECONDS);
	}

	double collectCI(){
		double CIs[] = new double[client.length]; 
		for(int i=0;i<client.length;i++)
			if(client[i]!=null){
				if(!client[i].isSource){
					//if(client[i].isLeaving){
					//	int mustHave = client[i].leavingTime+client[i].joinTime;
					//}
					//else{
						double CI=(((double)client[i].continuityIndex)/((double)client[i].allIndex));
						if(Double.isNaN(CI))
							CI=1;
						CIs[i]=CI;
					//}
				}
				else {
					CIs[i]=-1;
				}
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
			return 1;
		Double avg = new Double(sum/count);
		ciBuffer.add(avg);
		return avg;
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
				//if(client[i].port==39)
					//System.err.println("------> "+client[i].continuityIndex+" - "+client[i].allIndex);
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
