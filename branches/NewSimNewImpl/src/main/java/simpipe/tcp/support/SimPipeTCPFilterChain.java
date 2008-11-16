package simpipe.tcp.support;

import java.nio.ByteBuffer;
import java.util.Random;

import org.apache.mina.common.IoSession;
import org.apache.mina.common.IoFilter.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.DelayManager;
import se.peertv.peertvsim.network.MessageDeliveryEvent;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.network.conc.ConcurrentDelayManager;
import se.peertv.peertvsim.network.udp.UDPNode;
import simpipe.base.SimPipeFilterChain;
import simpipe.base.SimPipeIdleStatusChecker;
import simpipe.base.SimPipeSessionImpl;
import simpipe.base.support.MinaEvent;
import simpipe.base.support.MinaEventType;
import simpipe.base.support.SimPipeAddress;
import simpipe.base.support.peers.PeerManager;
import simpipe.protocol.SimpPipeMessage;
import simpipe.protocol.SimpPipeUDPMessage;

public class SimPipeTCPFilterChain extends SimPipeFilterChain {
	private static final Logger log = LoggerFactory.getLogger(SimPipeTCPFilterChain.class);

	public SimPipeTCPFilterChain(IoSession session) {
		super(session);
	}

	/*
	 * For Meemo: Never use random without a seed that we can control
	 */
	Random rand = new Random(SimulableSystem.currentTimeMillis());

	@Override
	public void fireFilterClose(final IoSession session) {

		PeerManager.setHandlingTCP(true);

		super.fireFilterClose(session);
	}

	@Override
	public void doWrite(final IoSession session, final WriteRequest writeRequest) {

		PeerManager.setHandlingTCP(true);

		super.doWrite(session, writeRequest);

	}

	protected void send(IoSession session, MinaEvent e) {

		PeerManager.setHandlingTCP(true);

		super.send(session, e);
	}

	// @Override
	// protected se.peertv.peertvsim.core.Event wrapMinaEvent(IoSession session, MinaEvent e) {
	// final SimpPipeMessage msg = new SimpPipeMessage(e);
	//
	// // // Modify to true when finished
	// // final int dest = PeerManager.getPeer(((SimPipeAddress) ((SimPipeSessionImpl) session).getLocalAddress()).getOwnerGroup(), false).getId();
	// // final int src = PeerManager.getPeer(((SimPipeAddress) ((SimPipeSessionImpl) session).getRemoteAddress()).getOwnerGroup(), false).getId();
	// //
	// // msg.setDest(dest);
	// // msg.setSource(src);
	// //
	// // long delay = Conf.CONNECTIONDELAY;
	// // try {
	// // switch (EventLoop.type) {
	// // case CONCURRENT:
	// // delay = ConcurrentDelayManager.getInstance().getDelay(src, dest);
	// // break;
	// // case SEQUENTIAL:
	// // delay = DelayManager.getInstance().getDelay(src, dest);
	// // break;
	// // default:
	// // break;
	// // }
	// //
	// // delay += delay * (rand.nextFloat() * Conf.UDP_REORDERING_FACTOR);
	// // } catch (final Exception e1) {
	// // // TODO Auto-generated catch block
	// // e1.printStackTrace();
	// // }
	//
	// // System.out.println("UDP");
	// // return new MessageDeliveryEvent(Scheduler.getInstance().getNow() + delay, msg);
	// }

	@Override
	public void fireMessageReceived(final IoSession session, final Object message) {
		// final MinaEvent minaEvent = new simpipe.base.support.MinaEvent(MinaEventType.RECEIVED, message, ((SimPipeSessionImpl)
		// session).getSessionId());
		// ((SimpPipeUDPMessage) message).setMinaEvent(minaEvent);
		// Object bb = ((SimpPipeUDPMessage) message).getPayload();
		//
		// // if (Conf.UDP_FAILURE_PERCENT > 0 && rand.nextFloat() < (Conf.UDP_FAILURE_PERCENT / 100.0f))
		// // return; // don't push the msg
		// java.nio.ByteBuffer byteBufferToSend = null;
		// long size = -1;
		// // if (bb instanceof org.apache.mina.common.ByteBuffer) {
		// // byteBufferToSend = java.nio.ByteBuffer.allocate(((org.apache.mina.common.ByteBuffer) bb).capacity());
		// // // TODO: we should copy the contents here if we want to...
		// // size = ((org.apache.mina.common.ByteBuffer) bb).capacity();
		// // } else if (bb instanceof java.nio.ByteBuffer) {
		// // byteBufferToSend = (java.nio.ByteBuffer) bb;
		// // size = ((java.nio.ByteBuffer) bb).capacity();
		// // }
		// // ((SimpPipeUDPMessage) message).setBb((ByteBuffer)bb);
		//
		// // if (Conf.USE_BANDWIDTH_MODEL && bb != null && size > -1) {
		// // try {
		// // PeerManager.getPeer(((SimPipeAddress) session.getRemoteAddress()).getOwnerGroup()).sendBytes(PeerManager.getPeer(((SimPipeAddress)
		// // session.getLocalAddress()).getOwnerGroup()).getId(),
		// // (SimpPipeUDPMessage) message, size);
		// // } catch (Exception e) {
		// // e.printStackTrace();
		// // }
		// // } else {
		// // print Warning: sending non-byteBuffers over UDP connection
		// // log.warn("Sending non-ByteBuffers over UDP connection. Not using bandwidth simulation");
		// // System.out.println("Sending non-ByteBuffers over UDP connection. Not using bandwidth simulation");
		// // pushEvent(wrapMinaEvent(session, minaEvent));
		// // }
		//
		// send(session, minaEvent);

		// FOR NOW, NO TCP Bandwidth modeling
		super.fireMessageReceived(session, message);

		// We should instead intercept the call and pass it to the TCPNode

		// Object bb = ((SimpPipeUDPMessage) message).getPayload();

	}
}
