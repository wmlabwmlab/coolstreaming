package se.peertv.peertvsim.network.tcp.bw;

import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.Message;
import se.peertv.peertvsim.network.MessageDeliveryEvent;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.utils.A;
import se.peertv.peertvsim.utils.UnitConversion;

/**
 * This class represents an ongoing transfer.
 * It is mainly used to keep track of the transfer
 * and the amount of bandwidth it is using.
 * When bandwidth changes, it will reschedule the receive 
 * event. On {@link Node} failure, it will cancel the receive event
 * and inform the recipient about the  the failure.
 * Several Transfer instances are manager by a single {@link BandwidthManager}. 
 * 
 * @author meemo
 *
 */
public class Transfer {
	Scheduler scheduler = Scheduler.getInstance();
	
	BandwidthManager sourceBandwidthManager;
	BandwidthManager destBandwidthManager;
	private long totalBytes;
	double bandwidthInBPS;
	private long startTime;	
	public MessageDeliveryEvent deliveryEvent;
	private double nextEpochBW = -1;
	
	private Message message; // for debug purposes only
	public long originalStartTime; // for debug purposes only 
	public long originalBytes; // for debug purposes only
	double looseValue = -1;

	public boolean failed;
	
		
	
	public Transfer(double bandwidthInBPS, BandwidthManager sourceBandwidthManager,
			BandwidthManager destBandwidthManager,
			long bytes, MessageDeliveryEvent evt) throws Exception {
		super();
		
		A.ssert(bandwidthInBPS > 0);
		A.ssert(bytes > 0);
		A.ssert(sourceBandwidthManager != destBandwidthManager);
		
		this.setBandwidthInBPS(bandwidthInBPS);
		this.setSourceBandwidthManager(sourceBandwidthManager);
		this.setDestBandwidthManager(destBandwidthManager);
		this.totalBytes = bytes;
		this.deliveryEvent = evt;
		this.originalStartTime = this.startTime = scheduler.getNow();
		this.originalBytes = this.totalBytes;
	}	
	
	public long getRemainingBytes() throws Exception
	{
		// if total bytes less than what can be transferred in a single milli second then return 0
		long bytesPerMilliSec = (long)(bandwidthInBPS/1000.0); // 1000 to convert BPS to Byte per millisec
		if(totalBytes <= bytesPerMilliSec)
			return 0;
//		
		long timeInQueue = scheduler.getNow() - startTime;
		long bytesTransferred = timeInQueue * bytesPerMilliSec;
		
		if(bytesTransferred > totalBytes)
			A.ssert(bytesTransferred <= totalBytes);
		
		long bytesRemaining = totalBytes - bytesTransferred;
		
		return bytesRemaining;
		//return (deliveryEvent.time - Scheduler.getInstance().now)*bytesPerMilliSec;
	}

	/**
	 * This method should reschedule the reception event time. 
	 * @param newBandwidth
	 * @throws Exception 
	 */
	public void resetBandwidth(double newBandwidth) throws Exception {		
		A.ssert(newBandwidth >= 0);
		
		A.ssert(Math.rint(newBandwidth) == newBandwidth);
		
		if(deliveryEvent.time < scheduler.getNow())
		{
			System.err.println("dlvr: " + deliveryEvent.time + " < " + " now: " +scheduler.getNow());
			A.ssert(deliveryEvent.time >= scheduler.getNow());			
		}
		
		if(this.deliveryEvent.time == scheduler.getNow())
		{
			// this is accounting info only, don't actually affect anything, cuz there
			// is nothing to be changed, cuz this transfer is already dead and 
			// this is just a matter of ordering of reception of connections 
			// at the same milli second.
			// this new bandwidth is just will be used as accounting info in the 
			// bandwidth reallocation not as means of re-timing a transfer.
			this.bandwidthInBPS = newBandwidth;
			return;
		}
		
		long bytesRemaining = getRemainingBytes();
		
		if(deliveryEvent.time > scheduler.getNow())
		{	
			A.ssert(bytesRemaining >= 0);
			
			long oldTimeRemaining = deliveryEvent.time - Scheduler.getInstance().getNow(); 
				//bytesRemaining>0?(long)UnitConversion.secToMilliSec(bytesRemaining/bandwidthInBPS):0;
			
			if(oldTimeRemaining > 0)
			{
				scheduler.remove(deliveryEvent);
				
				// ms = b/(b/ms)
				long newTime = (long) UnitConversion.secToMilliSec(bytesRemaining / newBandwidth);
				if(newTime == 0)
					newTime = 1;
				
				deliveryEvent.setTime( newTime + scheduler.getNow() );
				
				// only for debugging purposes..
				/*P.rint("\tModified transfer <" + message.seqNum + "> " + getSourceBandwidthManager().getNodeId() + "-->" + getDestBandwidthManager().getNodeId() +
						" from " + bandwidthInBPS + " BPS to " + newBandwidth + " BPS, time remaining was " + 
						oldTimeRemaining + " ms now is " + newTime + " ms, " +
						"delivery time = " + deliveryEvent.time + (loose()?" LOOSE":" TIGHT"));//*/
				
				scheduler.enqueue(deliveryEvent);			
			}
		}
		// like a brand new transfer
		this.totalBytes = bytesRemaining;
		this.startTime = scheduler.getNow();
		this.bandwidthInBPS = newBandwidth;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Transfer))
			return false;
		return this.message.equals(((Transfer)obj).message);
	}

	public void setBandwidthInBPS(double bandwidthInBPS) throws Exception {
		A.ssert(bandwidthInBPS >= 0);
		this.bandwidthInBPS = bandwidthInBPS;
	}

	public void setSourceBandwidthManager(BandwidthManager sourceBandwidthManager) {
		this.sourceBandwidthManager = sourceBandwidthManager;
	}

	public BandwidthManager getSourceBandwidthManager() {
		return sourceBandwidthManager;
	}

	public BandwidthManager getOtherBandwidthManagerThan(BandwidthManager bandwidthManager) {
		return bandwidthManager == sourceBandwidthManager? destBandwidthManager : sourceBandwidthManager;
	}

	public BandwidthManager getDestBandwidthManager() {
		return destBandwidthManager;
	}

	public void setDestBandwidthManager(BandwidthManager destBandwidthManager) {
		this.destBandwidthManager = destBandwidthManager;
	}

	public void setMessage(Message message) {
		this.message = message; // for debug purposes only		
	}	
	
	public Message getMessage() {
		return this.message;
	}

	public void setNextEpochBW(double bw) throws Exception {
		A.ssert(Math.rint(bw) == bw);
		this.nextEpochBW = bw;		
	}

	public double getNextEpochBW() {		
		return nextEpochBW;
	}

	public void applyNextEpochBW() throws Exception {
		if(nextEpochBW < 0)
			return;
		
		this.resetBandwidth(nextEpochBW);
		
		nextEpochBW = -1;
	}

	public void fail() throws Exception {
		scheduler.remove(deliveryEvent);		
		destBandwidthManager.receive(this); // the same effect as deallocating bandwidth
		this.failed = true; // prevent duplicate receive (deallocation) of this transfer
	}

	// can this transfer be maxed out ?
	public boolean loose() {
		return Math.floor(looseValue()) >= BandwidthManager.BANDWIDTH_TOL;
	}
	
	// the amount that can be added to this transfer
	public double looseValue() {
		return looseValue;
	}
	
	public double updatedLooseValue() {
		looseValue = Math.min(sourceBandwidthManager.preferredAddTo(this), destBandwidthManager.preferredAddTo(this));
		return looseValue;	
	}
}
