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

import se.peertv.peertvsim.conf.Conf;

public abstract class EventLoop {

	final static Scheduler scheduler = Scheduler.getInstance();	
	
	long start;

	//The following stuff is reported in stats
	protected String causeOfTermination;
	protected long simTime;
	protected long realTime;

	
	protected  void preSimulationLoop() {
		causeOfTermination = null;
		start = System.currentTimeMillis();
	};

	protected  void postSimulationLoop() {
		if (causeOfTermination == null)
			causeOfTermination="Max Simulation Time Reached";
		
		realTime = System.currentTimeMillis() - start;
		simTime = scheduler.now;
		System.out.println("NOW EXITING");
		
		
		//P.rint(causeOfTermination);
	}

	protected  void prematureTermination(Throwable t) {
		causeOfTermination  = t.getMessage();
		realTime= System.currentTimeMillis() - start;
		simTime = scheduler.now;
		
		//P.rint(causeOfTermination);
		//P.rint(t);
		t.printStackTrace();
	}
	
	protected  void preEventExecution() {};

	protected  abstract boolean postEventExecution();
	

	public boolean run() {
		try {
			preSimulationLoop();
			while (true) {
				Event e = scheduler.dequeue();
				if (e != null && scheduler.now < Conf.MAX_SIMULATION_TIME) {
					preEventExecution();
					e.handle();
					if(postEventExecution()){
						break;
					}
				} else {
					break; // simulationEnded
				}
			}
			postSimulationLoop();
			return true;
		} catch (Throwable t) {
			prematureTermination(t);
			return false;
		}
	}

}
