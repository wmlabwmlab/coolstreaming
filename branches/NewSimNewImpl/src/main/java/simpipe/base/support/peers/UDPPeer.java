package simpipe.base.support.peers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.udp.UDPNode;
import simpipe.base.SimPipeFilterChain;
import simpipe.base.SimPipeSessionImpl;
import simpipe.multiplexor.SimPipeMultiplexorInt;
import simpipe.protocol.SimpPipeMessage;
import simpipe.protocol.SimpPipeUDPMessage;

public class UDPPeer extends UDPNode {

	private static final Logger log = LoggerFactory.getLogger(UDPPeer.class);

	// BaseIoService minaPeer;
	private SimPipeMultiplexorInt multiplexor;

	public UDPPeer(int id) {
		super(id);
		
		super.registerHandler("handle", SimpPipeUDPMessage.class);
	}

	public UDPPeer(int id, SimPipeMultiplexorInt multiplexorInt) {
		super(id);
		multiplexor = multiplexorInt;

		super.registerHandler("handle", SimpPipeUDPMessage.class);
	}

	public UDPPeer(int id, long downBandwidth, long downWindowSize, long upBandwidth, long upWindowSize) {
		super(id, downBandwidth, downWindowSize, upBandwidth, upWindowSize);

		super.registerHandler("handle", SimpPipeUDPMessage.class);
	}

	public UDPPeer(int id, long downBandwidth, long downWindowSize, long upBandwidth, long upWindowSize, SimPipeMultiplexorInt multiplexorInt) {
		super(id, downBandwidth, downWindowSize, upBandwidth, upWindowSize);
		multiplexor = multiplexorInt;

		super.registerHandler("handle", SimpPipeUDPMessage.class);
	}

	private void handle(SimpPipeMessage msg) {
		if (SimulableSystem.isSimPipeTraceEnabled())
			log.info("T(" + Scheduler.getInstance().getNow() + ")," + Reflection.getExecutingGroup() + "+++++++++++ STARTED ++++++++++++ "
					+ (msg.getCarriedData() == null ? "empty" : msg.getCarriedData().getClass().getSimpleName()) + " message");

		String sessionId = msg.getMinaEvent().getTargetSessionId();

		SimPipeFilterChain chain = (SimPipeFilterChain) SimPipeSessionImpl.sessionsMap.get(sessionId).getFilterChain();

		chain.fireEvent(msg.getMinaEvent());

		if (SimulableSystem.isSimPipeTraceEnabled())
			log.info("-------------------------------- ENDED -------------------");
		// chain = minaPeer.getFilterChain();
	}

	// public void handle(SimPipeKillEvent msg) {
	// // if (SimulableSystem.isSimPipeTraceEnabled())
	// // log.info("T(" + Scheduler.getInstance().now + ")," + Reflection.getExecutingGroup() + "+++++++++++ STARTED ++++++++++++ "
	// // + (msg.getCarriedData() == null ? "empty" : msg.getCarriedData().getClass().getSimpleName()) + " message");
	//
	// // SimPipeFilterChain chain = (SimPipeFilterChain) msg.getMinaEvent().getTargetSession().getFilterChain();
	// // chain.fireEvent(msg.getMinaEvent());
	//
	//		
	// if (SimulableSystem.isSimPipeTraceEnabled())
	// log.info("-------------------------------- ENDED -------------------");
	// // chain = minaPeer.getFilterChain();
	// }

	public void handle(SimpPipeUDPMessage msg) {
		try {
			if (multiplexor != null)
				multiplexor.messageReceived(msg);
			else
				handle((SimpPipeMessage) msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Peer" + id;
	}
}
