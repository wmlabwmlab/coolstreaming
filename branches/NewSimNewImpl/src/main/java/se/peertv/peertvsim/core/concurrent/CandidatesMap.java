package se.peertv.peertvsim.core.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.utils.A;

public class CandidatesMap {

	private Long time;

	private Map<String, Queue<Event>> timePointMap;

	public CandidatesMap(Long time) {
		this.time = time;
		this.timePointMap = new TreeMap<String, Queue<Event>>();
	}

	public Long getTime() {
		return time;
	}

	public Set<String> getGroups() {
		return timePointMap.keySet();
	}

	public void addEvent(Event e) {

		try {
			A.ssert(e != null, "Event null when adding in queue");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String threadGroup = e.getThreadGroup();

		// InnerMap
		Queue<Event> innerQueue;
		if (!timePointMap.containsKey(threadGroup)) {
			
			ConcurrentEventComparator comparator=new ConcurrentEventComparator();
			
			innerQueue = new PriorityQueue<Event>(1,comparator);
			
			timePointMap.put(threadGroup, innerQueue);
		} else {
			innerQueue = timePointMap.get(threadGroup);
		}

		innerQueue.add(e);
	}

	public Event pollGroup(String group) {

		if (!timePointMap.isEmpty() && timePointMap.containsKey(group)) {
			Event e = timePointMap.get(group).poll();
			return e;
		} else {
			return null;
		}
	}

	public boolean isGroupEmpty(String group) {
		if (timePointMap.containsKey(group)) {
			return timePointMap.get(group).isEmpty();
		} else {
			return true;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (String group : timePointMap.keySet()) {

			builder.append("GR:" + group);
			Queue<Event> events = timePointMap.get(group);
			builder.append(events);
			builder.append(";");
		}

		return builder.toString();
	}

	public boolean remove(Event e) {
		String group = e.getThreadGroup();

		if (timePointMap.containsKey(group)) {
			Queue<Event> queue = timePointMap.get(group);
			boolean removed = queue.remove(e);
			if (queue.isEmpty())
				timePointMap.remove(group);
			return removed;
		} else {
			return false;
		}
	}
}
