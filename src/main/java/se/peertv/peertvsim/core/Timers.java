package se.peertv.peertvsim.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Timers {

	private static Timers timersSingelton = null;
	private HashMap<Integer, Map<Long, Timer>> timerMap;

	private Set<Long> removedTimers = new HashSet<Long>();

	public static Timers getInstance() {
		if (timersSingelton == null)
			timersSingelton = new Timers();
		return timersSingelton;
	}

	private Timers() {
		timerMap = new HashMap<Integer, Map<Long, Timer>>();
	}

	public void reset() {
		timerMap.clear();
	}

	public/* synchronized */void addTimer(int nodeId, long timerId, Timer t) {
		Map<Long, Timer> timers;
		if (!timerMap.containsKey(nodeId)) {
			timers = new HashMap<Long, Timer>();
			timerMap.put(nodeId, timers);
		} else {
			timers = timerMap.get(nodeId);
		}
		timers.put(timerId, t);
	}

	public/* synchronized */void removeTimer(int nodeId, long timerId, Timer t) {

		// removedTimers.add(timerId);

		if (timerMap.containsKey(nodeId)) {
			Map<Long, Timer> timers = timerMap.get(nodeId);
			timers.remove(timerId);
		}
	}

	public/* synchronized */Timer getTimer(int nodeId, long timerId) {
		if (timerMap.containsKey(nodeId)) {
			Map<Long, Timer> timers = timerMap.get(nodeId);

			if (timers.containsKey(timerId)) {
				return timers.get(timerId);
			} else {
				// if (removedTimers.contains(timerId)) {
				// System.out.println("rem previously");
				// } else {
				// System.out.println("not");
				//
				// }
			}

		}
		return null;
	}

}
