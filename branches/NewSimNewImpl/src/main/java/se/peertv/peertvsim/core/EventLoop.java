/**
 * =============================================== 
 *  File     : $Id: EventLoop.java,v 1.7 2007/06/06 09:22:49 sameh Exp $
 *  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
 *  Version  :$Revision: 1.7 $
 *  Tag	  : $Name:  $
 *  Last edited by   : $Author: sameh $
 *  Last updated:    $Date: 2007/06/06 09:22:49 $
 *===============================================
 */
package se.peertv.peertvsim.core;

import se.peertv.peertvsim.core.concurrent.ConcurrentEventLoop;
import se.peertv.peertvsim.core.sequential.SequentialEventLoop;

public abstract class EventLoop {

	static public EventLoopType type = EventLoopType.SEQUENTIAL;

	EventLoopInt evenLoopInstance;

	public EventLoop() {

		switch (type) {
		case SEQUENTIAL:

			evenLoopInstance = new SequentialEventLoop(this, Scheduler.SEQUENTIAL_SCHEDULING);
			break;

		case REPLAY_SEQUENTIAL:

			evenLoopInstance = new SequentialEventLoop(this, Scheduler.REPLAY_SCHEDULING);
			break;

		case CONCURRENT:

			evenLoopInstance = ConcurrentEventLoop.getInstance(this);
			break;

		default:
			break;
		}

	}

	public void preSimulationLoop() {
	}

	public void postSimulationLoop() {
	}

	protected void prematureTermination(Throwable t) {
		evenLoopInstance.prematureTermination(t);
	}

	public void preEventExecution() {
	};

	public boolean postEventExecution() {
		return true;
	}

	public boolean run() {
		return evenLoopInstance.run();
	}

	public static void setType(EventLoopType type) {
		EventLoop.type = type;
	}
}
