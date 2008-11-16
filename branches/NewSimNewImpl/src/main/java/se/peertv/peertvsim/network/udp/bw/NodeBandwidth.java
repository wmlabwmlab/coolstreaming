package se.peertv.peertvsim.network.udp.bw;

public class NodeBandwidth {

	long downBandwidth;
	long downWindowSize;
	long upBandwidth;
	long upWindowSize;

	public NodeBandwidth(long downBandwidth, long downWindowSize, long upBandwidth, long upWindowSize) {
		super();
		this.downBandwidth = downBandwidth;
		this.downWindowSize = downWindowSize;
		this.upBandwidth = upBandwidth;
		this.upWindowSize = upWindowSize;
	}

	public long getDownBandwidth() {
		return downBandwidth;
	}

	public long getDownWindowSize() {
		return downWindowSize;
	}

	public long getUpBandwidth() {
		return upBandwidth;
	}

	public long getUpWindowSize() {
		return upWindowSize;
	}

}
