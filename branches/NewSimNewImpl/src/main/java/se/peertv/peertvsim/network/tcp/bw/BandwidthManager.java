package se.peertv.peertvsim.network.tcp.bw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.DelayManager;
import se.peertv.peertvsim.network.Message;
import se.peertv.peertvsim.network.MessageDeliveryEvent;
import se.peertv.peertvsim.network.Network;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.network.tcp.TCPNode;
import se.peertv.peertvsim.utils.A;
import se.peertv.peertvsim.utils.P;
import se.peertv.peertvsim.utils.UnitConversion;
import simpipe.base.support.peers.TCPPeer;
import simpipe.protocol.SimpPipeUDPMessage;

/**
 * This classes is attached to one and only one {@link Node}. It is responsible to manage the bandwidth allocation and deallocation on that node. This
 * classes manages several {@link Transfer} instances. An instance is either a download manager or an upload manager.
 * 
 * @author meemo
 * 
 */
public class BandwidthManager {

	// the difference from zero of which we consider a number is zero
	static final double BANDWIDTH_TOL = 1;

	private Scheduler scheduler = Scheduler.getInstance();
	private DelayManager delayManager = DelayManager.getInstance();
	private Network network = Network.getInstance();

	public long totalBandwidthInBPS;
	double availableInBPS;
	private int nodeId;

	ArrayList<Transfer> transfers = new ArrayList<Transfer>();

	public BandwidthManager(long BPS, int nodeId) {
		super();
		this.availableInBPS = this.totalBandwidthInBPS = BPS;
		this.nodeId = nodeId;
	}

	private double getNextTransferBandwidth() {
		return Math.max(availableInBPS, totalBandwidthInBPS / (double) (transfers.size() + 1));
	}

	/**
	 * This method will modify the existing transfers. It will only modify those who are taking more than their fairshare. It will modify them
	 * proportional to their bandwidth, i.e. the more share they have, the more they will be cut.
	 * 
	 * @invariant availableBandwidthInBPS >= bps
	 * @param bps
	 *            the target bandwidth
	 * @throws Exception
	 */
	private void makeRoomFor(final double bps) throws Exception {
		double fairShare = totalBandwidthInBPS / (double) (transfers.size() + 1);

		// assert less than fair share
		// A.ssert(bps <= getNextTransferBandwidth());

		ArrayList<Transfer> trespassingTransfers = new ArrayList<Transfer>();

		sanityCheck();

		// this will only be true if the fair share is larger than the available bandwidth
		if (bps > availableInBPS) // if this is true, then there is trespassing transfers
		{
			final double amountToReclaim = bps - availableInBPS;
			double actualReclaimed = 0;

			double total = 0;
			for (Transfer t : transfers) {
				if (t.bandwidthInBPS > fairShare) {
					trespassingTransfers.add(t);
					total += t.bandwidthInBPS;
				}
			}

			// sort them ascending by bandwidth
			Collections.sort(trespassingTransfers, new Comparator<Transfer>() {
				public int compare(Transfer o1, Transfer o2) {
					return (int) (o1.bandwidthInBPS - o2.bandwidthInBPS);
				}
			});

			double extra = 0;

			for (Transfer t : trespassingTransfers) {
				double deduction = amountToReclaim * t.bandwidthInBPS / total;

				deduction = Math.ceil(deduction); // to prevent future rounding errors, avoid fractions

				if (t.bandwidthInBPS - deduction < fairShare) {
					double oldDeduction = deduction;
					deduction = t.bandwidthInBPS - fairShare;
					extra += oldDeduction - deduction;
				} else if (t.bandwidthInBPS - deduction > fairShare && extra > 0) {
					// take from extra
					double oldDeduction = deduction;
					double newBW = t.bandwidthInBPS - deduction;
					double diffFromFairShare = newBW - fairShare;
					if (diffFromFairShare > extra) {
						double takeFromExtra = extra;
						deduction += takeFromExtra;
					} else // diff <= extra
					{
						double takeFromExtra = diffFromFairShare;
						deduction += takeFromExtra;
					}
					extra -= oldDeduction - deduction;
				}

				deduction = Math.ceil(deduction);

				actualReclaimed += deduction;

				BandwidthManager remoteBwm = t.getOtherBandwidthManagerThan(this);
				A.ssert(remoteBwm != this);
				// remoteBwm.sanityCheck();

				if (deduction < BANDWIDTH_TOL)
					continue;

				double newBw = t.bandwidthInBPS - deduction;
				t.setNextEpochBW(newBw);
			}

			HashSet<BandwidthManager> candidates = new HashSet<BandwidthManager>();

			for (Transfer t : trespassingTransfers) {
				if (t.getNextEpochBW() < 0)
					continue;

				double deducted = t.bandwidthInBPS - t.getNextEpochBW();

				t.applyNextEpochBW();

				BandwidthManager remoteBWM = t.getOtherBandwidthManagerThan(this);

				remoteBWM.availableInBPS += deducted;

				candidates.add(remoteBWM);
			}

			availableInBPS += actualReclaimed;
			A.ssert(availableInBPS > bps || Math.abs(availableInBPS - bps) < BANDWIDTH_TOL);

			// we are going to lie about our available bandwidth cuz we are reserving bps of them.
			// availableInBPS -= bps;
			double oldVal = availableInBPS;
			availableInBPS = 0;
			for (BandwidthManager bwm : candidates)
				bwm.boostLocalTransfers();
			// availableInBPS += bps;
			availableInBPS = oldVal;
		}

		A.ssert(availableInBPS > bps || Math.abs(availableInBPS - bps) < BANDWIDTH_TOL);
	}

