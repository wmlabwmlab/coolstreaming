package se.peertv.peertvsim.core.sequential;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.utils.A;

public class SimulationQueue implements Queue<Event> {

	private final HashMap<Long, Queue<Event>> queueMap;
	private final PriorityQueue<Long> indexes;

	public SimulationQueue() {
		queueMap = new HashMap<Long, Queue<Event>>();
		indexes=new PriorityQueue<Long>();
	}

	private Event removeInternal(boolean remove) {
		if (!queueMap.isEmpty()) {
			Long nextTime=indexes.peek();
			Queue<Event> localQueue = queueMap.get(nextTime);
			Event e = null;

			if (localQueue != null) {
				if (remove) {
					e = localQueue.remove();

					if (localQueue.isEmpty()) {
						queueMap.remove(nextTime);
						indexes.remove(nextTime);
					}
				} else {
					e = localQueue.peek();
				}
			}
			
			return e;
		}
		return null;
	}

	@Override
	public boolean add(Event e) {
		long time = e.time;

		// if(time==Scheduler.getInstance().getNow()){
		// time+=1;
		// }
		try {
			A.ssert(time != Scheduler.getInstance().getNow());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		Queue<Event> innnerQueue;
		if (queueMap.containsKey(time)) {
			innnerQueue = queueMap.get(time);
		} else {
			innnerQueue = new LinkedList<Event>();
			queueMap.put(time, innnerQueue);
			indexes.add(time);
		}		
		innnerQueue.add(e);

		return true;
	}


	
	@Override
	public Event element() {
		Event e = removeInternal(false);
		if (e == null)
			throw new NoSuchElementException();
		return e;
	}

	@Override
	public boolean offer(Event e) {
		return add(e);
	}

	@Override
	public Event peek() {
		return removeInternal(false);
	}

	@Override
	public Event poll() {
		return removeInternal(true);
	}

	@Override
	public Event remove() {
		Event e = removeInternal(true);
		if (e == null)
			throw new NoSuchElementException();
		return e;
	}

	@Override
	public boolean addAll(Collection<? extends Event> c) {
		for (Event e : c) {
			add(e);
		}
		return true;
	}

	@Override
	public void clear() {
		queueMap.clear();
	}

	@Override
	public boolean contains(Object o) {
		if (o == null) {
			throw new NullPointerException();
		}
		if (!(o instanceof Event)) {
			throw new ClassCastException();
		}
		Event e = (Event) o;
		Queue<Event> intQueue = find(e.time);
		if (intQueue != null) {
			return intQueue.contains(e);
		}
		return false;
	}

	private Queue<Event> find(Long time) {
		if (queueMap.containsKey(time)) {
			Queue<Event> intQueue = queueMap.get(time);
			return intQueue;
		} else {
			return null;
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		if (c == null) {
			throw new NullPointerException();
		}
		int count = c.size();
		for (Object o : c) {
			if (!(o instanceof Event)) {
				throw new ClassCastException();
			}
			if (contains(o)) {
				count--;
			}

		}
		if (count == 0) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isEmpty() {
		return queueMap.isEmpty();
	}

	@Override
	public Iterator<Event> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		boolean removed=false;
		if (o == null) {
			throw new NullPointerException();
		}
		if (!(o instanceof Event)) {
			throw new ClassCastException();
		}
		Event e = (Event) o;
		Queue<Event> intQueue = find(e.time);
		if (intQueue != null) {
			removed = intQueue.remove(e);

			if (intQueue.isEmpty()) {
				queueMap.remove(e.time);
				indexes.remove(e.time);
			}
		}
		
		try {
			A.ssert(removed);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return removed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if (c == null) {
			throw new NullPointerException();
		}
		int count = c.size();
		for (Object o : c) {
			if (!(o instanceof Event)) {
				throw new ClassCastException();
			}
			if (remove(o)) {
				count--;
			}

		}
		if (count == 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		int count = 0;
		for (Queue<Event> internalQueue : queueMap.values()) {
			count += internalQueue.size();
		}
		return count;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

}
