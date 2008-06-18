package simpipe.coolstreaming;

import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.Timer;

/*
 * -This class is designed for scheduling which segment will be fetched from which peer
 * -Also it hold the buffer map for the node initiating this object 
 */
public class Schedule {
	PeerNode node;
	int startTime;
	int wholeBits[];
	int deadLine[];
	int supplier[];
	int slack=2;
    int exchangeTime=30000;//request map every 5 sec
    int timeSlot;
    boolean requesting=false;
    
	public Schedule(PeerNode node, int startTime){
		this.node=node;
		this.startTime = startTime;
		fillDeadLine();
		wholeBits=new int[node.videoSize];
		supplier=new int[node.windowSize];
		if(node.isSource)
		{
			for(int i=0;i<node.videoSize;i++){
				wholeBits[i]=1;
			}
		}
	}
	public void fillDeadLine(){
		deadLine=new int[node.videoSize];
		int start=startTime/1000;	//in seconds
		for(int i=0;i<deadLine.length;i++){
			deadLine[i]=start+i+slack;
		}
	}
	boolean isValid(int index,int timeNow){
		if(deadLine[index]<timeNow)
			return true;
		return false;
	}
	BitField getWindow(int now){
			BitField bits = new BitField(node.windowSize);
			int diff=now-startTime;
			diff=diff/1000; //conv to sec
			int j=0;
			for(int i=diff;i<node.windowSize+diff;i++){
				if(j==node.windowSize)
					break;
				if(i<node.videoSize)
					bits.setBit(j,wholeBits[i]);
				else
					bits.setBit(j,0);
				
					j++;
				
			}
			
			return bits;
	}
	
	 // the BitMap's timer's firing function 
    public void exchangeBM(int dull){ 
    	
    	int milliesNow=(int)Scheduler.getInstance().now;	
    	int secondNow=milliesNow/1000;
    	timeSlot = milliesNow;
    	for(int i=0;i<node.videoSize;i++)
	    	if(wholeBits[i]==0&&deadLine[i]<secondNow){
	    		timeSlot=startTime+(i*1000);
	    		break;
	    	}
    	requesting=true;
    	for(int i=0;i<node.pSize;i++)
    		if(node.partners.pCache[i]!=null){
    		node.partners.pCache[i].session.write(""+Constants.BUFFERMAP_REQUEST+timeSlot);
   		}
    	
    	try {
			new Timer(exchangeTime,this,"exchangeBM",0);
		} catch (Exception e) {
		e.printStackTrace();
		}
    }
    
	BitField beginscheduling(){
		BitField field = new BitField(node.windowSize);
		int index=0;
		int length=0;
		int timeNow=(int)Scheduler.getInstance().now;
		for(int i=0;i<node.windowSize;i++){
			for(int j=0;j<node.pSize;j++){
				if(node.partners.pCache[j]==null)
					continue;
				if(timeSlot!=node.partners.pCache[j].bufferMap.time)
					continue;
				if(node.partners.pCache[j].bufferMap.bits[i]==1&&isValid(i,timeNow)){
					length++;
				}
			}
			if(length==0)
				continue;
			
			index=0;
			Partner supp[]=new Partner[length];
			for(int k=0;k<node.pSize;k++){
				if(node.partners.pCache[k]==null)
					continue;
				if(timeSlot!=node.partners.pCache[k].bufferMap.time)
					continue;
				if(node.partners.pCache[k].bufferMap.bits[i]==1&&isValid(i,timeNow)){
					supp[index++]=node.partners.pCache[k];
				}
			}
			
			/* before adding bandwidth
			int rand=(int)Math.round((Math.random()*length));
			rand=rand%length;
			field.setBit(i,supp[rand]);
			*/
			
			int bandwidth[]=new int[length];
			for(int counter=0;counter<length;counter++){
				int pos=node.partners.getIndex(supp[counter].port);
				if(pos!=-1)
				bandwidth[counter]=node.partners.pCache[pos].bandwidth;
				else
				bandwidth[counter]=node.defaultBandwidth;
			}
			int pos=pickPeer(bandwidth);
			field.setBit(i,supp[pos].port);
			
			length=0;
			
		}
		return field;
	}
	
	static int pickPeer(int[] bandwidth){
		int sum=0;
		double cummulative=0;
		double ratio[]=new double[bandwidth.length];
		for(int i=0;i<bandwidth.length;i++)
			sum+=bandwidth[i];
		for(int i=0;i<bandwidth.length;i++){
			ratio[i]=cummulative+(((double)bandwidth[i])/sum);
			cummulative=ratio[i];
		}
		double rand=Math.random();
		
		for(int i=0;i<ratio.length;i++){
			if(rand>ratio[i])
				continue;
			else
				return i;
		}
		
		return 0;
	}
	
	public String toString(){
		String str="";
		for(int i=0;i<wholeBits.length;i++){
			str=str+wholeBits[i];
		}
		return str;
	}
	public void identifyRequiredSegments() {
		if(requesting){
			requesting=false;
			int diff=(timeSlot - startTime)/1000;
			
			// is the movie finished
			if(diff >= node.videoSize)
				return;
			
			BitField field = beginscheduling();
			
			int loc = 0;
			for(int i=0;i < node.windowSize;i++){
				// is the movie finished
				if(diff+i >= node.videoSize)
					return;
				
				if(wholeBits[diff+i]==1 || field.bits[i]==0)
					continue;

				loc = node.partners.getIndex(field.bits[i]);
				if(loc==-1)
					continue;
				int sum=diff+i;
				node.partners.pCache[loc].session.write(""+Constants.SEGMENT_REQUEST+sum);
			}
		}
		
	}
	
}