	/*
	 * This method is called upon returning bandwidth to availabeInBPS and we want the current transfers to make use of it.
	 */
	public void boostLocalTransfers() throws Exception {
		if (transfers.isEmpty() || this.nodeId == /* backup source */30 || this.transfers.size() > 50 /*
																									 * effectively means that each transfer fair share
																									 * is < 2%
																									 */)
			return;

		// sanityCheck();

		P.rint(this.nodeId + " boosting local transfers");

		ArrayList<Transfer> looseTransfers = new ArrayList<Transfer>();

		for (Transfer t : transfers) {
			t.updatedLooseValue();
			if (Math.floor(t.looseValue) > BANDWIDTH_TOL) {
				looseTransfers.add(t);
			}
		}

		int loops = 0;

		while (!(looseTransfers.isEmpty() || availableInBPS < BANDWIDTH_TOL * (looseTransfers.size()))) {
			loops++;

			Set<BandwidthManager> candidates = new HashSet<BandwidthManager>();

			double error = 0;

			for (Transfer t : looseTransfers) {
				double added = t.looseValue;

				added = Math.floor(added); // to prevent future rounding errors, remove fractions

				error += t.looseValue - added;

				if (added < BANDWIDTH_TOL)
					continue;

				candidates.add(t.getOtherBandwidthManagerThan(this));

				t.setNextEpochBW(t.bandwidthInBPS + added);
			}

			int errorBytes = (int) error;

			for (Transfer t : looseTransfers) {
				A.ssert(t.getNextEpochBW() > 0);

				double added = t.getNextEpochBW() - t.bandwidthInBPS;

				t.applyNextEpochBW();

				t.sourceBandwidthManager.availableInBPS -= added;
				t.destBandwidthManager.availableInBPS -= added;
			}

			ArrayList<Transfer> stillLoose = new ArrayList<Transfer>();

			for (Transfer t : looseTransfers) {
				t.updatedLooseValue();
				if (Math.floor(t.looseValue) > BANDWIDTH_TOL) {
					stillLoose.add(t);
				}
			}

			looseTransfers = stillLoose;

			if (errorBytes < totalBandwidthInBPS * 2.0 / 100)
				break;
		}

		// for(Transfer t : transfers)
		// {
		// A.ssert(!t.loose());
		// }
		P.rint("boosting done in " + loops + " loops");
	}

