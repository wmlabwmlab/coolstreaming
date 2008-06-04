package simpipe.coolstreaming;

import org.apache.mina.common.IoSession;

import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.Timer;

public class Protocol {

	PeerNode node;
	public Protocol(PeerNode node){
		this.node=node;
	}
	public void deputyMessage(String msgPart2,IoSession session){
		session.close();
		String myPort=String.valueOf(node.port);
		if(!msgPart2.equals(myPort)){
    	node.connectTo(Integer.parseInt(msgPart2));
    	node.addMember(Integer.parseInt(msgPart2));
		}
    	else {
			node.beginSceduling();
			node.searching=false;
		}
	}
	public void connectMessage(String msgPart2,IoSession session){
		String []parameters=msgPart2.split("-");
		int destination=Integer.parseInt(parameters[1]);
		
		if(node.partners.getLength()==node.pSize){
			int hops=0;
			try{
				hops=Integer.parseInt(parameters[0]);
			}
			catch(Exception e){
				
			}
			if(hops==0){
				node.partners.forceAddPartner(destination,session);
    			node.addMember(destination);
    			String ports=node.partners.getPartners();
    			session.write("p"+node.port+"-"+ports);
    		}
    		else{
    			int port=node.getAnotherDeputy(destination);
    			session.write("d"+port);
    		}
    	}
		
    	else{
			boolean added=node.partners.addPartner(destination,session);
			if(added){
			String ports=node.partners.getPartners();
			session.write("p"+node.port+"-"+ports);
			node.addMember(destination);
			}
		}
	}
	public void partnersMessage(String msgPart2,IoSession session){
		node.searching=false;
		
		String []partners=msgPart2.split("-");
		node.partners.addPartner(Integer.parseInt(partners[0]),session);
		for(int i=1;i<partners.length;i++){
			node.connectTo(Integer.parseInt(partners[i]));
		}
		
	}
	
	public void acceptMessage(String msgPart2,IoSession session){
		
		int destination=Integer.parseInt(msgPart2);
		node.addMember(destination);
		if(node.partners.getLength()!=node.pSize)
		node.partners.addPartner(destination, session);
		/*
		else{
			session.write("t"+node.port);
			session.close();
		}
		*/
		
		else{
			double rand=Math.random();
			if(rand<0.25)
				node.partners.forceAddPartner(destination, session);
			else{
				session.write("t"+node.port);
    			session.close();
			}
		}
		
	}
	
	public void terminateConnectionMessage(String msgPart2,IoSession session){
		int destination=Integer.parseInt(msgPart2);
		node.partners.deletePartner(destination);
		session.close();
	}
	
	public void sendBandwidth(IoSession session){
		session.write("n"+node.port+"-"+node.bandwidth);
	}
	
	public void receiveBandwidth(String msgPart2,IoSession session){
		String []bandwidthParam=msgPart2.split("-");
		int port=Integer.parseInt(bandwidthParam[0]);
		int bandwidth=Integer.parseInt(bandwidthParam[1]);
		node.partners.setBandwidth(port,bandwidth);
	}
	
	public void gossipMessage(String msgPart2){
		String []gParam=msgPart2.split("-");
		int hops=Integer.parseInt(gParam[1]);
		int originalPort=Integer.parseInt(gParam[0]);
		node.addMember(originalPort);
		if(node.partners.getLength()<node.pSize&&node.partners.getIndex(originalPort)==-1){
			node.connectTo(originalPort);
		}
		if(hops>0){
    		node.gossip.bridge(originalPort,hops);
    	}
	}
	
	public void bitMapMessage(String msgPart2){
		String []bParam=msgPart2.split("-");
		int src=Integer.parseInt(bParam[1]);
		int time=Integer.parseInt(bParam[0]);
		int index=node.partners.getIndex(src);
		if(index==-1)
			return;
		node.partners.pCache[index].bufferMap.setBits(bParam[2],time);
		
	}
	public void pingMessage(String msgPart2,IoSession session){
		session.write("y"+msgPart2);
	}
	public void requestBitMapMessage(String msgPart2,IoSession session){
		int time=Integer.parseInt(msgPart2);
    	if(node.scheduler!=null){
		BitField bitField=node.scheduler.getWindow(time);
    	//System.err.println("me ["+node.port+"] sending : "+"b"+time+"-"+node.port+"-"+bitField.toString());
    	session.write("b"+time+"-"+node.port+"-"+bitField.toString());
    	}
    	else{
    		BitField zeros = new BitField(node.windowSize);
    		session.write("b"+time+"-"+node.port+"-"+zeros);
    	}
	}
	public void pongMessage(String msgPart2){
		int index=Integer.parseInt(msgPart2);
		if(node.scheduler.wholeBits[index]!=1)
		node.allIndex++;
		if(node.scheduler.deadLine[index]<=(Scheduler.getInstance().now/1000)&&node.scheduler.wholeBits[index]!=1)
			node.continuityIndex++;
    	node.scheduler.wholeBits[index]=1;
	}


}
