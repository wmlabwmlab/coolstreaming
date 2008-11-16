/**
 * =============================================== 
 *  File     : $Id: Event.java,v 1.6 2007/06/02 13:35:41 sameh Exp $
 *  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
 *  Version  :$Revision: 1.6 $
 *  Tag	  : $Name:  $
 *  Last edited by   : $Author: sameh $
 *  Last updated:    $Date: 2007/06/02 13:35:41 $
 *===============================================
 */
package se.peertv.peertvsim.core;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import se.peertv.peertvsim.network.MessageDeliveryEvent;

public abstract class Event implements Comparable<Event>, Serializable {

	private static final long serialVersionUID = -6448869871328228621L;

	public long time;
	public Object args;

	private static AtomicInteger id = new AtomicInteger(0);

	private final int myID;

	public Event() {
		this.myID = id.incrementAndGet();
	}

	public Event(long time, Object args) {
		this.time = time;
		this.args = args;
		this.myID = id.incrementAndGet();
	}

	public abstract void handle() throws Throwable;

	public long getTime() {
		return time;
	}

	public int compareTo(Event event) {
		Event e = (Event) event;
		if (this == event)
			return 0;

		if (this instanceof MessageDeliveryEvent && e instanceof TimerEvent && this.time == e.time)
			return -1; // always process message deliveries before timers
		if (e instanceof MessageDeliveryEvent && this instanceof TimerEvent && this.time == e.time)
			return 1; // always process message deliveries before timers

		long diff = time - e.getTime();
		// if (diff == 0)
		// return 0;
		if (diff > 0)
			return 1;
		return -1;
		// return (int)(time - ((Event)event).time);
	}

	@Override
	public String toString() {
		return "T:" + time;
	}

	public abstract String getThreadGroup();

	public int getMyID() {
		return myID;
	}

}
