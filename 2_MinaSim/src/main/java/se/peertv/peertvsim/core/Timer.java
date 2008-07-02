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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.utils.A;


public class Timer{
	private long firingTime;
	private Object targetObject;
	private String callBackFunction;
	private Event eventRef = null;
	private int param;
	
	public Timer() {}

	public Timer(long delay, Object targetObject, String callBackFunction) throws Exception{
		set(delay,  targetObject, callBackFunction);		
	}
	public Timer(long delay, Object targetObject, String callBackFunction,int param) throws Exception{
		this.param=param;
		set(delay,  targetObject, callBackFunction);		
	}
	
	public void set(long delay, Object targetObject, String callBackFunction) throws Exception {
		reset();
		this.firingTime = Scheduler.getInstance().now+delay;
		this.targetObject = targetObject;
		this.callBackFunction = callBackFunction;
		eventRef = new TimerEvent(firingTime, this);
		Scheduler.getInstance().enqueue(eventRef);
		//P.rint("t("+Scheduler.getInstance().now+") TIMR_SET"+this);
		//this.targetObject.addTimer(this);
	}

	public void reset() throws Exception{
		if(this.targetObject != null)
			//this.targetObject.removeTimer(this);
		if  (eventRef!=null){
			boolean removed= Scheduler.getInstance().remove(eventRef);
			A.ssert(removed, "not removed");
			eventRef = null;
			targetObject = null;
			callBackFunction = null;
		}
	}


	public void fire() throws Throwable{
		eventRef = null;
		//this.targetObject.removeTimer(this);
		try {
			//Class[] argType = { String.class, Integer.TYPE };
			Class[] argType = { Integer.TYPE };
			Method handler =  targetObject.getClass().getMethod(callBackFunction, argType);					
			Object[] arg = {new Integer(param) };
			handler.invoke(targetObject,arg);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw e.getCause();
		}
	}

}
