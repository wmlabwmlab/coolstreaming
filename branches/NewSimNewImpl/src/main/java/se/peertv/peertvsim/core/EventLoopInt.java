package se.peertv.peertvsim.core;

public interface EventLoopInt {

	public void preSimulationLoop();

	public void postSimulationLoop();

	public void prematureTermination(Throwable t);

	public boolean run();

}
