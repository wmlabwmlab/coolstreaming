package se.peertv.peertvsim.network.udp;

import java.lang.reflect.Method;
import java.util.Random;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.DelayManager;
import se.peertv.peertvsim.network.Message;
import se.peertv.peertvsim.network.MessageDeliveryEvent;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.network.conc.ConcurrentDelayManager;
import se.peertv.peertvsim.network.udp.bw.BandwidthManager;
import se.peertv.peertvsim.network.udp.bw.BandwidthModelVaryingSize;

public class UDPNode extends Node {

	protected final String group;
	protected long upCapacity; // in bytes per sec
	protected long downCapacity; // in in bytes per sec

	private BandwidthModelVaryingSize upBandwidthManager;
	private BandwidthModelVaryingSize downBandwidthManager;

	private Random rand = new Random(SimulableSystem.currentTimeMillis());

	final static Scheduler scheduler = Scheduler.getInstance();

	/**
	 * Use this constructor in case it's not necessary to model bandwidth
	 * 
	 */
	public UDPNode(int id) {
		super(id);
		group = Reflection.getExecutingGroup();
	}

	/**
	 * Use this constructor in case it's necessary to model bandwidth
	 * 
	 */

	public UDPNode(int id, long downBandwidth, long downWindowSize, long upBandwidth, long upWindowSize) {
		super(id);

		group = Reflection.getExecutingGroup();
		this.upCapacity = upBandwidth;
		this.downCapacity = downBandwidth;
		this.downBandwidthManager = BandwidthManager.getCurrentBWModelInstance(downBandwidth, downWindowSize, false);
		this.upBandwidthManager = BandwidthManager.getCurrentBWModelInstance(upBandwidth, upWindowSize, false);

	}

	/**
	 * Send a a message. ..... unreliable, any packet loss congestion etc.. should be modeled here
	 * 
	 * @param nodeId
	 * @param Message
	 */
	public void send(int nodeId, Message message) throws Exception {

		/*
		 * Here we model traffic shaping for upload bandwidth
		 */

		boolean send = true;

		if (Conf.USE_BANDWIDTH_MODEL && !upBandwidthManager.isToSend(SimulableSystem.currentTimeMillis(), message.getSize())) {

			/*
			 * Drop
			 */
			send = false;

			// System.out.println(Reflection.getExecutingGroup()+":SND: Drop for BW, msg size= "+message.getSize()+" BW="+upBandwidthManager.
			// getBWCapacity()+ " L:"+upBandwidthManager.getCurrentWindowLoad());

		}

		/*
		 * Here we model reordering
		 */
		
		double reorderingPercentile = (rand.nextFloat() * (Conf.UDP_REORDERING_PERCENTILE ) / 100);

		if (send) {
			super.send(nodeId, message, reorderingPercentile);
		}

	}

	public void receive(Message msg) throws Throwable {
		// P.rint("t["+msg.getSource()+"->"+msg.getDest()+"]("+Scheduler.
		// getInstance().now+") RECV "+msg.toString());

		/*
		 * Here we model dropping
		 */
		if (Conf.UDP_FAILURE_PERCENT > 0.0f && rand.nextFloat() < (Conf.UDP_FAILURE_PERCENT / 100.0f))
			return; // Drop due to udp failure

		if (Conf.USE_BANDWIDTH_MODEL && !downBandwidthManager.isToSend(SimulableSystem.currentTimeMillis(), msg.getSize())) {
			// System.out.println("RCV: Drop for BW, msg size= " + msg.getSize() + " BW=" + upBandwidthManager.getBWCapacity());
			return; // Drop due to bandwidth limitation
		}

		super.receive(msg);

	}

	// ================= Getter & setters =============================
	public int getId() {
		return id;
	}

	public long getDownCapacity() {
		return downCapacity;
	}

	public long getUpCapacity() {
		return upCapacity;
	}

	public String getGroup() {
		return group;
	}

}
