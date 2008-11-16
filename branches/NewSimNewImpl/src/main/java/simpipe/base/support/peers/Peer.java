package simpipe.base.support.peers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.Node;
import simpipe.multiplexor.SimPipeMultiplexorInt;
import simpipe.protocol.SimPipeKillEvent;
import simpipe.protocol.SimpPipeMessage;
import simpipe.protocol.SimpPipeUDPMessage;

public abstract class Peer extends Node {

	public Peer(int id) {
		super(id);
	}

	// private static final Logger log = LoggerFactory.getLogger(Peer.class);
	//
	// // BaseIoService minaPeer;
	// private SimPipeMultiplexorInt multiplexor;
	//
	// public Peer(int id, long downCapacity, long upCapacity) {
	// super(id, downCapacity, upCapacity);
	// }
	//
	// public Peer(int id, long downCapacity, long upCapacity, SimPipeMultiplexorInt multiplexorInt) {
	// super(id, downCapacity, upCapacity);
	// multiplexor = multiplexorInt;
	// }
	//
	// // public BaseIoService getMinaPeer() {
	// // return minaPeer;
	// // }
	// //
	// // public void setMinaPeer(BaseIoService minaPeer) {
	// // this.minaPeer = minaPeer;
	// // }
	//
	// public void handle(SimpPipeMessage msg) {
	// if (SimulableSystem.isSimPipeTraceEnabled())
	// log.info("T(" + Scheduler.getInstance().getNow() + ")," + Reflection.getExecutingGroup() + "+++++++++++ STARTED ++++++++++++ "
	// + (msg.getCarriedData() == null ? "empty" : msg.getCarriedData().getClass().getSimpleName()) + " message");
	//
	// String sessionId=msg.getMinaEvent().getTargetSessionId();
	//		
	// SimPipeFilterChain chain = (SimPipeFilterChain) SimPipeSessionImpl.sessionsMap.get(sessionId).getFilterChain();
	//		
	// chain.fireEvent(msg.getMinaEvent());
	//
	// if (SimulableSystem.isSimPipeTraceEnabled())
	// log.info("-------------------------------- ENDED -------------------");
	// // chain = minaPeer.getFilterChain();
	// }
	//
	// // public void handle(SimPipeKillEvent msg) {
	// // // if (SimulableSystem.isSimPipeTraceEnabled())
	// // // log.info("T(" + Scheduler.getInstance().now + ")," + Reflection.getExecutingGroup() + "+++++++++++ STARTED ++++++++++++ "
	// // // + (msg.getCarriedData() == null ? "empty" : msg.getCarriedData().getClass().getSimpleName()) + " message");
	// //
	// // // SimPipeFilterChain chain = (SimPipeFilterChain) msg.getMinaEvent().getTargetSession().getFilterChain();
	// // // chain.fireEvent(msg.getMinaEvent());
	// //
	// //
	// // if (SimulableSystem.isSimPipeTraceEnabled())
	// // log.info("-------------------------------- ENDED -------------------");
	// // // chain = minaPeer.getFilterChain();
	// // }
	//
	// public void handle(SimpPipeUDPMessage msg) {
	// try {
	// if (multiplexor != null)
	// multiplexor.messageReceived(msg);
	// else
	// handle((SimpPipeMessage) msg);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// @Override
	// public String toString() {
	// return "Peer" + id;
	// }
}
