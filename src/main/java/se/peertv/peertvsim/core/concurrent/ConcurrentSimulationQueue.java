package se.peertv.peertvsim.core.concurrent;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.utils.A;

public class ConcurrentSimulationQueue<T> {

	private final Map<Long, CandidatesMap> queueMap;
	private final PriorityQueue<Long> indexes;

	public ConcurrentSimulationQueue() {
		queueMap = new TreeMap<Long, CandidatesMap>();
		indexes = new PriorityQueue<Long>();
	}

	public void put(T t) throws InterruptedException {

		Event e = (Event) t;

		Long time = e.time;

		String threadGroup = e.getThreadGroup();

		CandidatesMap entry = null;
		if (queueMap.containsKey(time)) {
			entry = queueMap.get(time);
		} else {
			entry = new CandidatesMap(time);
			queueMap.put(time, entry);
			indexes.add(time);
		}

		entry.addEvent(e);

	}

	public CandidatesMap removeTimeEntry() {

		// while (true) {

		Long currentTime = indexes.peek();

		// OuterMap
		CandidatesMap entry;
		if (currentTime != null && queueMap.containsKey(currentTime)) {
			entry = queueMap.remove(currentTime);
			indexes.remove(currentTime);
			return entry;

		}

		return null;

	}

	public boolean isEmpty() {
		return queueMap.isEmpty();
	}

	public void clear() {
		queueMap.clear();
	}

	public boolean remove(Event e) {

		Long time = e.time;

		CandidatesMap entry = null;
		if (queueMap.containsKey(time)) {
			entry = queueMap.get(time);
		} else {
			return false;
		}
		return entry.remove(e);

	}

	public String toString() {

		StringBuilder builder = new StringBuilder();

		for (Long index : indexes) {

			builder.append(index + ":" + queueMap.get(index) + ",");

		}

		return builder.toString();
	}

}
