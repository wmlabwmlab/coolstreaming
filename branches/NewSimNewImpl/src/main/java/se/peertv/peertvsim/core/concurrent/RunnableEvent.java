package se.peertv.peertvsim.core.concurrent;

import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.network.MessageDeliveryEvent;
import se.peertv.peertvsim.network.Network;
import se.peertv.peertvsim.utils.A;

class RunnableEvent implements Runnable {
	private Event e;
	private ConcurrentEventLoop loop;
	private String threadGroup;

	public RunnableEvent(Event e, String threadGroup,
			ConcurrentEventLoop improvedConcurrentEventLoop) {
		super();
		this.e = e;
		this.threadGroup = threadGroup;
		this.loop = improvedConcurrentEventLoop;
	}

	@Override
	public void run() {

		if (e instanceof MessageDeliveryEvent) {
			MessageDeliveryEvent messageEvent = (MessageDeliveryEvent) e;
			int id = messageEvent.getDestNodeId();

			Network net = Network.getInstance();

			if (!net.isNodeInNetwork(id)) {

				/*
				 * Checks if the destination node was removed before, otherwise
				 * there's a bug
				 */
//				boolean isRemoved = Network.getInstance().isRemovedNode(id);
				// try {
				// A.ssert(isRemoved);
				// } catch (Exception e1) {
				// e1.printStackTrace();
				// }
			}

		}

		executeEvent(e);

	}

	private void executeEvent(Event e) {

		loop.preEventExecution();

		try {
			e.handle();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

		loop.postEventExecution();

		loop.eventTerminated(threadGroup, e.getTime());
	}

}
