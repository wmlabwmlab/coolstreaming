package simpipe.coolstreaming;

import java.net.SocketAddress;

import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.Timer;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;


public class PeerNode extends Node {
    
	boolean searching=true;
    public PeerProtocol protocol;
    int time=0;
    
    PeerNode(boolean source,SocketAddress serverAdderess){
    	isSource=source;
		protocol = new PeerProtocol(serverAdderess,this); 
    }
    
    public void reboot(int dull){ 
    	
    	if(this.searching||partners.getLength()==0){
    		this.deputyHops=4;
    		protocol.connectTo(0);	
    	}
    	else{
    		partners.clearPartners();
    		scheduler.identifyRequiredSegments();
    	}
    	
    	try{
			new Timer(bootTime,this,"reboot",0);
		}
    		catch(Exception e){
    			e.printStackTrace();
		}
		
    }
    
    void beginSceduling(){
    	try{
    		if(!isSource){
    			new Timer(scheduler.getExchangeTime(),scheduler,"exchangeBM",0);
    		}
    	}
		catch(Exception e){
		}
    }
    
 	public void initalizeNode() throws Exception{
    
 		//wiring objects using spring
 		try{
 			 
 			 BeanFactory factory = new XmlBeanFactory(new FileSystemResource("src/main/java/simpipe/coolstreaming/resources/beans.xml"));
 			 Package p = (Package)factory.getBean("app_bean");	
 			 members=p.getMembers();
 	 		 partners=p.getPartners();
 	 		 scheduler=p.getScheduler();
 	 		 members.setParams(mSize,port,deleteTime);
 	 		 partners.setParams(pSize,port,windowSize,defaultBandwidth,this);
 	 		 scheduler.setParams(this,startTime);
 			
 		 }
 		 catch(Exception e){
 			 System.out.println("Error Parsing File");
 			 e.printStackTrace();
 			 
 		 }
 		
		gossip= new Gossip(this);
		bandwidth=(int)((Math.random()*512)+100);
		new Timer(bootTime,this,"reboot",0);
    }

    public void sessionClosed() {
    	partners.clearPartners();
     }
    
    
    
 }