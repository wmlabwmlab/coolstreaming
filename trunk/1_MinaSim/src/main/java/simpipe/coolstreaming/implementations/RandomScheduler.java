package simpipe.coolstreaming.implementations;

import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.core.Timer;
import simpipe.coolstreaming.BitField;
import simpipe.coolstreaming.Constants;
import simpipe.coolstreaming.PeerNode;

/*
 * -This class is designed for scheduling which segment will be fetched from which peer
 * -Also it hold the buffer map for the node initiating this object 
 */
public class RandomScheduler implements simpipe.coolstreaming.interfaces.Scheduler{
	
	
	private PeerNode node;
	private int startTime;
	private int wholeBits[];
	private int deadLine[];
	private int supplier[];
	private int slack=2;
	private int exchangeTime=30000;//request map every 5 sec
	private int timeSlot;
	private boolean requesting=false;
    
	public RandomScheduler(){
		
	}
	public RandomScheduler(PeerNode node, int startTime){
		this.node=node;
		this.startTime = startTime;
		fillDeadLine();
		wholeBits=new int[node.getVideoSize()];
		supplier=new int[node.getWindowSize()];
		if(node.isSource())
		{
			for(int i=0;i<node.getVideoSize();i++){
				wholeBits[i]=1;
			}
		}
	}
	
	@Override
	public void setParams(PeerNode node, int startTime) {
		this.node=node;
		this.startTime = startTime;
		fillDeadLine();
		wholeBits=new int[node.getVideoSize()];
		supplier=new int[node.getWindowSize()];
		if(node.isSource())
		{
			for(int i=0;i<node.getVideoSize();i++){
				wholeBits[i]=1;
			}
		}
		
	}
	
	@Override
	public void fillDeadLine(){
		deadLine=new int[node.getVideoSize()];
		int start=startTime/1000;	//in seconds
		for(int i=0;i<deadLine.length;i++){
			deadLine[i]=start+i+slack;
		}
	}
	
	@Override
	public boolean isValid(int index,int timeNow){
		if(deadLine[index]<timeNow)
			return true;
		return false;
	}
	
	@Override
	public BitField getWindow(int now){
			BitField bits = new BitField(node.getWindowSize());
			int diff=now-startTime;
			diff=diff/1000; //conv to sec
			int j=0;
			for(int i=diff;i<node.getWindowSize()+diff;i++){
				if(j==node.getWindowSize())
					break;
				if(i<node.getVideoSize())
					bits.setBit(j,wholeBits[i]);
				else
					bits.setBit(j,0);
				
					j++;
				
			}
			
			return bits;
	}
	
	 // the BitMap's timer's firing function 
	@Override
    public void exchangeBM(int dull){ 
    	
    	int milliesNow=(int)Scheduler.getInstance().now;	
    	int secondNow=milliesNow/1000;
    	timeSlot = milliesNow;
    	for(int i=0;i<node.getVideoSize();i++)
	    	if(wholeBits[i]==0&&deadLine[i]<secondNow){
	    		timeSlot=startTime+(i*1000);
	    		break;
	    	}
    	requesting=true;
    	for(int i=0;i<node.getPSize();i++)
    		if(node.getPartners().getPartner(i)!=null){
    		node.getPartners().getPartner(i).session.write(""+Constants.BUFFERMAP_REQUEST+timeSlot);
   		}
    	
    	try {
			new Timer(exchangeTime,this,"exchangeBM",0);
		} catch (Exception e) {
		e.printStackTrace();
		}
    }
    
	@Override
	public BitField beginscheduling(){
		BitField field = new BitField(node.getWindowSize());
		int index=0;
		int length=0;
		int timeNow=(int)Scheduler.getInstance().now;
		for(int i=0;i<node.getWindowSize();i++){
			for(int j=0;j<node.getPSize();j++){
				if(node.getPartners().getPartner(j)==null)
					continue;
				if(timeSlot!=node.getPartners().getPartner(j).bufferMap.time)
					continue;
				if(node.getPartners().getPartner(j).bufferMap.bits[i]==1&&isValid(i,timeNow)){
					length++;
				}
			}
			if(length==0)
				continue;
			
			index=0;
			Partner supp[]=new Partner[length];
			for(int k=0;k<node.getPSize();k++){
				if(node.getPartners().getPartner(k)==null)
					continue;
				if(timeSlot!=node.getPartners().getPartner(k).bufferMap.time)
					continue;
				if(node.getPartners().getPartner(k).bufferMap.bits[i]==1&&isValid(i,timeNow)){
					supp[index++]=node.getPartners().getPartner(k);
				}
			}
			
			/* before adding bandwidth
			int rand=(int)Math.round((Math.random()*length));
			rand=rand%length;
			field.setBit(i,supp[rand]);
			*/
			
			int bandwidth[]=new int[length];
			for(int counter=0;counter<length;counter++){
				int pos=node.getPartners().getIndex(supp[counter].port);
				if(pos!=-1)
				bandwidth[counter]=node.getPartners().getPartner(pos).bandwidth;
				else
				bandwidth[counter]=node.getDefaultBandwidth();
			}
			int pos=pickPeer(bandwidth);
			field.setBit(i,supp[pos].port);
			
			length=0;
			
		}
		return field;
	}
	
	@Override
	public synchronized int pickPeer(int[] bandwidth){
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
	
	@Override
	public String toString(){
		String str="";
		for(int i=0;i<wholeBits.length;i++){
			str=str+wholeBits[i];
		}
		return str;
	}
	
	@Override
	public void identifyRequiredSegments() {
		if(requesting){
			requesting=false;
			int diff=(timeSlot - startTime)/1000;
			
			// is the movie finished
			if(diff >= node.getVideoSize())
				return;
			
			BitField field = beginscheduling();
			
			int loc = 0;
			for(int i=0;i < node.getWindowSize();i++){
				// is the movie finished
				if(diff+i >= node.getVideoSize())
					return;
				
				if(wholeBits[diff+i]==1 || field.bits[i]==0)
					continue;

				loc = node.getPartners().getIndex(field.bits[i]);
				if(loc==-1)
					continue;
				int sum=diff+i;
				node.getPartners().getPartner(loc).session.write(""+Constants.SEGMENT_REQUEST+sum);
			}
		}
		
	}
	
	@Override
	public int getWholeBits(int index){
		return wholeBits[index];
	}
	
	@Override
	public void setWholeBits(int index, int value){
		wholeBits[index]=value;
	}
	
	@Override
	public int getDeadLine(int index){
		return deadLine[index];
	}
	
	@Override
	public int getExchangeTime(){
		return exchangeTime;
	}
	
	@Override
	public void setStartTime(int start){
		startTime=start;
	}
	public void subscribe(char header){
		
	}
	public void fire(char header){
		
	}
	
}
