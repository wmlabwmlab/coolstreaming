package simpipe.coolstreaming.interfaces;

import simpipe.coolstreaming.BitField;
import simpipe.coolstreaming.PeerNode;

public interface Scheduler {

	void setParams(PeerNode node, int startTime);
	void fillDeadLine();
	boolean isValid(int index,int timeNow);
	BitField getWindow(int now);
	void exchangeBM();
	BitField beginscheduling();
	int pickPeer(int[] bandwidth);
	String toString();
	void identifyRequiredSegments();
	int getWholeBits(int index);
	void setWholeBits(int index, int value);
	int getDeadLine(int index);
	int getExchangeTime();
	void setStartTime(int start);
	public void subscribe(char header);
	public void fire(char header);
	void setWholeBits(int startIndex, int endIndex, int value);
	
}
