package se.peertv.peertvsim.core.concurrent;

import java.util.Comparator;

import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.network.MessageDeliveryEvent;
import se.peertv.peertvsim.network.Network;
import se.peertv.peertvsim.network.Node;

public class ConcurrentEventComparator implements Comparator<Event> {

	private int count = 0;

	@Override
	public int compare(Event o1, Event o2) {

		String grp1 = o1.getThreadGroup();
		String grp2 = o2.getThreadGroup();

		if (!grp1.equals(grp2)) {
			return 1;
		} else {

			if (o1 instanceof MessageDeliveryEvent && o2 instanceof MessageDeliveryEvent) {

				MessageDeliveryEvent msg1 = (MessageDeliveryEvent) o1;
				MessageDeliveryEvent msg2 = (MessageDeliveryEvent) o2;

				int id1 = msg1.getSourceNodeId();
				int id2 = msg2.getSourceNodeId();

				if (id1 != id2) {
					return id1 - id2;
				}
			}

			return o1.getMyID() - o2.getMyID();
		}
	}

}
