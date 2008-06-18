package simpipe.coolstreaming;

import java.net.SocketAddress;
import se.peertv.peertvsim.core.Timer;


public class PeerNode extends Node {
    
	boolean searching=true;
    PeerProtocol protocol;
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
    			new Timer(scheduler.exchangeTime,scheduler,"exchangeBM",0);
    		}
    	}
		catch(Exception e){
		}
    }
    
 	public void initalizeNode() throws Exception{
    	
    	//This is the constructor of the PeerNode , its constructor cant be fired when initializing the object , but in stead I waited till first session is created (bootStraping flag) to set some settings
		members=new Membership(mSize,port,deleteTime);
		partners=new Partnership(pSize,port,windowSize,defaultBandwidth); 
		scheduler = new Schedule(this,100);
		gossip= new Gossip(this);
		bandwidth=(int)((Math.random()*512)+100);
		new Timer(bootTime,this,"reboot",0);
    }

    public void sessionClosed() {
    	partners.clearPartners();
     }
 }