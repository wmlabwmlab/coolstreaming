package simpipe.coolstreaming;

import java.net.SocketAddress;

import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.Timer;

import org.apache.mina.common.IoSession;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;


public class PeerNode extends Node {
    
	boolean searching=true;
    public PeerProtocol protocol;
    int time=0;
    IoSession serverBond;
    
    PeerNode(boolean source,SocketAddress serverAdderess,int port){
    	isSource=source;
		protocol = new PeerProtocol(serverAdderess,this); 
		this.port=port;
    }
    int i=0;

    public void reboot(int dull){ 
    	
    	if((this.searching||partners.getLength()==0)&&i++==4){
    		this.deputyHops=4;
    		protocol.connectTo(0);
    		i=0;
    		ControlRoom.logger.warn(port+ " will Reboot now");
    		//System.err.println("REBOOOOT "+port);
    	}
    	else{
    		int size= partners.clearPartners();
    		if(size==0){
    			this.deputyHops=4;
        		protocol.connectTo(0);
    		}
    			
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
 	 		 scheduler.setParams(this,startTime);
 	 		 partners.setParams(pSize,port,windowSize,defaultBandwidth,this);
 	 		 members.setParams(mSize,port,deleteTime);
 			
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