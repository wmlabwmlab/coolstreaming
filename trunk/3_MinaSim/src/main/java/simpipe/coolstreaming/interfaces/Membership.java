package simpipe.coolstreaming.interfaces;

import simpipe.coolstreaming.Member;

public interface Membership {

	void setParams(int mSize,int port,int deleteTime);
	int getLength();
	int getIndex(int value);
	void addMember(int port);
	void deleteMember(int port);
	int getAnotherDeputy(int destPort);
	int[] toArray();
	void setMember(int index,Member m);
	Member getMember(int index);
	
	
}
