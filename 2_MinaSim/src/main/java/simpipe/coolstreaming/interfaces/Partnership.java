package simpipe.coolstreaming.interfaces;

import org.apache.mina.common.IoSession;

import simpipe.coolstreaming.Partner;
import simpipe.coolstreaming.PeerNode;

public interface Partnership {

	void setParams(int pSize,int port,int windowSize,int defaultBandwidth, PeerNode node );
	boolean addPartner(int port,IoSession session);
	void forceAddPartner(int port,IoSession session);
	void setBandwidth(int port, int bandwidth);
	void deletePartner(int port);
	void clearPartners();
	String getPartners();
	Partner getPartner(int i);
	void setPartner(int index,Partner p);
	int getIndex(int value);
	int[] toArray();
	int getLength();
	Partner[] getCache();
}
