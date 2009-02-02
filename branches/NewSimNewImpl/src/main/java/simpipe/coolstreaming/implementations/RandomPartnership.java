package simpipe.coolstreaming.implementations;

import org.apache.mina.common.IoSession;

import simpipe.coolstreaming.BitField;
import simpipe.coolstreaming.Constants;
import simpipe.coolstreaming.PeerNode;
import simpipe.coolstreaming.interfaces.Partnership;

/*
 * this class is responsible for handling all the operations that concerns the partners of each node
 * 
 */

public class RandomPartnership implements Partnership {

	private Partner[] pCache ;
    int port;
    int windowSize=0;
    int defaultBandwidth;
    PeerNode node;
    int pSize=0;
    
    public RandomPartnership(){
    	
    }
    public RandomPartnership(int pSize,int port,int windowSize,int defaultBandwidth, PeerNode node ){
    	this.port=port;
    	this.windowSize=windowSize;
    	this.defaultBandwidth=defaultBandwidth;
    	pCache =new Partner[pSize];
        this.pSize=pSize;
        this.node=node;
    }
    
    @Override
    public void setParams(int pSize, int port, int windowSize,int defaultBandwidth, PeerNode node) {
    	this.port=port;
    	this.windowSize=windowSize;
    	this.defaultBandwidth=defaultBandwidth;
    	pCache =new Partner[pSize];
        this.pSize=pSize;
        this.node=node;
    	
    }
    
    //gets the number of partners
	@Override
    public synchronized int getLength(){ 
    	int sum=0;
    	for(int i=0;i<pCache.length;i++)
    		if(pCache[i]!=null)
    			sum++;
    	return sum;
    }
    
    //get random set of partners to be sent to the new joining node
	@Override
	public synchronized String getPartners(){ //return a set of partners to make the incoming peer connect to them
    	String candidates="";
    	for(int i=0;i<pSize-1;i++)
    		if (pCache[i]!=null)
    			candidates=candidates+pCache[i].port+"-";
    		
    	return candidates;	
    }
    
    // adds partner to the partner's cache
	@Override
	public synchronized boolean addPartner(int port,IoSession session,boolean isTracker){ //add anew partner to my partner's cache

		if(isTracker){
			if(getLength()==pSize){
				return false;
			}
			else{
				for(int i=0;i<pCache.length;i++)
		    		if(pCache[i]==null){
		    			pCache[i]= new Partner(port,defaultBandwidth,session,new BitField(windowSize));
		    			return true;
		    		}
			}
		}
		
    	if(port > Constants.SERVER_PORT)
    		port -= Constants.SERVER_PORT;

    	if(getLength() + node.protocol.committed < pSize && getIndex(port) == -1){
    		for(int i=0;i<pCache.length;i++)
    		if(pCache[i]==null){
    			pCache[i]= new Partner(port,defaultBandwidth,session,new BitField(windowSize));
    			return true;
    		}
    	}
    	else if(node.committed.size()>0){
    		for(int i=0;i<pCache.length;i++)
        		if(pCache[i]==null){
        			IoSession session2=node.committed.get(0);
            		node.protocol.committed--;
            		session2.close();
            		node.committed.remove(0);
            		pCache[i]= new Partner(port,defaultBandwidth,session,new BitField(windowSize));
        			return true;
        		}
    		
    	}
    	return false;
    }
    
    //add partner when it is obligatory to add one (i.e. when the number of remaining hops of the new joining node is zero)
	@Override
	public synchronized void forceAddPartner(int port,IoSession session){
    	if(port > Constants.SERVER_PORT)
    		port -= Constants.SERVER_PORT;
    	int rand=(int)Math.round((Math.random()*getLength()));
    	if(rand>=pSize)
    		rand=0;
    	if(pCache[rand]!=null){
    		pCache[rand].session.write(""+Constants.LEAVE_NETWORK+this.port);
    		session.close();
    		System.out.println("me "+port+" kicked "+pCache[rand].port);
    	}
    	pCache[rand] = new Partner(port,defaultBandwidth,session,new BitField(windowSize));
    }
    
	@Override
	public synchronized int getAnotherDeputy(int destPort){ 
//modified during the impoting of new sim			
			if(getLength()==0 || (getLength() == 1 && getIndex(destPort) != -1))
				return destPort;
			
			int[] temp=null;
			if(getIndex(destPort)==-1)
	    		temp=new int[getLength()];
	    	else
	    		temp=new int[getLength()-1];
	    	
	    	int j=0;
	    	for(int i=0;i<pCache.length;i++)
	    		if(pCache[i]!=null&&pCache[i].port!=destPort)
	    			temp[j++]=pCache[i].port;
	    	int rand=(int)Math.round((Math.random()*temp.length));
	    	if(rand>=temp.length)
	    		rand=0;
	    	return temp[rand];
	    }
	
	@Override
    public synchronized void setBandwidth(int port, int bandwidth){
    	int index=getIndex(port);
    	if(index!=-1){
    		pCache[index].bandwidth=bandwidth;
    	}
    }
    
	@Override
	public synchronized void deletePartner(int port){
    	if(port > Constants.SERVER_PORT)
    		port -= Constants.SERVER_PORT;
    	int index=getIndex(port);
    	if(index!=-1){
    		pCache[index].session.close();
    		pCache[index]=null;
    	}
    }
    
    //deletes any partner who has left the network without sending departure message
	@Override
	public synchronized int clearPartners(){
    	for(int i=0;i<pSize;i++)
    		if(pCache[i]!=null)
    		if(pCache[i].session!=null){
    		if(pCache[i].session.isClosing()||!pCache[i].session.isConnected()||pCache[i].port==port||pCache[i].port == Constants.SERVER_PORT){
    			pCache[i]=null;
    			if(node==null)
    			System.out.println("someone deleted meeeeeeeee so I'll delete him");
    		}
    		}
    		else{
    			pCache[i]=null;
    		}
    		return getLength();	
    }
	
	@Override
	public Partner getPartner(int i){
    	return pCache[i];
    }
   
	@Override
	public void setPartner(int index,Partner p){
    	pCache[index]=p;
    }
    
	@Override
    public int getIndex(int value){
    	for(int i=0;i<pSize;i++)
    		if(pCache[i]!=null)
    		if(pCache[i].port==value)
    			return i;
    	return -1;
    }
	
	@Override
    public Partner[] getCache(){
    	return pCache;
    }
    
	@Override
    public int[] toArray(){
    	int[] result = new int[pCache.length];
    	for(int i=0;i<pCache.length;i++)
    		if(pCache[i]==null)
    			result[i]=0;
    		else
    			result[i]=pCache[i].port;
    	return result;
    }
	public void subscribe(char header){
		
	}
	public void fire(char header){
		
	}
}