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
    public int committed=0;
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

    	if(port > Constants.SERVER_PORT)
    		port -= Constants.SERVER_PORT;

    	if(getLength() + committed < pSize && getIndex(port) == -1){
    		for(int i=0;i<pCache.length;i++)
    		if(pCache[i]==null){
    			pCache[i]= new Partner(port,defaultBandwidth,session,new BitField(windowSize));
    			return true;
    		}
    	}
    	return false;
    }
    
    //add partner when it is obligatory to add one (i.e. when the number of remaining hops of the new joining node is zero)
    synchronized void forceAddPartner(int port,IoSession session){
    	if(port > Constants.SERVER_PORT)
    		port -= Constants.SERVER_PORT;
    	int rand=(int)Math.round((Math.random()*getLength()));
    	if(rand>=pSize)
    		rand=0;
    	if(pCache[rand]!=null){
    		pCache[rand].session.write("t"+this.port);
    		session.close();
    		System.out.println("me "+port+" kicked "+pCache[rand].port);
    	}
    	pCache[rand] = new Partner(port,defaultBandwidth,session,new BitField(windowSize));
    }
    
    synchronized void setBandwidth(int port, int bandwidth){
    	int index=getIndex(port);
    	if(index!=-1){
    		pCache[index].bandwidth=bandwidth;
    	}
    }
    synchronized void deletePartner(int port){
    	if(port > Constants.SERVER_PORT)
    		port -= Constants.SERVER_PORT;
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
    		if(pCache[i].session.isClosing()||!pCache[i].session.isConnected()||pCache[i].port==port||pCache[i].port == Constants.SERVER_PORT)
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
    
    int[] toArray(){
    	int[] result = new int[pCache.length];
    	for(int i=0;i<pCache.length;i++)
    		if(pCache[i]==null)
    			result[i]=0;
    		else
    			result[i]=pCache[i].port;
    	return result;
    }
}