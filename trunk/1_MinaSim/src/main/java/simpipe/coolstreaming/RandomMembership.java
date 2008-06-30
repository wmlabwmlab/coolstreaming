package simpipe.coolstreaming;

import se.peertv.peertvsim.core.Timer;
import simpipe.coolstreaming.interfaces.Membership;
import simpipe.coolstreaming.interfaces.Partnership;

public class RandomMembership implements Membership {
	private Member[] mCache;
	int mSize=0;
	int port;
	int deleteTime=30000;
	
	RandomMembership(int mSize,int port,int deleteTime){
    	this.port=port;
    	mCache =new Member[mSize];
        this.mSize=mSize;
        this.deleteTime = deleteTime;
    }

	public synchronized int getLength(){ 
    	int sum=0;
    	for(int i=0;i<mCache.length;i++)
    		if(mCache[i]!=null)
    			sum++;
    	return sum;
    }
	
	public int getIndex(int value){
	    	for(int i=0;i<mSize;i++)
	    		if(mCache[i]!=null)
	    		if(mCache[i].port==value)
	    			return i;
	    	return -1;
	    }
	
	public synchronized void addMember(int port){
 		try{	    	
	    	int index=getIndex(port);
	    	if(index!=-1){
	    			mCache[index].timer.reset();
	    			mCache[index].timer=new Timer(deleteTime,(Object)this,"deleteMember",port);
	    	}
	    	else{
    			Member m = new Member(port,new Timer(deleteTime,(Object)this,"deleteMember",port));
    			if(getLength()==mSize){ //the member buffer is full
	    			int i = ((int)(Math.random()*mSize))%mSize;
    				mCache[i].timer.reset();
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
	 
	 public synchronized void deleteMember(int port){
	    	for(int i=0;i<mCache.length;i++)
	    		if(mCache[i]!=null)
	    		if(mCache[i].port==port){
	    			try{
	    				mCache[i].timer.reset();
	    			}
	    			catch (Exception e) {
	    				e.printStackTrace();
					}
	    			mCache[i]=null;
	    			return;
	    		}
	    }
	 
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
	    
	 public int[] toArray(){
	    	int[] result = new int[mCache.length];
	    	for(int i=0;i<mCache.length;i++)
	    		if(mCache[i]==null)
	    			result[i]=0;
	    		else
	    			result[i] = mCache[i].port;
	    	return result;
	    }
	 public Member getMember(int index){
		 return mCache[index];
	 }
	 public void setMember(int index,Member m){
		 mCache[index]=m;
	 }
	
}