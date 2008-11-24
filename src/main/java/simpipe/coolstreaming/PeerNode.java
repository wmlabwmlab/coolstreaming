package simpipe.coolstreaming;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.Timer;
import se.peertv.peertvsim.executor.SchedulingExecutor;
import simpipe.coolstreaming.implementations.CoolStreamingScheduler;
import simpipe.coolstreaming.implementations.RandomMembership;
import simpipe.coolstreaming.implementations.RandomPartnership;
import simpipe.coolstreaming.implementations.RandomScheduler;

import org.apache.mina.common.IoSession;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;


public class PeerNode extends Node {
    
	boolean searching=true;
    public PeerProtocol protocol;
    int time=0;
    IoSession serverBond;
    int i=0;
    
    PeerNode(boolean source,SocketAddress serverAdderess,int port){
    	isSource=source;

    	this.port=port;
    	protocol = new PeerProtocol(serverAdderess,this); 
    }


    public void reboot(){ 
    	System.out.println("reboot is called....");    	
    	if((this.searching||partners.getLength()==0)&&i++==4){
    	
    		this.deputyHops=4;
    		protocol.connectTo(0);
    		i=0;
    		ControlRoom.logger.warn(port+ " will Reboot now");
    		System.err.println("REBOOOOT "+port);
    	}
    	else{
    		int size= partners.clearPartners();
    		if(size==0){
    			this.deputyHops=4;
        		protocol.connectTo(0);
    		}
    			
    		scheduler.identifyRequiredSegments();
    	}
		/*
		 * this section has been commented to import the new sim
		 */    	
//    	try{
//			new Timer(bootTime,this,"reboot",0);
//		}
//    		catch(Exception e){
//    			e.printStackTrace();
//		}
		
    }
    
    void beginSceduling(){
		/*
		 * this section has been commented to import the new sim
		 */
//    	try{
//    		if(!isSource){
//    			new Timer(scheduler.getExchangeTime(),scheduler,"exchangeBM",0);
//    		}
//    	}
//		catch(Exception e){
//		}
		if(!isSource){
			new SchedulingExecutor(System.currentTimeMillis()).scheduleAtFixedRate(new Runnable(){
												public void run(){scheduler.exchangeBM();}
												},
					  scheduler.getExchangeTime(), scheduler.getExchangeTime(), TimeUnit.MILLISECONDS);
		}
    }
    
 	public void initalizeNode() throws Exception{
    
 		//wiring objects using spring
 		try{
// 			System.out.println("node: "+getPort()+" is initalizeNode ");
// 			 BeanFactory factory = new XmlBeanFactory(new FileSystemResource("src/main/java/simpipe/coolstreaming/resources/beans.xml"));
// 			 Package p = (Package)factory.getBean("app_bean");	
// 			 members=p.getMembers();
// 	 		 partners=p.getPartners();
// 	 		 scheduler=p.getScheduler();
 			 members = new RandomMembership();
 	 		 partners = new RandomPartnership();
 	 		 scheduler = new CoolStreamingScheduler();
 			 members.setParams(mSize,port,deleteTime);
 	 		 partners.setParams(pSize,port,windowSize,defaultBandwidth,this);
 	 		 scheduler.setParams(this,startTime);
 			
 		 }
 		 catch(Exception e){
 			 System.out.println("Error Parsing File");
 			 e.printStackTrace();
 			 
 		 }
 		
		gossip= new Gossip(this);
		/*
		 * this section has been added to import the new sim
		 */
//		executor.scheduleAtFixedRate(new Runnable(){
//										public void run(){gossip.initiate();}},
//										gossip.gossipTime, gossip.gossipTime, TimeUnit.MILLISECONDS);
		bandwidth=(int)((Math.random()*512)+100);
		
		/*
		 * this section has been commented to import the new sim
		 */
//		new Timer(bootTime,this,"reboot",0);
		new SchedulingExecutor(System.currentTimeMillis()).scheduleAtFixedRate(new Runnable(){
			public void run(){reboot();}},
			bootTime, bootTime, TimeUnit.MILLISECONDS);
    }

    public void sessionClosed() {
    	partners.clearPartners();
     }
    
    
    
 }