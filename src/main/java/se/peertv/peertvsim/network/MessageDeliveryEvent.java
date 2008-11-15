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


import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.utils.A;

public class MessageDeliveryEvent extends Event {
	/**
	 * @param time
	 * @param args
	 */
	public MessageDeliveryEvent(long time, Object args) {
		super(time, args);
	}

	public void handle() throws Throwable{
		 Message message = (Message) args;
		 //P.rint("t["+message.getSource()+"->"+message.getDest()+"]("+Scheduler.getInstance().now+") RECV "+args.toString());
		 Node  dest =   Network.getInstance().get(message.dest);
		 A.ssert(Network.getInstance().contains(message.dest), "No such node "+ message.dest);
		 dest.receive(message);
	}

	public void setTime(long time) throws Exception {		
		this.time = time;		
	}

	@Override
	public String toString() {
		return super.toString() + " MsgDeliveryEvt" + ((Message) args).toString();
	}
}
