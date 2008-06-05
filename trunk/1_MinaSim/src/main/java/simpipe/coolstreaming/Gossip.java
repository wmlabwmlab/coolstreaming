package simpipe.coolstreaming;

import java.util.Arrays;
import java.util.Collections;

import org.apache.mina.common.IoSession;

/*
initialized when the partner's cache begins to be filled
periodically called using function called beginGossip in the PeerNode class
**/

public class Gossip { 
	
	PeerNode node;
	
	public Gossip(PeerNode node){
		this.node=node;
	}
	
	/*-this function is used to bridge gossiping message from one node to another
	 *-that means that me calling this function is not the initiator of the gossip message
	 */
	void bridge(int port,int hops){
		Partner[] temp=new Partner[node.partners.getLength()];
    	int j=0;
    	for(int i=0;i<node.pSize;i++)
    		if(node.partners.pCache[i]!=null){
    			temp[j++]=node.partners.pCache[i];
    		}
    	for(int i=0;i<temp.length;i++)
    	sendMessage(temp[i].port,port,hops-1);
	}
	
	/*-this function is used to initiate gossiping (I am the source of gossiping)
	 * -it is called periodically using the timer fired in the peerNode that alarms that the time has come for gossiping
	 */
	void initiate(int hops){
    	for(int i=0;i<node.pSize;i++)
    		if(node.partners.pCache[i]!=null)
    		sendMessage(node.partners.pCache[i].port,node.port,hops);
	}
	
	/*each of the 2 functions in the class (initiate)&(bridge) must use the function sendmessag() to repopulate the gossip message for other nodes in the network
	 * 
	 */
	void sendMessage(int destPort,int originalPort,int hops){
		if(destPort==originalPort)
			return;
		int index=node.partners.getIndex(destPort);
		if(index!=-1){ // if there are not any partner with that port number
			IoSession session=node.partners.pCache[index].session;
			session.write("g"+originalPort+"-"+hops);
		}
	}
}
