/**
 * =============================================== 
 *  File     : $$Id: Message.java,v 1.6 2007/06/06 09:22:48 sameh Exp $$
 *  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
 *  Version  :$$Revision: 1.6 $$
 *  Tag	  : $$Name:  $$
 *  Last edited by   : $$Author: sameh $$
 *  Last updated:    $$Date: 2007/06/06 09:22:48 $$
 *===============================================
 */
package se.peertv.peertvsim.network;

import java.util.concurrent.atomic.AtomicInteger;

import se.peertv.peertvsim.network.tcp.bw.Transfer;

public class Message {

	protected int source = -1;
	protected int dest = -1;

	static AtomicInteger id = new AtomicInteger(0);

	public int seqNum = id.incrementAndGet();

	public String trace = "";
	// this is used to identify the bandwidth'd messages from others who aren't
	private long bytes = 0;
	private Transfer transfer;

	// public Message(int source, int dest) {
	// super();
	// this.source = source;
	// this.dest = dest;
	// }
	// The source and destination of message are set in the receive
	// method of a node
	public Message() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Message))
			return false;
		return seqNum == ((Message) obj).seqNum;
	}

	public String getLabel() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return getLabel() + ":" + source + "-->" + dest + " <" + seqNum + ">";
	}

	public int getDest() {
		return dest;
	}

	public int getSource() {
		return source;
	}

	public void setDest(int dest) {
		this.dest = dest;
	}

	public void setSource(int source) {
		this.source = source;
	}

	// public void addTotrace(){
	//			
	// }
	public long getSize() {
		return bytes;
	}

	public Transfer getTransfer() {
		return transfer;
	}

	public void setSize(long bytes) {
		this.bytes = bytes;
	}

	public void setTransfer(Transfer transfer) {
		this.transfer = transfer;
	}

	public static void reset() {
		id = new AtomicInteger();
	}
}
