/**
 * =============================================== 
*  File     : $$Id: Node.java,v 1.9 2007/06/06 09:22:48 sameh Exp $$
*  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
*  Version  :$$Revision: 1.9 $$
*  Tag	  : $$Name:  $$
*  Last edited by   : $$Author: sameh $$
*  Last updated:    $$Date: 2007/06/06 09:22:48 $$
*===============================================
 */

package se.peertv.peertvsim.network;

import java.lang.reflect.Method;

import se.peertv.peertvsim.core.Callbackable;
import se.peertv.peertvsim.core.NodeIdInt;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.bandwidth.BandwidthManager;
import se.peertv.peertvsim.network.bandwidth.Transfer;
import se.peertv.peertvsim.utils.P;



public class Node extends Callbackable implements NodeIdInt{

	protected final int id;
	protected final long upCapacity; //in bytes per sec
	protected final long downCapacity; //in in bytes per sec
	private BandwidthManager upBandwidthManager;
	private BandwidthManager downBandwidthManager;

	final static  Scheduler scheduler   = Scheduler.getInstance(); 
	final static  DelayManager delayManager = DelayManager.getInstance();
	
	public Node(int id, long downCapacity, long upCapacity) {
		super();
		this.id = id;
		this.upCapacity = upCapacity;
		this.downCapacity = downCapacity;
		this.upBandwidthManager = new BandwidthManager(		
				upCapacity,				
				id);
		this.downBandwidthManager = new BandwidthManager(
				downCapacity,
				id);
	}

	public Transfer sendBytes(int remoteNodeId, Message  message, long bytes) throws Exception{		
		return this.upBandwidthManager.sendBytes(remoteNodeId, message, bytes);
	}

	/**
	 * Send a a message.   
	 * ..... unreliable, any packet loss congeston etc.. should be modeled here  
	 * @param nodeId
	 * @param Message
	 */
	public void send(int nodeId, Message  message) throws Exception{
		long  time = scheduler.now+ delayManager.getDelay(id, nodeId);
		message.setDest(nodeId);
		message.setSource(id);		
		scheduler.enqueue( new MessageDeliveryEvent(time,  message)); 
		//P.rint("t("+scheduler.now+") SEND "+message.toString());
		//P.rint("t["+message.getSource()+"->"+message.getDest()+"]("+Scheduler.getInstance().now+") SEND "+message.toString());
	}
	
	public void  receive(Message msg) throws Throwable{ 
		//P.rint("t["+msg.getSource()+"->"+msg.getDest()+"]("+Scheduler.getInstance().now+") RECV "+msg.toString());
		 try {
			 	// in case the message is a bandwidth'd message
			 	if(msg.getBytes() > 0)
			 	{
			 		this.downBandwidthManager.receive(msg.getTransfer());			 		
			 	}	
				Class[] argTypes = { msg.getClass() };
				Object[] args = {msg};
				String handlerName = "handle";//+msg.getClass().getSimpleName();
				Method handler =  this.getClass().getMethod(handlerName,argTypes);
				handler.invoke(this, args);				 
			} catch (java.lang.reflect.InvocationTargetException e){
				throw e.getCause();
			}
	   }
	
	// ================= Getter & setters =============================
	public int getId() {
		return id;
	}

	public long getDownCapacity() {
		return downCapacity;
	}
	
	public long getUpCapacity() {
		return upCapacity;
	}

	public BandwidthManager getUpBandwidthManager() {
		return upBandwidthManager;
	}

	public BandwidthManager getDownBandwidthManager() {
		return downBandwidthManager;
	}
}
	//==== Brainstorming fro the piece semantics
	//=== Assumption the node initating the connection is the downloader

	//See how much slack is available at my side
	//Case1: Have no previous connections
	//			 : return downCapacity
	//Case2: Have exisitng connections
	//       2.a:  Have enough bandwidth for one stream 
	//       2.b:  Not enough bandwidth
	//                 2.b.1: shrinking others to their (fair/strict) share  is an option
	//						   2.b.1.a: Shrinking others will pay off
	//						   2.b.1.a: Shrinking won't pay off
	//				 2.b.2: shrinking others is not an option

	//See how much 

	//See how much slack is available at my side
	//Case1: Have nothing
	//Case2: Have exisitng connections
	//       2.a:  Have enough bandwidth
	//       2.b:  Not enough bandwidth
	//                 2.b.1: shrinking others to their (fair/strict) share  is an option
	//						   2.b.1.a: Shrinking others will pay off
	//						   2.b.1.a: Shrinking won't pay off
	//				 2.b.2: shrinking others is not an option

	//See how much 

	
	
