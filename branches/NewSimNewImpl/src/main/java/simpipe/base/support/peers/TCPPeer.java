package simpipe.base.support.peers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.network.tcp.TCPNode;
import simpipe.base.SimPipeFilterChain;
import simpipe.base.SimPipeSessionImpl;
import simpipe.multiplexor.SimPipeMultiplexorInt;
import simpipe.protocol.SimpPipeMessage;
import simpipe.protocol.SimpPipeUDPMessage;

public class TCPPeer extends TCPNode /* Should extends TCP node */{

	private static final Logger log = LoggerFactory.getLogger(TCPPeer.class);

	// BaseIoService minaPeer;
	private SimPipeMultiplexorInt multiplexor;

	public TCPPeer(int id, long downCapacity, long upCapacity) {
		super(id, upCapacity, downCapacity);

		super.registerHandler("handle", SimpPipeMessage.class);
	}

	public TCPPeer(int id, long downCapacity, long upCapacity, SimPipeMultiplexorInt multiplexorInt) {
		super(id, upCapacity, downCapacity);
		multiplexor = multiplexorInt;

		super.registerHandler("handle", SimpPipeMessage.class);
	}

	public void handle(SimpPipeMessage msg) {
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

	@Override
	public String toString() {
		return "Peer" + id;
	}
}
