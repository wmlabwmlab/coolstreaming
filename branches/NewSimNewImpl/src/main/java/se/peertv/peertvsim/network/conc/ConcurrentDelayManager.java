/**
 * =============================================== 
 *  File     : $Id: DelayManager.java,v 1.7 2007/06/06 09:22:48 sameh Exp $
 *  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
 *  Version  :$Revision: 1.7 $
 *  Tag	  : $Name:  $
 *  Last edited by   : $Author: sameh $
 *  Last updated:    $Date: 2007/06/06 09:22:48 $
 *===============================================
 */
package se.peertv.peertvsim.network.conc;

import java.util.HashMap;
import java.util.Map;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.utils.A;
import se.peertv.peertvsim.utils.MultitonRandUtils;

public class ConcurrentDelayManager {

	Map<String, Long> delayMatrix;

	private static ConcurrentDelayManager singeltonDelayManager = null;

	public synchronized static ConcurrentDelayManager getInstance() {
		if (singeltonDelayManager == null)
			singeltonDelayManager = new ConcurrentDelayManager();
		return singeltonDelayManager;
	}

	public synchronized long getDelay(int nodeA, int nodeB) throws Exception {
		String threadGroup = Reflection.getExecutingGroup();
		if (nodeA == -1) {
			long delay = MultitonRandUtils.getDedicatedRandom(threadGroup)
					.nextInt(500) + 1;
			// System.out.println("TG: " + Reflection.getExecutingGroup() + " "
			// + SimulableSystem.currentTimeMillis() + " Delay from " + nodeA
			// + " to " + nodeB + " = " + delay);
			return delay;
		}
		// A.ssert(nodeA != nodeB);
		if (nodeA == nodeB) {
			long delay = MultitonRandUtils.getDedicatedRandom(threadGroup)
					.nextInt(15) + 1;
			// System.out.println("TG: " + Reflection.getExecutingGroup() + " "
			// + SimulableSystem.currentTimeMillis() + " Delay from " + nodeA
			// + " to " + nodeB + " = " + delay);
			if (delay < 5)
				delay = 5;
			if (delay > 15)
				delay = 15;
			return delay;
		}

		// This is assymetric delay.. but is doesn't have to be that way
		String linkLabel = nodeA + "-" + nodeB;
		if (delayMatrix.containsKey(linkLabel))
			return delayMatrix.get(linkLabel);

		long delay = MultitonRandUtils.getDedicatedRandom(threadGroup).nextInt(
				Conf.MAX_LINK_DELAY) + 1;
		// FIXME:
		// (long)(1000 * 5000 / Math.max(
		// (Network.getInstance().get(nodeA).upCapacity+1) ,
		// (Network.getInstance().get(nodeB).downCapacity+1)));

		if (delay < 15)
			delay = 15;
		if (delay > Conf.MAX_LINK_DELAY)
			delay = Conf.MAX_LINK_DELAY;

		if (delay <= 0)
			A.ssert(delay > 0);

		// System.out.println("TG: " + Reflection.getExecutingGroup() + " "
		// + SimulableSystem.currentTimeMillis() + " Delay from " + nodeA
		// + " to " + nodeB + " = " + delay);

		delayMatrix.put(linkLabel, delay);

		return delay;
	}

	private ConcurrentDelayManager() {
		delayMatrix = new HashMap<String, Long>();
	}

	@Override
	public String toString() {
		String s = "";
		for (String label : delayMatrix.keySet())
			s += label + ": " + delayMatrix.get(label) + "\n";
		return s;
	}

	public void clear() {
		delayMatrix.clear();
	}

}