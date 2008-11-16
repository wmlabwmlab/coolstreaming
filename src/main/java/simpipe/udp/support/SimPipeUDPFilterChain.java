package simpipe.udp.support;

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

public class SimPipeUDPFilterChain extends SimPipeFilterChain {
	private static final Logger log = LoggerFactory.getLogger(SimPipeUDPFilterChain.class);

	public SimPipeUDPFilterChain(IoSession session) {
		super(session);
	}

	/*
	 * For Meemo: Never use random without a seed that we can control
	 */
	Random rand = new Random(SimulableSystem.currentTimeMillis());

	@Override
	public void fireFilterClose(final IoSession session) {

		PeerManager.setHandlingTCP(false);

		super.fireFilterClose(session);
	}

	@Override
	public void doWrite(final IoSession session, final WriteRequest writeRequest) {

		PeerManager.setHandlingTCP(false);

		final Object message = writeRequest.getMessage();

		// long byteCount = 1;

		// if (message instanceof SimpPipeUDPMessage && ((SimpPipeUDPMessage)message).getSize()==0) {
		// SimpPipeUDPMessage UDPmessage=(SimpPipeUDPMessage)message;
		//			
		// byte[] payload=(byte[])UDPmessage.getPayload();
		//			
		// if(payload!=null){
		// byteCount=payload.length;
		// }
		//			
		// ((SimpPipeUDPMessage) message).setSize(byteCount);
		//
		// }

		super.doWrite(session, writeRequest);

	}

	// @Override
	// protected void send(IoSession session, MinaEvent e) {
	//
	// PeerManager.setHandlingTCP(false);
	//
	// final SimpPipeUDPMessage msg = new SimpPipeUDPMessage(e, e.getData());
	//
	// String receiverGroup = ((SimPipeAddress) ((SimPipeSessionImpl) session).getLocalAddress()).getOwnerGroup();
	// String senderGroup = ((SimPipeAddress) ((SimPipeSessionImpl) session).getRemoteAddress()).getOwnerGroup();
	//
	// UDPNode senderNode = (UDPNode) PeerManager.getPeer(senderGroup);
	// Node receiverNode = PeerManager.getPeer(receiverGroup);
	//
	// final int src = senderNode.getId();
	// final int dest = receiverNode.getId();
	//
	// msg.setDest(dest);
	// msg.setSource(src);
	//
	// try {
	// senderNode.send(dest, msg);
	// } catch (Exception e1) {
	// e1.printStackTrace();
	// }
	//
	// }

	@Override
	protected void send(IoSession localSession, MinaEvent minaEvent) {

		PeerManager.setHandlingTCP(false);

		final SimpPipeUDPMessage msg = new SimpPipeUDPMessage(minaEvent, null);

		if (minaEvent.getData() instanceof SimpPipeUDPMessage) {
			int byteCount = (int) ((SimpPipeUDPMessage) minaEvent.getData()).getSize();
			msg.setSize(byteCount);
		}

		String senderGroup = ((SimPipeAddress) ((SimPipeSessionImpl) localSession).getLocalAddress()).getOwnerGroup();
		String receiverGroup = ((SimPipeAddress) ((SimPipeSessionImpl) localSession).getRemoteAddress()).getOwnerGroup();

		UDPNode senderNode = (UDPNode) PeerManager.getPeer(senderGroup);
		Node receiverNode = PeerManager.getPeer(receiverGroup);

		final int src = senderNode.getId();
		final int dest = receiverNode.getId();

		msg.setDest(dest);
		msg.setSource(src);

		try {
			senderNode.send(dest, msg);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

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

	// @Override
	// public void fireMessageReceived(final IoSession session, final Object message) {
	// final MinaEvent minaEvent = new simpipe.base.support.MinaEvent(MinaEventType.RECEIVED, message, ((SimPipeSessionImpl) session).getSessionId());
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
	// }
}
