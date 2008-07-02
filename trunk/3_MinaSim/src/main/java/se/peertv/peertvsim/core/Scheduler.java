/**
 * =============================================== 
*  File     : $Id: Scheduler.java,v 1.7 2007/06/06 09:22:48 sameh Exp $
*  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
*  Version  :$Revision: 1.7 $
*  Tag	  : $Name:  $
*  Last edited by   : $Author: sameh $
*  Last updated:    $Date: 2007/06/06 09:22:48 $
*===============================================
 */
package se.peertv.peertvsim.core;

import java.util.PriorityQueue;

import se.peertv.peertvsim.utils.A;
import se.peertv.peertvsim.utils.P;

public class Scheduler {

	public  long now;
	private PriorityQueue<Event> queue;
	private static Scheduler singeltonScheduler = null;

	
	public static Scheduler getInstance() {
		if (singeltonScheduler == null)
			singeltonScheduler = new Scheduler();
		return singeltonScheduler;
	}

	private Scheduler() {
		queue = new PriorityQueue<Event>(1000);
		clear();
	}

	public void enqueue(Event e) throws Exception{
		A.ssert (e.getTime() > now, "Time cant go backwards");
		boolean enqueued = queue.offer(e);
		A.ssert(enqueued);
	}
	
	public  Event dequeue()  throws Exception{
		Event e= queue.poll();
		if(e==null) 
			return null;
		A.ssert(e.getTime() >= now, "Time cant go backwards" );
//		if(now != e.getTime() && now % 10000 == 0) 
//			//System.out.println("now " + e.getTime());
//			System.out.print(".");
		now= e.getTime();		
		return e;
	}

	public boolean remove(Object o) {
		P.rint("Scheduler::remove " + o.getClass().toString() + " " + o.toString());
		return queue.remove(o);
	}

	public void clear() {
		queue.clear();
		now = 0;
	}

	public boolean equals(Object obj) {
		return singeltonScheduler.equals(obj);
	}

	public int hashCode() {
		return singeltonScheduler.hashCode();
	}

	public String toString() {
		return singeltonScheduler.toString();
	}

	// used only for testing purposes, when an outer scheduler is used and it depends on the objects to use the time
	// provided the the only scheduler they see: this scheduler
	public void setTime(long currentTime) {
		this.now = currentTime;		
	}

}
