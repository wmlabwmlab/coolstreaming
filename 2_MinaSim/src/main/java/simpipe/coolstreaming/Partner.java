package simpipe.coolstreaming;

import org.apache.mina.common.IoSession;

public class Partner {

	int port;
	int bandwidth;
	IoSession session;
	BitField bufferMap;
	public Partner(int port, int bandwidth, IoSession session,
			BitField bufferMap) {
		super();
		this.port = port;
		this.bandwidth = bandwidth;
		this.session = session;
		this.bufferMap = bufferMap;
	}

	
}
