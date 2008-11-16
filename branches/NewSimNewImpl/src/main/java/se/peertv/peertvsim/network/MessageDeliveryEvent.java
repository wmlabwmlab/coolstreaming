/**
 * =============================================== 
 *  File     : $Id: MessageDeliveryEvent.java,v 1.5 2007/06/03 00:51:59 sameh Exp $
 *  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
 *  Version  :$Revision: 1.5 $
 *  Tag	  : $Name:  $
 *  Last edited by   : $Author: sameh $
 *  Last updated:    $Date: 2007/06/03 00:51:59 $
 *===============================================
 */
package se.peertv.peertvsim.network;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.utils.P;
import simpipe.protocol.SimpPipeMessage;

public class MessageDeliveryEvent extends Event implements Serializable{

	private static final long serialVersionUID = 3892645896873996883L;

	private static final Logger log = LoggerFactory.getLogger(MessageDeliveryEvent.class);
	private Message message;

	public MessageDeliveryEvent(long time, Message message) {
		this.time = time;
		this.message = message;
	}

	@Override
	public void handle() throws Throwable {
 		Reflection.setExecutingNode(message.dest);

		Node dest = Network.getInstance().get(message.dest);
		if (dest == null) {
			/*
			 * Ignore if destination is not present
			 */
			// System.err.println("Destination null");
			return;
		}

		Reflection.setExecutingGroup(dest.getGroup());

		if (SimulableSystem.isSimPipeTraceEnabled())
			if (message instanceof SimpPipeMessage) {
				String At = ((SimpPipeMessage) message).getMinaEvent().getTargetSessionId();
				P.rint("t[" + Scheduler.getInstance().getNow() + "][@" + At + "][" + dest.getGroup() + "][" + message.getSource() + "->" + message.getDest() + "]" + message.toString());
			} else {
				P.rint("t[" + Scheduler.getInstance().getNow() + "][" + dest.getGroup() + "][" + message.getSource() + "->" + message.getDest() + "]" + message.toString());
			}

		// A.ssert(Network.getInstance().contains(message.dest),
		// "No such node "+ message.dest);

		// if (Network.getInstance().contains(message.dest)) {
		// }

		dest.receive(message);
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	public synchronized Message getMessage() {
		return message;
	}

	public void setMessage(Message msg) {
		this.message = msg;
	}

	@Override
	public String toString() {
		return super.toString() + " MsgDeliveryEvt" + message.toString();
	}

	@Override
	public String getThreadGroup() {
//		Node dest = Network.getInstance().get(((Message) args).dest);
//		try {
//			A.ssert(dest!=null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return dest.getGroup();
		return "msgDelivery";
	}

	public int getDestNodeId() {
		return message.getDest();
	}

	public int getSourceNodeId() {
		return message.getSource();
	}
}
