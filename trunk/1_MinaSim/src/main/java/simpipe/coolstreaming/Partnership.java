package simpipe.coolstreaming;

import org.apache.mina.common.IoSession;

/*
 * this class is responsible for handling all the operations that concerns the partners of each node
 * 
 */

public class Partnership {

	Partner[] pCache ;
    int port;
    int windowSize=0;
    int defaultBandwidth;
    
    int pSize=0;
    
    Partnership(int pSize,int port,int windowSize,int defaultBandwidth){
    	this.port=port;
    	this.windowSize=windowSize;
    	this.defaultBandwidth=defaultBandwidth;
    	pCache =new Partner[pSize];
        this.pSize=pSize;
    }
    
    //gets the number of partners
    synchronized int getLength(){ 
    	int sum=0;
    	for(int i=0;i<pCache.length;i++)
    		if(pCache[i]!=null)
    			sum++;
    	return sum;
    }
    
    //get random set of partners to be sent to the new joining node
    synchronized String getPartners(){ //return a set of partners to make the incoming peer connect to them
    	String candidates="";
    	for(int i=0;i<pSize-1;i++)
    		if (pCache[i]!=null)
    			candidates=candidates+pCache[i].port+"-";
    		
    	return candidates;	
    }
    
    // adds partner to the partner's cache
    synchronized boolean addPartner(int port,IoSession session){ //add anew partner to my partner's cache
    	if(getLength()==pSize){
    			session.write("t"+this.port);
    			session.close();
    			System.out.println("Me "+this.port+" refusing "+port);
    			return false;
    		}
    	if(port>CentralNode.PORT)
    		port-=CentralNode.PORT;
    	int index=getIndex(port);
    	if(index==-1){
    		for(int i=0;i<pCache.length;i++)
    		if(pCache[i]==null){
    			Partner p = new Partner();
    			p.port=port;
    			p.session=session;
    			p.bufferMap=new BitField(windowSize);
    			p.bandwidth=defaultBandwidth;
    			session.write("m");
    			pCache[i]=p;
    		    return true;
    		}
    	}
    	return false;
    }
    
    //add partner when it is obligatory to add one (i.e. when the number of remaining hops of the new joining node is zero)
    synchronized void forceAddPartner(int port,IoSession session){
    	if(port>CentralNode.PORT)
    		port-=CentralNode.PORT;
    	int rand=(int)Math.round((Math.random()*getLength()));
    	if(rand>=pSize)
    		rand=0;
    	if(pCache[rand]!=null){
    		pCache[rand].session.write("t"+this.port);
    		session.close();
    		System.out.println("me "+port+" kicked "+pCache[rand].port);
    	}
		Partner p = new Partner();
		p.port=port;
		p.session=session;
		p.bufferMap=new BitField(windowSize);
		p.bandwidth=defaultBandwidth;
		pCache[rand]=p;
		session.write("m");	
    }
    
    synchronized void setBandwidth(int port, int bandwidth){
    	int index=getIndex(port);
    	if(index!=-1){
    		pCache[index].bandwidth=bandwidth;
    	}
    }
    synchronized void deletePartner(int port){
    	if(port>CentralNode.PORT)
    		port-=CentralNode.PORT;
    	int index=getIndex(port);
    	if(index!=-1){
    		pCache[index].session.close();
    		pCache[index]=null;
    	}
    		
    }
    
    //deletes any partner who has left the network without sending departure message
    synchronized void clearPartners(){
    	for(int i=0;i<pSize;i++)
    		if(pCache[i]!=null)
    		if(pCache[i].session!=null){
    		if(pCache[i].session.isClosing()||!pCache[i].session.isConnected()||pCache[i].port==port)
    			pCache[i]=null;
    		}
    		else{
    			pCache[i]=null;
    		}
    			
    }
    Partner getPartner(int i){
    	return pCache[i];
    }
    
    int getIndex(int value){
    	for(int i=0;i<pSize;i++)
    		if(pCache[i]!=null)
    		if(pCache[i].port==value)
    			return i;
    	return -1;
    }
    
	
    
}

class Partner{
	
	int port;
	int bandwidth;
	IoSession session;
	BitField bufferMap;
	
	
}




