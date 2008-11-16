package se.peertv.peertvsim.network.udp.bw;

public interface BandwidthModelVaryingSize {
	public boolean isToSend(long timeCurrent, long units);
	
	public long getBWCapacity();
	
	public long getWindowSize();
	
	public long getCurrentWindowLoad();
	
}