	// FIXME assure this function is transitive...
	double preferredAddTo(Transfer t) {
		final double boostTol = 1; // // don't boost connections below 10 Kbps
		if (this.availableInBPS < BANDWIDTH_TOL || Math.min(t.sourceBandwidthManager.availableInBPS, t.destBandwidthManager.availableInBPS) < BANDWIDTH_TOL * boostTol)
			return 0;

		double usedBw = 0;// BPS - availableInBPS;
		int numLoose = 0;
		for (Transfer t1 : transfers)
			if (Math.min(t1.sourceBandwidthManager.availableInBPS, t1.destBandwidthManager.availableInBPS) > BANDWIDTH_TOL * boostTol) {
				usedBw += t1.bandwidthInBPS;
				numLoose++;
			}

		double retval = 0;
		if (usedBw > 0)
			retval = availableInBPS * t.bandwidthInBPS / usedBw;
		else
			retval = 0;// */

		// double retval = availableInBPS/transfers.size();
		// retval = Math.floor(retval); // to prevent future rounding errors, remove fractions

		return retval;
	}

	// private double preferredAddTo(Transfer t) {
	// int numLoose = 0;
	// for(Transfer t1 : transfers)
	// if(t1.loose())
	// numLoose++;
	// double retval = availableInBPS / numLoose;
	//		
	// retval = Math.floor(retval); // to prevent future rounding errors, remove fractions
	//		
	// return retval;
	// }

	/**
	 * This method does bandwidth allocation. This method should be used only for upload bandwidth managers.
	 * 
	 * @param remoteNodeId
	 * @param message
	 * @param bytes
	 * @throws Exception
	 */
	public Transfer sendSize(int remoteNodeId, Message message, long bytes) throws Exception {
		// System.out.println("Send Bytes: ");

		// the delay is only used as a minimum possible delay in case the bandwidth delay is less that it
		// in other words, the final delay will not be less that 'delay'.
		long delay = 0;// delayManager.getDelay(this.nodeId, remoteNodeId);

		Node remoteNode = (network.get(remoteNodeId));

		if (remoteNode instanceof TCPNode) {
			TCPNode tcpNode = (TCPNode) remoteNode;

			BandwidthManager remoteBandwidthManager = tcpNode.getDownBandwidthManager();

			sanityCheck();
			remoteBandwidthManager.sanityCheck();

			double bandwidthInBPS = Math.min(getNextTransferBandwidth(), remoteBandwidthManager.getNextTransferBandwidth());

			bandwidthInBPS = Math.floor(bandwidthInBPS); // to prevent future rounding errors, prevent fractions

			// ms = b/(b/ms)
			long timeInMilliSec = Math.max((long) UnitConversion.secToMilliSec(bytes / (bandwidthInBPS)),
			/* delay */0);

			long now = scheduler.getNow();

			message.setDest(remoteNodeId);
			message.setSource(this.nodeId);
			message.setSize(bytes);
			MessageDeliveryEvent evt = new MessageDeliveryEvent(now + timeInMilliSec, (SimpPipeUDPMessage) message);

			Transfer trns = new Transfer(bandwidthInBPS, this, remoteBandwidthManager, bytes, evt);
			message.setTransfer(trns);
			trns.setMessage(message); // for debug purposes only

			sanityCheck();
			remoteBandwidthManager.sanityCheck();

			this.makeRoomFor(bandwidthInBPS);
			this.availableInBPS -= bandwidthInBPS;

			remoteBandwidthManager.makeRoomFor(bandwidthInBPS);
			remoteBandwidthManager.availableInBPS -= bandwidthInBPS;

			// this must not be done before we call makeRoomFor,
			// cuz that function depend on the old size of the transfers array List
			// and it depends on the old availableInBPS
			transfers.add(trns);
			remoteBandwidthManager.transfers.add(trns);

			sanityCheck();
			remoteBandwidthManager.sanityCheck();

			scheduler.enqueue(evt);

			P.rint("t( " + scheduler.getNow() + " ) SEND BYTES (" + message.getSource() + "->" + message.getDest() + ") " + "[" + bytes + " b] , alloc_bw = " + bandwidthInBPS + " BPS of " + totalBandwidthInBPS
					+ ", curr_trans = " + transfers.size() + "/ " + remoteBandwidthManager.transfers.size() + ", time_est. = " + timeInMilliSec + " ms, delvr = " + evt.getTime() + ", " + message.toString());

			return trns;

		}
		return null;
	}

