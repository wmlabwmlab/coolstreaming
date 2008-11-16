/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package simpipe.base;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.IoFilter.WriteRequest;
import org.apache.mina.common.support.AbstractIoFilterChain;

import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.DelayManager;
import se.peertv.peertvsim.network.MessageDeliveryEvent;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.network.conc.ConcurrentDelayManager;
import simpipe.base.support.MinaEvent;
import simpipe.base.support.MinaEventType;
import simpipe.base.support.SimPipeAddress;
import simpipe.base.support.peers.PeerManager;
import simpipe.protocol.SimpPipeMessage;
import simpipe.protocol.SimpPipeUDPMessage;

/**
 * @todo Document me!
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 585081 $, $Date: 2007-10-16 17:45:14 +0900 $
 */
public class SimPipeFilterChain extends AbstractIoFilterChain {

	// private final Queue<Event> eventQueue = new
	// ConcurrentLinkedQueue<Event>();

	private static final long CLOSE_DELAY = 2000;

	private volatile boolean flushEnabled;

	private volatile boolean sessionOpened;

	public SimPipeFilterChain(final IoSession session) {
		super(session);
	}

	// public void start() {
	// flushEnabled = true;
	// flushEvents();
	// flushPendingDataQueues( (SimPipeSessionImpl) getSession() );
	// }

	// protected void pushEvent(final se.peertv.peertvsim.core.Event e) {
	// try {
	// Scheduler.getInstance().enqueue(e);
	// } catch (final Exception e1) {
	// e1.printStackTrace();
	// }
	// // eventQueue.offer(e);
	// // if ( flushEnabled ) {
	// // flushEvents();
	// // }
	// }

	// private void flushEvents() {
	// Event e;
	// while ((e = eventQueue.poll()) != null) {
	// fireEvent(e);
	// }
	// }

	public void fireEvent(final MinaEvent e) {
		final IoSession session = getSession();
		final MinaEventType type = e.getType();
		final Object data = e.getData();

		if (type == MinaEventType.RECEIVED) {
			final SimPipeSessionImpl s = (SimPipeSessionImpl) session;

			if (sessionOpened && s.getTrafficMask().isReadable() && s.getLock().tryLock()) {
				try {
					int byteCount = 1;
					if (data instanceof ByteBuffer) {
						byteCount = ((ByteBuffer) data).remaining();
					}

					s.increaseReadBytes(byteCount);

					super.fireMessageReceived(s, data);
				} finally {
					s.getLock().unlock();
				}

				flushPendingDataQueues(s);
			} else {
				s.pendingDataQueue.add(data);
			}
		} else if (type == MinaEventType.WRITE) {
			super.fireFilterWrite(session, (WriteRequest) data);
		} else if (type == MinaEventType.SENT) {
			super.fireMessageSent(session, (WriteRequest) data);
		} else if (type == MinaEventType.EXCEPTION) {
			super.fireExceptionCaught(session, (Throwable) data);
		} else if (type == MinaEventType.IDLE) {
			super.fireSessionIdle(session, (IdleStatus) data);
		} else if (type == MinaEventType.OPENED) {
			// super.fireSessionOpened(session);
			// sessionOpened = true;
		} else if (type == MinaEventType.CREATED) {
			super.fireSessionCreated(session);
			super.fireSessionOpened(session);
			sessionOpened = true;
		} else if (type == MinaEventType.CLOSED) {
			super.fireSessionClosed(session);
		} else if (type == MinaEventType.CLOSE) {
			super.fireFilterClose(session);
		}
	}

	protected static void flushPendingDataQueues(final SimPipeSessionImpl s) {
		s.updateTrafficMask();
		s.getRemoteSession().updateTrafficMask();
	}

	@Override
	public void fireFilterClose(final IoSession session) {
		final MinaEvent minaEvent = new MinaEvent(MinaEventType.CLOSE, null, ((SimPipeSessionImpl) session).getSessionId());
		// fireEvent(minaEvent);
		// if (PeerManager.getPeer(Reflection.getExecutingGroup()) != null)

		if (PeerManager.getPeer(((SimPipeAddress) session.getRemoteAddress()).getOwnerGroup()) != null && PeerManager.getPeer(((SimPipeAddress) session.getLocalAddress()).getOwnerGroup()) != null) {
			// pushEvent(wrapMinaEvent(session, minaEvent));

			// We send from the remote session to ourselves
			send(((SimPipeSessionImpl) session).getRemoteSession(), minaEvent);

		} else {
			if (PeerManager.getPeer(((SimPipeAddress) session.getLocalAddress()).getOwnerGroup()) != null) {
				fireEvent(minaEvent);
			} else {
				SimPipeIdleStatusChecker.getInstance().remove(session);
			}
		}
	}

