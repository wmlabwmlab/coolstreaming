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
			//System.out.println(node.port+" will Do NOTHINGGG WITH "+msgPart2);
			node.beginSceduling();
			node.searching=false;
		}
	}
	public void connectMessage(String msgPart2,IoSession session){
		String []parameters=msgPart2.split("-");
		int destination=Integer.parseInt(parameters[1]);
		
		if(node.getLength(node.pCache)==node.pSize){
			if(Integer.parseInt(parameters[0])==0){
				//System.out.println("Peer "+node.port+"forced to add "+destination);
    			node.forceAddPartner(destination,session);
    			node.addMember(destination);
    			String ports=node.getPartners();
    			session.write("p"+node.port+"-"+ports);
    		}
    		else{
    			int port=node.getAnotherDeputy(destination);
    			session.write("d"+port);
    		}
    	}
		
    	else{
			String ports=node.getPartners();
			session.write("p"+node.port+"-"+ports);
			node.addPartner(destination,session);
			node.addMember(destination);
    	}
	}
	public void partnersMessage(String msgPart2,IoSession session){
		node.searching=false;
		
		String []partners=msgPart2.split("-");
		node.addPartner(Integer.parseInt(partners[0]),session);
		//System.out.print("[Peer "+node.port+"] : now my friends are  : ");
		for(int i=0;i<node.pSize;i++)
			if(node.pCache[i]!=0){}
				//System.out.print(" - "+(node.pCache[i]));
		//System.out.println("\n");
		for(int i=1;i<partners.length;i++)
			node.connectTo(Integer.parseInt(partners[i]));
		
		
	}
	public void acceptMessage(String msgPart2,IoSession session){
		
		int destination=Integer.parseInt(msgPart2);
		node.addPartner(destination, session);
		node.addMember(destination);
		//System.out.println("[Peer "+node.port+"] Accepting connecting to  : "+Integer.parseInt(msgPart2));
		//System.out.print("[Peer "+node.port+"] : now my friends are  : ");
		//for(int i=0;i<node.pSize;i++)
		//	if(node.pCache[i]!=0)
				//System.out.print(" - "+(node.pCache[i]));
		//System.out.println("\n");
	}
	public void terminateConnectionMessage(String msgPart2,IoSession session){
		int destination=Integer.parseInt(msgPart2);
		node.deletePartner(destination);
		//System.out.println("NOODE "+node.port+" DELETED "+destination);
		session.close();
	}
	public void sendBandwidth(IoSession session){
		session.write("n"+node.port+"-"+node.bandwidth);
	}
	public void receiveBandwidth(String msgPart2,IoSession session){
		String []bandwidthParam=msgPart2.split("-");
		int port=Integer.parseInt(bandwidthParam[0]);
		int bandwidth=Integer.parseInt(bandwidthParam[1]);
		node.setBandwidth(port,bandwidth);
	}
	
	public void gossipMessage(String msgPart2){
		String []gParam=msgPart2.split("-");
		int hops=Integer.parseInt(gParam[1]);
		int originalPort=Integer.parseInt(gParam[0]);
		node.addMember(originalPort);
		if(node.getLength(node.pCache)!=node.pSize&&node.getIndex(node.pCache,originalPort)==-1)
			node.connectTo(originalPort);
    	if(hops>0){
    		node.gossip.bridge(originalPort,hops);
    	}
	}
	
	public void bitMapMessage(String msgPart2){
		String []bParam=msgPart2.split("-");
		int src=Integer.parseInt(bParam[1]);
		int time=Integer.parseInt(bParam[0]);
		int index=node.getIndex(node.pCache,src);
		if(index==-1)
			return;
		node.BM[index].setBits(bParam[2],time);
		
		for(int i=0;i<node.pSize;i++){
			if(node.pCache[i]!=0&&node.BM[i].time!=time)
				return;
		}
		BitField field=node.scheduler.beginscheduling() ;
		//System.err.println("&"+field);
		
		int diff=(time-node.scheduler.startTime)/1000;
		for(int i=0;i<node.windowSize;i++){
			if(diff+i>=node.videoSize)
				continue;
			if(node.scheduler.wholeBits[diff+i]==1)
				continue;
			if(field.bits[i]==0)
				continue;
			int dest=field.bits[i];
			int loc=node.getIndex(node.pCache,dest);
			if(loc==-1)
				continue;
			int sum=diff+i;
			//System.err.println("me("+node.port+")sending to ("+node.pCache[loc]+") : x"+sum);
			node.pSession[loc].write("x"+sum);
		}
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
