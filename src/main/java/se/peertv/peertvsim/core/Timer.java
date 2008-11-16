/**
 * =============================================== 
 *  File     : $Id: Timer.java,v 1.9 2007/06/06 09:22:49 sameh Exp $
 *  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
 *  Version  :$Revision: 1.9 $
 *  Tag	  : $Name:  $
 *  Last edited by   : $Author: sameh $
 *  Last updated:    $Date: 2007/06/06 09:22:49 $
 *===============================================
 */
package se.peertv.peertvsim.core;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import se.peertv.peertvsim.utils.A;

public class Timer {

	private static final long serialVersionUID = -696712066767810615L;

	private static long counter;

	private long firingTime;
	private NodeIdInt targetObject;
	private String callBackFunction;
	private TimerEvent eventRef = null;

	private long id;

	public Timer() {
	}

	public void set(long delay, NodeIdInt targetObject, String callBackFunction) throws Exception {
		reset();
		firingTime = Scheduler.getInstance().getNow() + delay;
		this.targetObject = targetObject;
		this.callBackFunction = callBackFunction;
		this.id = counter++;
		eventRef = new TimerEvent();
		eventRef.time = firingTime;
		eventRef.args = this.targetObject.getId();
		eventRef.timerId = id;
		eventRef.nodeId = this.targetObject.getId();

		// P.rint("t("+Scheduler.getInstance().now+") TIMR_SET"+this);
		// this.targetObject.addTimer(this);
		Timers.getInstance().addTimer(this.targetObject.getId(), id, this);
		Scheduler.getInstance().enqueue(eventRef);
	}

	public void reset() throws Exception {
		if (eventRef != null) {
			boolean removed = Scheduler.getInstance().remove(eventRef);
			A.ssert(removed, "not removed");
			eventRef = null;
			targetObject = null;
			callBackFunction = null;
		}
		if (targetObject != null)
			// targetObject.removeTimer(this);
			Timers.getInstance().removeTimer(targetObject.getId(), id, this);
	}

	public void fire() throws Throwable {
		eventRef = null;
		Timers.getInstance().removeTimer(targetObject.getId(), id, this);
		try {
			Method handler = targetObject.getClass().getMethod(callBackFunction, (Class[]) null);
			handler.invoke(targetObject, (Object[]) null);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw e.getCause();
		}
	}

	@Override
	public String toString() {
		String id = "";

		if (targetObject != null) {
			String who = targetObject.getClass().getSimpleName();
			id = "peer:" + targetObject.getId();
		}
		return id + " fireT:" + firingTime + "  meth:" + callBackFunction + "  targetObj:" + targetObject;
	}

	public String getCallBackFunction() {
		return callBackFunction;
	}

	public int getTargetObjectId() {
		return targetObject.getId();
	}

	public String getTargetGroup() {
		return targetObject.getGroup();
	}

	public static void resetCounter() {
		counter = 0;
	}

}
