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

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.core.concurrent.ConcurrentEventLoop;
import se.peertv.peertvsim.core.recorder.Recorder;
import se.peertv.peertvsim.core.sequential.SequentialScheduler;
import se.peertv.peertvsim.utils.A;

public class Scheduler {

	public final static int SEQUENTIAL_SCHEDULING = 0;
	public final static int CONCURRENT_SCHEDULING = 1;
	public final static int REPLAY_SCHEDULING = 2;

	public static int schedulerType = SEQUENTIAL_SCHEDULING;

	private static Scheduler schedulerInstance = null;

	private static SchedulerInt singeltonScheduler = null;

	public static Scheduler getInstance() {

		if (schedulerInstance == null)
			schedulerInstance = new Scheduler();

		if (singeltonScheduler == null) {
			
			switch (schedulerType) {
			case SEQUENTIAL_SCHEDULING:
				singeltonScheduler = new SequentialScheduler();
				break;

			case REPLAY_SCHEDULING:
				singeltonScheduler = new SequentialScheduler();
				break;

			// case CONCURRENT_SCHEDULING:
			// singeltonScheduler = ConcurrentEventLoop.getScheduler();
			// break;
			default:
				break;
			}
		}

		return schedulerInstance;
	}

	public void enqueue(Event e) throws Exception {

		/*
		 * Don't queue events if in replay mode
		 */
		if (schedulerType != REPLAY_SCHEDULING) {

			if (SimulableSystem.isMinaSimTraceEnabled()) {
				System.out.println("Enqueueing event: " + e + " class: " + e.getClass().getSimpleName());
			}

			singeltonScheduler.enqueue(e);
		}
	}

	public void reset() {
		singeltonScheduler.reset();
		Timers.getInstance().reset();
		Timer.resetCounter();
	}

	@Override
	public boolean equals(Object obj) {
		return singeltonScheduler.equals(obj);
	}

	public boolean remove(Event o) {
		return singeltonScheduler.remove(o);
	}

	@Override
	public int hashCode() {
		return singeltonScheduler.hashCode();
	}

	@Override
	public String toString() {
		return singeltonScheduler.toString();
	}

	// used only for testing purposes, when an outer scheduler is used and it
	// depends on the objects to use the time
	// provided the the only scheduler they see: this scheduler
	public void setTime(long currentTime) {
		singeltonScheduler.setTime(currentTime);
	}

	public long getNow() {
		return singeltonScheduler.getNow();
	}

	public void setFilm(Recorder film) {
		try {
			A.ssert(schedulerType == Scheduler.REPLAY_SCHEDULING);
		} catch (Exception e) {
			e.printStackTrace();
		}

		SequentialScheduler scheduler = (SequentialScheduler) singeltonScheduler;
		scheduler.setFilm(film);
	}

	public static SchedulerInt getInternalSchedulerInstance() {
		getInstance();
		return singeltonScheduler;
	}

	public static void setSchedulerType(int schedulerType) {
		Scheduler.schedulerType = schedulerType;
	}

}
