/**
 * =============================================== 
 *  File     : $$Id: Node.java,v 1.9 2007/06/06 09:22:48 sameh Exp $$
 *  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
 *  Version  :$$Revision: 1.9 $$
 *  Tag	  : $$Name:  $$
 *  Last edited by   : $$Author: sameh $$
 *  Last updated:    $$Date: 2007/06/06 09:22:48 $$
 *===============================================
 */

package se.peertv.peertvsim.network;

import java.lang.reflect.Method;
import java.util.Random;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.Callbackable;
import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.NodeIdInt;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.conc.ConcurrentDelayManager;
import se.peertv.peertvsim.network.tcp.bw.BandwidthManager;
import se.peertv.peertvsim.network.tcp.bw.Transfer;
import se.peertv.peertvsim.utils.P;

public class Node /* extends Callbackable */implements NodeIdInt {

	protected final int id;
	protected final String group;

	final static Scheduler scheduler = Scheduler.getInstance();

	private Method handler;

	public Node(int id) {

		this.id = id;
		group = Reflection.getExecutingGroup();
	}

	/**
	 * Send a a message. ..... unreliable, any packet loss congeston etc.. should be modeled here
	 * 
	 * @param nodeId
	 * @param Message
	 */
	public void send(int nodeId, Message message, double percentageVariance) throws Exception {

		long delay = DelayManager.getInstance().getDelay(id, nodeId);

		long variance = (long) (delay * (percentageVariance));

		long scheduleTime = (long) (scheduler.getNow() + delay + variance);

		// System.out.println("D: " +delay+ " PVAR: "+ percentageVariance+" VAR: "+variance +" T: " + scheduleTime);

		MessageDeliveryEvent event = new MessageDeliveryEvent(scheduleTime, message);

		scheduler.enqueue(event);
		// P.rint("t("+scheduler.now+") SEND "+message.toString());
		// P.rint("t["+message.getSource()+"->"+message.getDest()+"]("+Scheduler.
		// getInstance().now+") SEND "+message.toString());
	}

	public void receive(Message msg) throws Throwable {
		// P.rint("t["+msg.getSource()+"->"+msg.getDest()+"]("+Scheduler.
		// getInstance().now+") RECV "+msg.toString());

		try {

			Method handlerLocal = handler;

			if (handler == null) {
				Class<?>[] argTypes = { msg.getClass() };
				String handlerName = "handle";// +msg.getClass().getSimpleName();
				handlerLocal = this.getClass().getMethod(handlerName, argTypes);
			}

			Object[] args = { msg };

			handlerLocal.invoke(this, args);
		} catch (java.lang.reflect.InvocationTargetException e) {
			throw e.getCause();
		}
	}

	public void registerHandler(String handlerName, Class<?> handlerClassArgument) {

		Class<?>[] argTypes = { handlerClassArgument };
		try {
			handler = this.getClass().getMethod(handlerName, argTypes);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

	}

	// ================= Getter & setters =============================
	public int getId() {
		return id;
	}

	public String getGroup() {
		return group;
	}

}
