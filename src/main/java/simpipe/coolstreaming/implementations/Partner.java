package simpipe.coolstreaming.implementations;

import org.apache.mina.common.IoSession;

import simpipe.coolstreaming.BitField;

public class Partner {

	int port;
	int bandwidth;
	IoSession session;
	BitField bufferMap;
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}
	public IoSession getSession() {
		return session;
	}
	public void setSession(IoSession session) {
		this.session = session;
	}
	public BitField getBufferMap() {
		return bufferMap;
	}
	public void setBufferMap(BitField bufferMap) {
		this.bufferMap = bufferMap;
	}
	public Partner(int port, int bandwidth, IoSession session,
			BitField bufferMap) {
		super();
		this.port = port;
		this.bandwidth = bandwidth;
		this.session = session;
		this.bufferMap = bufferMap;
	}

	
}