	/*
	 * Make sure that available bandwidth + used bandwidth sum to the original total bandwidth
	 */
	private void sanityCheck() throws Exception {
		if (Math.abs(availableInBPS) < BANDWIDTH_TOL / 10)
			availableInBPS = 0;
		if (Math.abs(availableInBPS - totalBandwidthInBPS) < BANDWIDTH_TOL / 10)
			availableInBPS = totalBandwidthInBPS;
		if (!Conf.BW_SANITY_CHECK)
			return;

		A.ssert(totalBandwidthInBPS >= -BANDWIDTH_TOL);
		A.ssert(availableInBPS >= -BANDWIDTH_TOL);
		if (totalBandwidthInBPS - availableInBPS < -BANDWIDTH_TOL)
			A.ssert(totalBandwidthInBPS - availableInBPS >= BANDWIDTH_TOL);

		sumUsedSanityCheck();
	}

	private void sumUsedSanityCheck() throws Exception {
		if (!Conf.BW_SANITY_CHECK)
			return;

		if (transfers.isEmpty())
			A.ssert(availableInBPS == totalBandwidthInBPS);

		double usedBandwidth = 0;
		for (Transfer t : transfers) {
			if (t.deliveryEvent.time < scheduler.getNow())
				A.ssert(t.deliveryEvent.time >= scheduler.getNow());
			usedBandwidth += t.bandwidthInBPS;
		}
		if (Math.abs(((usedBandwidth + availableInBPS) - this.totalBandwidthInBPS) / transfers.size()) > BANDWIDTH_TOL)
			A.ssert(Math.abs(((usedBandwidth + availableInBPS) - this.totalBandwidthInBPS)) < BANDWIDTH_TOL); // equality test

	}

	/**
	 * This method deallocated bandwidth This method should be used only for download bandwidth managers.
	 * 
	 * @param transfer
	 * @throws Exception
	 */
	public void receive(Transfer transfer) throws Exception {
		// can't assert that in fail mode
		// FIXME return the assertion in non-fail mode
		// if(transfer.deliveryEvent.time != scheduler.now)
		// A.ssert(transfer.deliveryEvent.time == scheduler.now);
		if (transfer.failed)
			return;

		// System.out.println("Recv Bytes :");

		sanityCheck();
		transfer.getSourceBandwidthManager().sanityCheck();

		this.availableInBPS += transfer.bandwidthInBPS;
		transfers.remove(transfer);

		transfer.getSourceBandwidthManager().availableInBPS += transfer.bandwidthInBPS;
		transfer.getSourceBandwidthManager().transfers.remove(transfer);

		sanityCheck();
		transfer.getSourceBandwidthManager().sanityCheck();

		transfer.sourceBandwidthManager.boostLocalTransfers();
		transfer.destBandwidthManager.boostLocalTransfers();

		sanityCheck();
		transfer.getSourceBandwidthManager().sanityCheck();

		P.rint("t(" + scheduler.getNow() + ") RECV BYTES " + transfer.getSourceBandwidthManager().getNodeId() + "-->" + transfer.getDestBandwidthManager().getNodeId() + " <" + transfer.getMessage().seqNum + "> / "
				+ transfer.originalStartTime + ", curr_trans = " + transfer.getSourceBandwidthManager().transfers.size() + ", curr_bw=" + availableInBPS + " out of " + totalBandwidthInBPS);
	}

	public int getNodeId() {
		return nodeId;
	}

	public ArrayList<Transfer> getTranfers() {
		return transfers;
	}

	public void fail() throws Exception {
		while (!transfers.isEmpty())
			transfers.get(0).fail();
	}
}
