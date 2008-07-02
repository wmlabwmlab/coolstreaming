/**
 * =============================================== 
*  File     : $Id: TimerEvent.java,v 1.3 2007/06/02 13:06:15 sameh Exp $
*  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
*  Version  :$Revision: 1.3 $
*  Tag	  : $Name:  $
*  Last edited by   : $Author: sameh $
*  Last updated:    $Date: 2007/06/02 13:06:15 $
*===============================================
 */
package se.peertv.peertvsim.core;

import se.peertv.peertvsim.utils.P;


/**
 * @author sameh
 *
 */
public class TimerEvent extends Event {
	public TimerEvent(long time, Object args) {
		super(time, args);
	}

	public void handle() throws Throwable{
		 P.rint("t("+time+") "+args.toString());
		((Timer)args).fire();
	}
	
	@Override
	public String toString() {	
		return super.toString() + " Timer " + ((Timer)args).toString();
	}
}
