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
	Partnership partners;
	
    public final int mSize=20;
    int[] mCache =new int[mSize];
    Timer[] mTimer =new Timer[mSize];
    
    int deputyHops=4;
    int deleteTime=60000; // time to delete the members from the mCache
    int bootTime=3000;
    
    int continuityIndex;
    int allIndex; // it is all the segments that I've received (doesnt matter if its deadline came or not when it is received)
   
    int bandwidth;
    final int defaultBandwidth=256;
    
//////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\   
    
    //Gossip parameters
    Gossip gossip;
    int gossipTime=6000;
    final int hops=3;
    
//////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\      
    
    //Video parameters
    int windowSize=30;	//30 sec
    int videoSize=500;  //used to be 120sec 
    BitField myBuffer;
    Schedule scheduler;
    int exchangeTime=30000;//request map every 5 sec
    
//////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\   
    
    //exiperiment limit 
    int gossipNumber;
    int exchangeNumber;
    int gossipLimit=4;
    int exchangeLimit=40;//used to be 25
    
//////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\       
    
   
    
    synchronized int getLength(int a[]){ //utility function to calculate a length of an array 
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
    
    int getClosest(int []arr,int basePort,int incomingPort){ //used to compare which member is the closest to have priority of adding members 
    	int diff=Math.abs(basePort-incomingPort);
    	int index=-1;
    	for(int i=0;i<arr.length;i++)
    		if(Math.abs(arr[i]-basePort)>diff){
    			index=i;
    			break; //opto -->replace with incomingPort=arr[i];
    		}
    	return index;
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
    
    int getMember(int i){
    	return mCache[i];
    }
    int getIndex(int arr[],int value){
    	for(int i=0;i<arr.length;i++)
    		if(arr[i]==value)
    			return i;
    	return -1;
    }
    
    int getDefaultBandwidth(){
    	return defaultBandwidth;
    }

}
