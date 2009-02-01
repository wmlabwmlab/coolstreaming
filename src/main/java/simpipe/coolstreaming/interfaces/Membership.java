package simpipe.coolstreaming.interfaces;

import simpipe.coolstreaming.implementations.Member;

public interface Membership {

	void setParams(int mSize,int port,int deleteTime);
	int getStranger(Partnership p);
	int getLength();
	int getIndex(int value);
	void addMember(int port);
	void refreshCache();
	int getAnotherDeputy(int destPort);
	int[] toArray();
	void setMember(int index,Member m);
	Member getMember(int index);
	public void subscribe(char header);
	public void fire(char header);
}
