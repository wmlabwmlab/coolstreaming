package simpipe.coolstreaming;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import se.peertv.peertvsim.core.Timer;

public class Node extends IoHandlerAdapter{
	
	public int port;
	boolean isSource=false;
	
	int dummy=400;
	//main coolstreaming parameters
	public final int pSize=4;
    public final int mSize=20;
    int[] mCache =new int[mSize];
    Timer[] mTimer =new Timer[mSize];
    int[] pCache =new int[pSize];
    IoSession pSession[]=new IoSession[pSize];
    int[] pBandwidth =new int[pSize];
    int deputyHops=8;
    int deleteTime=60000;
    
    //Gossip parameters
    Gossip gossip;
    int gossipTime=3000;
    final int hops=4;
    
    //Video parameters
    int windowSize=30;	//30 sec
    int videoSize=500;  //used to be 120sec 
    BitField BM[]= new BitField[pSize];
    BitField myBuffer;
    Schedule scheduler;
    int exchangeTime=30000;//request map every 5 sec
    
    //exiperiment limit 
    int gossipNumber;
    int exchangeNumber;
    int gossipLimit=4;
    int exchangeLimit=40;//used to be 25
    
    int continuityIndex;
    int allIndex;
    
    int bandwidth;
    final int defaultBandwidth=256;
   
    
    synchronized int getLength(int a[]){ 
    	int sum=0;
    	for(int i=0;i<a.length;i++)
    		if(a[i]!=0)
    			sum++;
    	return sum;
    }
    synchronized int getAnotherDeputy(int destPort){
    	int[] temp=new int[getLength(mCache)];
    	if(temp.length==0)
    		return destPort;
    	int j=0;
    	for(int i=0;i<mCache.length;i++)
    		if(mCache[i]!=0)
    			temp[j++]=mCache[i];
    	int rand=(int)Math.round((Math.random()*getLength(temp)));
    	if(rand>=temp.length)
    		rand=0;
    	return temp[rand];
    }
    
    
    
    synchronized String getPartners(){ //return a set of partners to make the incoming peer connect to them
    	String candidates="";
    	for(int i=0;i<pSize-1;i++)
    		if (pCache[i]!=0)
    			candidates=candidates+pCache[i]+"-";
    		
    	return candidates;	
    }

    synchronized void addPartner(int port,IoSession session){ //add anew partner to my partner's cache
    	if(getLength(pCache)==pSize){
    			session.write("t"+this.port);
    			session.close();
    			System.out.println("Me "+this.port+" refusing "+port);
    			return;
    		}
    	if(port>CentralNode.PORT)
    		port-=CentralNode.PORT;
    	int index=getIndex(pCache,port);
    	if(index==-1){
    		for(int i=0;i<pCache.length;i++)
    		if(pCache[i]==0){
    			pCache[i]=port;
    			pSession[i]=session;
    			BM[i]=new BitField(windowSize);
    			pBandwidth[i]=defaultBandwidth;
    			session.write("m");
    		    break;
    		}
    		addMember(port);
    	}
    }
    synchronized void forceAddPartner(int port,IoSession session){
    	if(port>CentralNode.PORT)
    		port-=CentralNode.PORT;
    	addMember(port);
    	int rand=(int)Math.round((Math.random()*getLength(pCache)));
    	if(rand>=pSize)
    		rand=0;
    	System.out.println("now I deleted "+pCache[rand]+" and inserted"+port);
    	pCache[rand]=port;
    	pSession[rand].write("t"+this.port);
    	pSession[rand]=session;
    	BM[rand]=new BitField(windowSize);
		pBandwidth[rand]=defaultBandwidth;
		session.write("m");
    	
    	
    }
    
    synchronized void setBandwidth(int port, int bandwidth){
    	int index=getIndex(pCache,port);
    	if(index!=-1){
    		pBandwidth[index]=bandwidth;
    	}
    }
    
    synchronized void addMember(int port){
    	
    	int index=getIndex(mCache,port);
    	if(index!=-1){
    		try{
    			mTimer[index].reset();
    			mTimer[index]=new Timer(deleteTime,(Object)this,"delete",port);
    		}
    		catch (Exception e) {
				// TODO: handle exception
			}
    	}
    	else{
    		if(getLength(mCache)==mSize){ //the member buffer is full
    			int i=getClosest(mCache,this.port,port);
    			if(i==-1){
    				return;
    			}
    			else{
    				mCache[i]=port;
	    			try{
	    				Timer t=new Timer(deleteTime,(Object)this,"delete",port);
	    				mTimer[i]=t;
	    			}
	    			catch (Exception e) {
					}
    			}
    			}
    		else{
    			for(int i=0;i<mCache.length;i++)
    	    		if(mCache[i]==0){
    	    			mCache[i]=port;
    	    			if(port!=CentralNode.PORT)
    	    			try{
    	    				Timer t=new Timer(deleteTime,(Object)this,"delete",port);
    	    				mTimer[i]=t;
    	    			}
    	    			catch (Exception e) {
    					}
    	    			break;
    	    		}
    		}
    	}
    }
    
    int getClosest(int []arr,int basePort,int incomingPort){ //
    	int diff=Math.abs(basePort-incomingPort);
    	int index=-1;
    	for(int i=0;i<arr.length;i++)
    		if(Math.abs(arr[i]-basePort)>diff){
    			index=i;
    			break; //opto -->replace with incomingPort=arr[i];
    		}
    	return index;
    }
	
    
    synchronized void deletePartner(int port){
    	if(port>CentralNode.PORT)
    		port-=CentralNode.PORT;
    	for(int i=0;i<pCache.length;i++)
    		if(pCache[i]==port){
    			pCache[i]=0;
    			pSession[i].close();
    			pBandwidth[i]=0;
    		}
    		
    }
    synchronized void clearPartners(){
    	for(int i=0;i<pSize;i++)
    		if(pSession[i]!=null)
    		if(pSession[i].isClosing()||!pSession[i].isConnected()){
    			pSession[i]=null;
    			pCache[i]=0;
    			BM[i]=null;
    			pBandwidth[i]=0;
    		}
    }
    synchronized void deleteMember(int port){
    	for(int i=0;i<mCache.length;i++)
    		if(mCache[i]==port){
    			mCache[i]=0;
    			try{
    				mTimer[i].reset();
    				mTimer[i]=null;
    			}
    			catch (Exception e) {
					// TODO: handle exception
				}
    			return;
    		}
    }
    int getPartner(int i){
    	return pCache[i];
    }
    int getMember(int i){
    	return mCache[i];
    }
    int getIndex(int arr[],int value){
    	for(int i=0;i<arr.length;i++)
    		if(arr[i]==value)
    			return i;
    	return -1;
    }
    IoSession getPartnerSession(int i){
    	return pSession[i];
    }

}
