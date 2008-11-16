package simpipe.coolstreaming;

import org.apache.mina.common.IoSession;

import se.peertv.peertvsim.core.Timer;

/*
initialized when the partner's cache begins to be filled
periodically called using function called beginGossip in the PeerNode class
*/

public class Gossip { 
	
	private PeerNode node;
    //Gossip parameters
    public int gossipTime=6000;
    private final int hops=3;
    
	public Gossip(PeerNode node){
		this.node=node;
		initiate();
	}
	
	/*-this function is used to bridge gossiping message from one node to another
	 *-that means that me calling this function is not the initiator of the gossip message
	 */
	void bridge(int port,int hops){
		for(int i=0;i<node.pSize;i++)
    		if(node.partners.getPartner(i)!=null){
    		sendMessage(node.partners.getPartner(i).getPort(),port,hops-1);
    		}
    }
	
	/*-this function is used to initiate gossiping (I am the source of gossiping)
	 * -it is called periodically using the timer fired in the peerNode that alarms that the time has come for gossiping
	 */
	public void initiate(){
    	for(int i=0;i<node.pSize;i++)
    		if(node.partners.getPartner(i)!=null)
    		sendMessage(node.partners.getPartner(i).getPort(),node.port,hops);
    	/*
    	 * this section is modified to import new sim
    	*/
//    	try {
//			new Timer(gossipTime,this,"initiate",0);
//		} catch (Exception e) {
//		e.printStackTrace();
//		}
	}
	
	/*each of the 2 functions in the class (initiate)&(bridge) must use the function sendmessag() to repopulate the gossip message for other nodes in the network
	 * 
	 */
	void sendMessage(int destPort,int originalPort,int hops){
		if(destPort==originalPort)
			return;
		int index=node.partners.getIndex(destPort);
		if(index != -1){ // if there are not any partner with that port number
			IoSession session=node.partners.getPartner(index).getSession();
			session.write(""+Constants.GOSSIPING+originalPort+"-"+hops);
		}
	}
}
