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
package se.peertv.peertvsim.core.sequential;

import java.util.Queue;

import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.core.SchedulerInt;
import se.peertv.peertvsim.core.recorder.Recorder;
import se.peertv.peertvsim.utils.A;
import se.peertv.peertvsim.utils.P;

public class SequentialScheduler implements SchedulerInt {

	private static SchedulerInt singeltonScheduler = null;
	private Queue<Event> queue;

	public long now;
	private boolean isIgnoreEnqueuing = false;

	public SequentialScheduler() {
		now = 0;
		queue = new SimulationQueue();
	}

	public void enqueue(Event e) throws Exception {
		A.ssert(e.getTime() >= now, "Time cant go backwards");

		if (!isIgnoreEnqueuing) {
			boolean enqueued = queue.offer(e);
			A.ssert(enqueued);
		}
	}

	public Event dequeue() throws Exception {
		Event e = queue.poll();
		if (e == null)
			return null;
		A.ssert(e.getTime() >= now, "Time cant go backwards");
		// if(now != e.getTime() && now % 10000 == 0)
		// //System.out.println("now " + e.getTime());
		// System.out.print(".");
		now = e.getTime();
		return e;
	}

	public boolean remove(Event o) {
		P.rint("Scheduler::remove " + o.getClass().toString() + " "
				+ o.toString());
		return queue.remove(o);
	}

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

	/**
	 * used only for testing purposes, when an outer scheduler is used and it
	 * depends on the objects to use the time provided the the only scheduler
	 * they see: this scheduler
	 */
	public void setTime(long currentTime) {
		now = currentTime;
	}

	public long getNow() {
		return now;
	}

	@Override
	public void setIngnoreEnqueuing(boolean isIgnoreEnueueing) {
		this.isIgnoreEnqueuing = isIgnoreEnueueing;
	}

	public void setFilm(Recorder film) {

		try {
			A.ssert(now == 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		queue = film.getFilm();
	}

}