	@Override
	public void fireFilterWrite(final IoSession session, final WriteRequest writeRequest) {
		final MinaEvent minaEvent = new MinaEvent(MinaEventType.WRITE, writeRequest, ((SimPipeSessionImpl) session).getSessionId());
		fireEvent(minaEvent);
		// pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireExceptionCaught(final IoSession session, final Throwable cause) {
		final MinaEvent minaEvent = new MinaEvent(MinaEventType.EXCEPTION, cause, ((SimPipeSessionImpl) session).getSessionId());
		fireEvent(minaEvent);
		// pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireMessageSent(final IoSession session, final WriteRequest request) {
		// final Event minaEvent = new Event(EventType.SENT, request, session);
		// fireEvent(minaEvent);
		// pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireSessionClosed(final IoSession session) {
		final MinaEvent minaEvent = new MinaEvent(MinaEventType.CLOSED, null, ((SimPipeSessionImpl) session).getSessionId());
		fireEvent(minaEvent);

		/*
		 * we're not pushing any event because Mina already sends a Close message to the other peer. (It does a Write)
		 */

		// SimPipeDeliveryEvent event = new SimPipeDeliveryEvent(Scheduler.getInstance().now + CLOSE_DELAY, new SimpPipeMessage(minaEvent));
		// pushEvent(wrapMinaEvent(session, minaEvent));
	}

	protected void send(IoSession localSession, Object message) {
		final MinaEvent minaEvent = new MinaEvent(MinaEventType.RECEIVED, message, ((SimPipeSessionImpl) localSession).getRemoteSession().getSessionId());

		send(localSession, minaEvent);
	}

