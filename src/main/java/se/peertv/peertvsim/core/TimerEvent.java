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

import java.io.Serializable;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.utils.A;
import se.peertv.peertvsim.utils.P;

/**
 * @author sameh
 * 
 */
public class TimerEvent extends Event implements Serializable {

	private static final long serialVersionUID = -4309747954937981541L;

//	public long time;
	public int nodeId;
	public long timerId;

	@Override
	public void handle() throws Throwable {

		Timer timer = Timers.getInstance().getTimer(nodeId, timerId);

		try {
			A.ssert(timer != null, "timer is null");

		} catch (Exception e) {
			System.out.println("s");
		}

		Reflection.setExecutingNode(timer.getTargetObjectId());

		// String group=timer.getTargetGroup();

		Reflection.setExecutingGroup(timer.getTargetGroup());
		if (SimulableSystem.isMinaSimTraceEnabled())
			P.rint("t(" + time + ") " + timer.toString());
		timer.fire();
	}

	@Override
	public String toString() {
		return super.toString() + " Timer id: " + timerId + " nodeId " + nodeId;
	}

	@Override
	public String getThreadGroup() {
		Timer timer = Timers.getInstance().getTimer(nodeId, timerId);
		return timer.getTargetGroup();
	}
}
