package simpipe.coolstreaming.implementations;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import se.peertv.peertvsim.core.Timer;
import se.peertv.peertvsim.executor.SchedulingExecutor;
import simpipe.coolstreaming.interfaces.Membership;
import simpipe.coolstreaming.interfaces.Partnership;

public class RandomMembership implements Membership {
	private Member[] mCache;
	int mSize=0;
	int port;
	int deleteTime=30000;
	
	public RandomMembership() {
	}

	public RandomMembership(int mSize,int port,int deleteTime){
    	this.port=port;
    	mCache =new Member[mSize];
        this.mSize=mSize;
        this.deleteTime = deleteTime;
    }
	
	@Override
	public void setParams(int mSize, int port, int deleteTime) {
		// TODO Auto-generated method stub
		this.port=port;
    	mCache =new Member[mSize];
        this.mSize=mSize;
        this.deleteTime = deleteTime;
	}
	@Override
	public synchronized int getLength(){ 
    	int sum=0;
    	for(int i=0;i<mCache.length;i++)
    		if(mCache[i]!=null)
    			sum++;
    	return sum;
    }
	
	@Override	
	public int getIndex(int value){
	    	for(int i=0;i<mSize;i++)
	    		if(mCache[i]!=null)
	    		if(mCache[i].port==value)
	    			return i;
	    	return -1;
	}
	
	@Override	
	public synchronized void addMember(final int port){
 		try{	    	
	    	int index=getIndex(port);
	    	if(index!=-1){
    			/*
    			 * this section has been commented to import the new sim
    			 */
//	    		mCache[index].timer.reset();
//	    		mCache[index].timer=new Timer(deleteTime,(Object)this,"deleteMember",port);
	    		mCache[index].scheduledTask.cancel(true);
	    		if(mCache[index].scheduledTask.isCancelled())
	    			mCache[index].scheduledTask = new SchedulingExecutor(System.currentTimeMillis()).schedule(new Runnable(){
	    											public void run(){deleteMember(port);}},
	    												deleteTime, TimeUnit.MILLISECONDS);	    		
	    	}
    		/*
    		 * this section has been commented to import the new sim
    		 */	    	
//	    	else{
//	    		/*
//	    		 * this section has been commented to import the new sim
//	    		 */
//    			Member m = new Member(port,new Timer(deleteTime,(Object)this,"deleteMember",port));
	    	
	    	if(index == -1 || !mCache[index].scheduledTask.isCancelled()){
	    		if(index != -1) // this means that the scheduledTask is not cancelled 
	    			mCache[index] = null;
    			Member m = new Member(port,
    					new SchedulingExecutor(System.currentTimeMillis()).schedule(new Runnable(){
    											public void run(){deleteMember(port);}},
    												deleteTime, TimeUnit.MILLISECONDS));
	    	if(getLength()==mSize){ //the member buffer is full
	    			int i = ((int)(Math.random()*mSize))%mSize;
//    				mCache[i].timer.reset();  //has been commented to import the new sim
		    		mCache[i]=m;
	    		}
	    		else{
	    			for(int i=0;i<mCache.length;i++)
	    	    		if(mCache[i]==null){
	    		    		mCache[i]=m;
	    	    			break;
	    	    		}
	    		}
	    	}
    	}
    	catch (Exception e) {
				e.printStackTrace();
		}
	  }
	 
	@Override	 
	public synchronized void deleteMember(int port){
	    	for(int i=0;i<mCache.length;i++)
	    		if(mCache[i]!=null)
	    		if(mCache[i].port==port){
	    			try{
//	    				mCache[i].timer.reset();  //has been commented to import the new sim
	    				mCache[i].scheduledTask.cancel(true);
	    			}
	    			catch (Exception e) {
	    				e.printStackTrace();
					}
	    			mCache[i]=null;
	    			return;
	    		}
	    }
	 
	@Override
	public synchronized int getAnotherDeputy(int destPort){ 
	    	int[] temp=new int[getLength()];
	    	if(temp.length==0)
	    		return destPort;
	    	int j=0;
	    	for(int i=0;i<mCache.length;i++)
	    		if(mCache[i]!=null)
	    			temp[j++]=mCache[i].port;
	    	int rand=(int)Math.round((Math.random()*temp.length));
	    	if(rand>=temp.length)
	    		rand=0;
	    	return temp[rand];
	    }
	    
	@Override
	public int[] toArray(){
	    	int[] result = new int[mCache.length];
	    	for(int i=0;i<mCache.length;i++)
	    		if(mCache[i]==null)
	    			result[i]=0;
	    		else
	    			result[i] = mCache[i].port;
	    	return result;
	    }
	
	@Override 
	public Member getMember(int index){
		 return mCache[index];
	 }
	
	@Override
	public void setMember(int index,Member m){
		 mCache[index]=m;
	 }
	public void subscribe(char header){
		
	}
	public void fire(char header){
		
	}

	@Override
	public int getStranger(Partnership partnership) {
		ArrayList<Integer> strangers = new ArrayList<Integer>();
		for(int i=0;i<getLength();i++){
			if(getMember(i)==null)
				continue;
			int port=getMember(i).port;
			if(partnership.getIndex(port)==-1){
				strangers.add(port);
			}
		}
		int size=strangers.size();
		if(size==0)
			return -1;
		int rand=(int)(Math.random()*size);
	    if(rand==size)
	    	rand=0;
	    int port= strangers.get(rand);
		return port;
	}
	
}