	protected void send(IoSession localSession, MinaEvent minaEvent) {

		final SimpPipeMessage msg = new SimpPipeMessage(minaEvent);

		String senderGroup = ((SimPipeAddress) ((SimPipeSessionImpl) localSession).getLocalAddress()).getOwnerGroup();
		String receiverGroup = ((SimPipeAddress) ((SimPipeSessionImpl) localSession).getRemoteAddress()).getOwnerGroup();

		Node senderNode = PeerManager.getPeer(senderGroup);
		Node receiverNode = PeerManager.getPeer(receiverGroup);

		final int src = senderNode.getId();
		final int dest = receiverNode.getId();

		msg.setDest(dest);
		msg.setSource(src);

		try {
			senderNode.send(dest, msg, 0);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	// protected se.peertv.peertvsim.core.Event wrapMinaEvent(final IoSession session, final MinaEvent e) {
	// final SimpPipeMessage msg = new SimpPipeMessage(e);
	//
	// final int dest = PeerManager.getPeer(((SimPipeAddress) ((SimPipeSessionImpl) session).getLocalAddress()).getOwnerGroup(), true).getId();
	// final int src = PeerManager.getPeer(((SimPipeAddress) ((SimPipeSessionImpl) session).getRemoteAddress()).getOwnerGroup(), true).getId();
	//
	// msg.setDest(dest);
	// msg.setSource(src);
	//
	// long delay = Conf.CONNECTIONDELAY;
	// try {
	//
	// switch (EventLoop.type) {
	// case CONCURRENT:
	// delay = ConcurrentDelayManager.getInstance().getDelay(src, dest);
	// break;
	// case SEQUENTIAL:
	// delay = DelayManager.getInstance().getDelay(src, dest);
	// break;
	// default:
	// break;
	// }
	//
	// } catch (final Exception e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	//
	// // P.rint(Scheduler.getInstance().now+" >>>>>>>>>>>>>>"+msg);
	// return new MessageDeliveryEvent(Scheduler.getInstance().getNow() + delay, msg);
	// }

	@Override
	public void fireSessionCreated(final IoSession session) {
		final MinaEvent minaEvent = new MinaEvent(MinaEventType.CREATED, null, ((SimPipeSessionImpl) session).getSessionId());
		fireEvent(minaEvent);
		// pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireSessionIdle(final IoSession session, final IdleStatus status) {
		final MinaEvent minaEvent = new MinaEvent(MinaEventType.IDLE, status, ((SimPipeSessionImpl) session).getSessionId());
		fireEvent(minaEvent);
		// pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireSessionOpened(final IoSession session) {
		// Event minaEvent =new Event(EventType.OPENED, null, session);
		// pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireMessageReceived(final IoSession session, final Object message) {
		final MinaEvent minaEvent = new MinaEvent(MinaEventType.RECEIVED, message, ((SimPipeSessionImpl) session).getSessionId());
		// pushEvent(wrapMinaEvent(session, minaEvent));

		// wrapMinaEvent(session, minaEvent);

		send(session, minaEvent);
	}

	@Override
	protected void doWrite(final IoSession session, final WriteRequest writeRequest) {
		final SimPipeSessionImpl s = (SimPipeSessionImpl) session;

		String ownerGroup = ((SimPipeAddress) s.getRemoteSession().getLocalAddress()).getOwnerGroup();

		// // Discarding messages of dead/not present yet peers
		// if (PeerManager.getPeer(ownerGroup) == null && ownerGroup != null) {
		// // s.getRemoteSession().getFilterChain().fireSessionClosed(session);
		// // ((SimPipeAddress) s.getRemoteSession().getLocalAddress()).setOwnerGroup(null);
		// return;
		// }

		if (s.isConnected() && PeerManager.getPeer(ownerGroup) != null && ownerGroup != null) {
			// if ( s.getTrafficMask().isWritable() && s.getLock().tryLock()) {
			// try {

			final Object message = writeRequest.getMessage();

			int byteCount = 1;
			Object messageCopy = message;
			if (message instanceof ByteBuffer) {
				final ByteBuffer rb = (ByteBuffer) message;
				rb.mark();
				byteCount = rb.remaining();
				final ByteBuffer wb = ByteBuffer.allocate(rb.remaining());
				wb.put(rb);
				wb.flip();
				rb.reset();
				messageCopy = wb;
			}
			
			// Avoid unwanted side effect that scheduledWrite* becomes negative
			// by increasing them.
			s.increaseScheduledWriteBytes(byteCount);
			s.increaseScheduledWriteRequests();

			s.increaseWrittenBytes(byteCount);
			s.increaseWrittenMessages();

			//
			//
			// HERE the communication happens
			//
			//

			// s.getRemoteSession().getFilterChain().fireMessageReceived(s.getRemoteSession(), messageCopy);
			send(session, messageCopy);

			s.getFilterChain().fireMessageSent(s, writeRequest);
			// } finally {
			// s.getLock().unlock();
			// }

			flushPendingDataQueues(s);
			// } else {
			// s.pendingDataQueue.add(writeRequest);
			// }
		} else {
			writeRequest.getFuture().setWritten(false);
		}
	}

	// @Override
	// protected void doWrite(IoSession session, WriteRequest writeRequest) {
	// SimPipeSessionImpl s = (SimPipeSessionImpl) session;
	// if (s.isConnected()) {
	// if ( s.getTrafficMask().isWritable() && s.getLock().tryLock()) {
	// try {
	// Object message = writeRequest.getMessage();

	// int byteCount = 1;
	// Object messageCopy = message;
	// if (message instanceof ByteBuffer) {
	// ByteBuffer rb = (ByteBuffer) message;
	// rb.mark();
	// byteCount = rb.remaining();
	// ByteBuffer wb = ByteBuffer.allocate(rb.remaining());
	// wb.put(rb);
	// wb.flip();
	// rb.reset();
	// messageCopy = wb;
	// }

	// // Avoid unwanted side effect that scheduledWrite* becomes negative
	// // by increasing them.
	// s.increaseScheduledWriteBytes(byteCount);
	// s.increaseScheduledWriteRequests();

	// s.increaseWrittenBytes(byteCount);
	// s.increaseWrittenMessages();

	// // HERE the communication happens

	// s.getRemoteSession().getFilterChain().fireMessageReceived(
	// s.getRemoteSession(), messageCopy);
	// s.getFilterChain().fireMessageSent(s, writeRequest);
	// } finally {
	// s.getLock().unlock();
	// }

	// flushPendingDataQueues( s );
	// } else {
	// s.pendingDataQueue.add(writeRequest);
	// }
	// } else {
	// writeRequest.getFuture().setWritten(false);
	// }
	// }

	@Override
	protected void doClose(final IoSession session) {
		final SimPipeSessionImpl s = (SimPipeSessionImpl) session;

		try {
			s.getLock().lock();

			if (!session.getCloseFuture().isClosed()) {
				s.getServiceListeners().fireSessionDestroyed(s);
				s.getRemoteSession().close();
			}
		} finally {
			s.getLock().unlock();
		}
	}

}
