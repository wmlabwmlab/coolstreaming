package se.peertv.peertvsim.network.udp.bw;

import java.util.LinkedList;
import java.util.List;

public class BandwidthModelImpl5WithVaryingSize implements BandwidthModelVaryingSize {

	// Capacity
	private final long bytesPerWindow;

	// Window Size
	private long windowSize;

	boolean TRACE = false;

	// TimeStamp's list
	List<Double> timesList;

	// Size's List
	List<Double> sizeList;

	// Current Window Byte size
	long currentWindowByteSize;

	double byteTimeWindow;


	public BandwidthModelImpl5WithVaryingSize(long bytesPerWindow, long windowSize, boolean trace) {
		this.bytesPerWindow = bytesPerWindow;
		this.windowSize=windowSize;
		
		TRACE = trace;
		timesList = new LinkedList<Double>();
		sizeList = new LinkedList<Double>();
		

		byteTimeWindow = (double) windowSize / (double) bytesPerWindow;

	}

	/**
	 * Decides to send or not a packet according to the bandwidth of a peer
	 * 
	 * @param tc
	 *            the time the packet should be sent
	 * @return true if the packet can be sent, false otherwise
	 */
	public boolean isToSend(long timeCurrent, long packetSize) {

		boolean send = false;

		double packetTimeWindow = packetSize * (byteTimeWindow);

		if (timesList.isEmpty()) {

			addWindow(timeCurrent, packetTimeWindow, packetSize);

			send = true;

		} else {

			if (currentWindowByteSize + packetSize <= bytesPerWindow) {

				addWindow(timeCurrent, packetTimeWindow, packetSize);

				send = true;

			} else {
				double timeStamp = timesList.get(0);

				boolean finished = false;

				while (!finished) {

					// Remove sent packets
					if (timeStamp <= timeCurrent) {

						timesList.remove(0);

						// Compute new size
						double oldPacketSize = sizeList.remove(0);

						currentWindowByteSize -= oldPacketSize;

						// Check if the removal of the old packet has made room for the new one
						if (currentWindowByteSize + packetSize <= bytesPerWindow) {

							addWindow(timeCurrent, packetTimeWindow, packetSize);

							send = true;

							finished = true;
						}

					} else {

						double timeRemaining = timeStamp - timeCurrent;

						double bytesRemaining = byteTimeWindow * 10 * timeRemaining;

						double lastPacketSize = sizeList.get(0);

						double bytesSent = lastPacketSize - bytesRemaining;

						if (bytesSent >= packetSize) {

							// We don't actually touch the timeStamp for the last packet since it still represents the correct time when the packet
							// will be completely sent

							// Resize last packet to make room for the new one
							sizeList.remove(0);
							sizeList.add(0, bytesRemaining);

							// Add the new one

							addWindow(timeCurrent, packetTimeWindow, lastPacketSize);

							send = true;

						}

						finished = true;

					}

					// We drop the packet if the size of the packet is more than the capacity of the peer
					if(timesList.isEmpty() && currentWindowByteSize + packetSize > bytesPerWindow){
						finished=true;
					}else{
						timeStamp = timesList.get(0);
					}

				}

			}
		}

		if (TRACE) {
			System.out.println("Time: " + timeCurrent);
			System.out.println("Send: " + send);
		}
		return send;
	}

	private void addWindow(double timeCurrent, double packetTimeWindow, double packetSize) {

		double lastValue = 0;

		if (!timesList.isEmpty()) {
			lastValue = timesList.get(timesList.size() - 1);

			if (lastValue < timeCurrent) {
				lastValue = timeCurrent;
			}

		} else {
			lastValue = timeCurrent;
		}

		double nextUsefulTime = lastValue + packetTimeWindow;

		timesList.add(nextUsefulTime);

		currentWindowByteSize += packetSize;

		sizeList.add(packetSize);
	}

	@Override
	public long getBWCapacity() {
		return bytesPerWindow;
	}

	@Override
	public long getCurrentWindowLoad() {
		return currentWindowByteSize;
	}

	@Override
	public long getWindowSize() {
		return windowSize;
	}
}
