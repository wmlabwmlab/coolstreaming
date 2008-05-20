package simpipe.coolstreaming;

import java.util.Arrays;
import java.util.Collections;

import org.apache.mina.common.IoSession;


public class Gossip { //initialized when the partner's cache begins to be filled
	
	PeerNode node;
	
	public Gossip(PeerNode node){
		this.node=node;
	}
	
	void bridge(int port,int hops){
		int[] temp=new int[node.getLength(node.pCache)];
    	int j=0;
    	for(int i=0;i<node.pSize;i++)
    		if(node.pCache[i]!=0){
    			temp[j++]=node.pCache[i];
    		}
    	for(int i=0;i<temp.length;i++)
    	sendMessage(temp[i],port,hops-1);
	}
	
	void initiate(int hops){
    	for(int i=0;i<node.pSize;i++)
    		if(node.pCache[i]!=0)
    		sendMessage(node.pCache[i],node.port,hops);
	}
	void sendMessage(int destPort,int originalPort,int hops){
		if(destPort==originalPort)
			return;
		int index=node.getIndex(node.pCache,destPort);
		if(index!=-1){ // if there are not any partner with that port number
			IoSession session=node.pSession[index];
			session.write("g"+originalPort+"-"+hops);
		}
	}
}
