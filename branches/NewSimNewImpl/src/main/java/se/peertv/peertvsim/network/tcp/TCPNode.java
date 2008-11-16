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

package se.peertv.peertvsim.network.tcp;

import java.lang.reflect.Method;

import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.Message;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.network.tcp.bw.BandwidthManager;
import se.peertv.peertvsim.network.tcp.bw.Transfer;

public class TCPNode extends Node {

	protected final long upCapacity; // in bytes per sec
	protected final long downCapacity; // in in bytes per sec

	private BandwidthManager upBandwidthManager;
	private BandwidthManager downBandwidthManager;

	final static Scheduler scheduler = Scheduler.getInstance();

	public TCPNode(int id, long upCapacity, long downCapacity) {

		super(id);

		this.upCapacity = upCapacity;
		this.downCapacity = downCapacity;
		// this.upBandwidthManager = new BandwidthManager(upCapacity, id);
		// this.downBandwidthManager = new BandwidthManager(downCapacity, id);
	}

	private Transfer sendBytes(int remoteNodeId, Message message, long bytes) throws Exception {
		return this.upBandwidthManager.sendSize(remoteNodeId, message, bytes);
	}

	/**
	 * Send a a message. ..... unreliable, any packet loss congestion etc.. should be modeled here
	 * 
	 * @param nodeId
	 * @param Message
	 */
	public void send(int nodeId, Message message) throws Exception {

		/*
		 * We should use:
		 */
		// long size = message.getBytes();
		// sendBytes(nodeId, message, size);
		/*
		 * For the moment no TCP bandwidth modeling
		 */
		super.send(nodeId, message, 0);

		// P.rint("t("+scheduler.now+") SEND "+message.toString());
		// P.rint("t["+message.getSource()+"->"+message.getDest()+"]("+Scheduler.
		// getInstance().now+") SEND "+message.toString());
	}

	public void receive(Message msg) throws Throwable {
		// P.rint("t["+msg.getSource()+"->"+msg.getDest()+"]("+Scheduler.
		// getInstance().now+") RECV "+msg.toString());

		try {

			/*
			 * We should use:
			 */
			// // in case the message is a bandwidth'd message
			// if (msg.getBytes() > 0) {
			// this.downBandwidthManager.receive(msg.getTransfer());
			// }
			/*
			 * For the moment no TCP bandwidth modeling
			 */

			super.receive(msg);
		} catch (java.lang.reflect.InvocationTargetException e) {
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

	public String getGroup() {
		return group;
	}

}
