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
package se.peertv.peertvsim.core.concurrent;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.core.SchedulerInt;
import se.peertv.peertvsim.utils.A;
import se.peertv.peertvsim.utils.P;

public class ConcurrentScheduler implements SchedulerInt {
	public long now;
	private final ConcurrentSimulationQueue<Event> queue;
	private static ConcurrentScheduler singeltonScheduler = null;

	public ConcurrentScheduler() {
		queue = new ConcurrentSimulationQueue<Event>();
		reset();
	}

	public synchronized void enqueue(Event e) throws Exception {
		A.ssert(e!=null, "event==null");
		A.ssert(e.getTime() >= now, "Time cant go backwards");
		queue.put(e);
	}

	public synchronized CandidatesMap dequeue() throws Exception {
		CandidatesMap entry = queue.removeTimeEntry();
		if (entry == null)
			return null;
		try {
			A.ssert(entry.getTime() > now, "Time cant go backwards");
		} catch (Exception e) {
			System.out.println("error");
		}
		// if(now != e.getTime() && now % 10000 == 0)
		// //System.out.println("now " + e.getTime());
		// System.out.print(".");
		now = entry.getTime();
		return entry;
	}

	/*
	 * public boolean remove(Object o) { P.rint("Scheduler::remove " +
	 * o.getClass().toString() + " " + o.toString()); return queue.remove(o); }
	 */

	/*
	 * public Event peek() { P.rint("Scheduler::peek "); return queue.peek(); }
	 */

	public void reset() {
		queue.clear();
		now = 0;
	}

	@Override
	public boolean equals(Object obj) {
		return singeltonScheduler.equals(obj);
	}

	@Override
	public int hashCode() {
		return singeltonScheduler.hashCode();
	}

	@Override
	public String toString() {
		return singeltonScheduler.toString();
	}

	public String queueToString() {
		return queue.toString();
	}

	// used only for testing purposes, when an outer scheduler is used and it
	// depends on the objects to use the time
	// provided the the only scheduler they see: this scheduler
	public void setTime(long currentTime) {
		now = currentTime;
	}

	public synchronized long getNow() {
		return now;
	}

	@Override
	public boolean remove(Event e) {
		return queue.remove(e);
	}

	@Override
	public void setIngnoreEnqueuing(boolean isIgnoreEnueueing) {
		try {
			A.ssert(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
