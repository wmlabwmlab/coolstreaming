package simpipe.coolstreaming;

import se.peertv.peertvsim.core.Scheduler;

/*
 * -This class is designed for scheduling which segment will be fetched from which peer
 * -Also it hold the buffer map for the node initiating this object 
 */
public class Schedule {
	Node node;
	static int startTime;
	int wholeBits[];
	int deadLine[];
	int supplier[];
	int slack=2;
	
	public Schedule(int startTime){
		this.startTime=startTime;
	}
	
	public Schedule(Node node){
		this.node=node;
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
	BitField beginscheduling(int timeSlot){
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
	
}
