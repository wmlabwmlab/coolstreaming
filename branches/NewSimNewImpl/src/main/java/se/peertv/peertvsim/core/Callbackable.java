package se.peertv.peertvsim.core;

import java.util.HashSet;
import java.util.Set;

public class Callbackable {
	Set<Timer> timers = new HashSet<Timer>();
	public void addTimer(Timer t)
	{
		timers.add(t);
	}
	public void removeTimer(Timer t)
	{
		timers.remove(t);
	}
	public void resetTimers() throws Exception
	{
		while(timers.size() > 0)
			timers.iterator().next().reset();
		timers.clear();
	}
	public void fire(){
		System.err.println("HURRAYYYYYYYYYYYYYYYYYY");
	}
}
