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
package simpipe.support;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.IoFilter.WriteRequest;
import org.apache.mina.common.support.AbstractIoFilterChain;

import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.DelayManager;
import se.peertv.peertvsim.network.Message;
import se.peertv.peertvsim.network.MessageDeliveryEvent;
import simpipe.SimPipeAddress;
import simpipe.protocol.SimPipeDeliveryEvent;
import simpipe.protocol.SimpPipeMessage;

/**
 * @todo Document me!
 *
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 585081 $, $Date: 2007-10-16 17:45:14 +0900 (화, 16 10월 2007) $
 */
public class SimPipeFilterChain extends AbstractIoFilterChain {

	//private final Queue<Event> eventQueue = new ConcurrentLinkedQueue<Event>();

	private volatile boolean flushEnabled;
	private volatile boolean sessionOpened;

	public SimPipeFilterChain(IoSession session) {
		super(session);
	}

//	public void start() {
//	flushEnabled = true;
//	flushEvents();
//	flushPendingDataQueues( (SimPipeSessionImpl) getSession() );
//	}

	private void pushEvent(se.peertv.peertvsim.core.Event e) {
		try {
			Scheduler.getInstance().enqueue(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
//		eventQueue.offer(e);
//		if ( flushEnabled ) {
//		flushEvents();
//		}
	}

//	private void flushEvents() {
//	Event e;
//	while ((e = eventQueue.poll()) != null) {
//	fireEvent(e);
//	}
//	}

	void fireEvent(Event e) {
		IoSession session = getSession();
		EventType type = e.getType();
		Object data = e.getData();

		if (type == EventType.RECEIVED) {
			SimPipeSessionImpl s = (SimPipeSessionImpl) session;

			if( sessionOpened && s.getTrafficMask().isReadable() && s.getLock().tryLock()) {
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

				flushPendingDataQueues( s );
			} else {
				s.pendingDataQueue.add(data);
			}
		} else if (type == EventType.WRITE) {
			super.fireFilterWrite(session, (WriteRequest) data);
		} else if (type == EventType.SENT) {
			super.fireMessageSent(session, (WriteRequest) data);
		} else if (type == EventType.EXCEPTION) {
			super.fireExceptionCaught(session, (Throwable) data);
		} else if (type == EventType.IDLE) {
			super.fireSessionIdle(session, (IdleStatus) data);
		} else if (type == EventType.OPENED) {
//			super.fireSessionOpened(session);
//			sessionOpened = true;
		} else if (type == EventType.CREATED) {
			super.fireSessionCreated(session);
			super.fireSessionOpened(session);
			sessionOpened = true;
		} else if (type == EventType.CLOSED) {
			super.fireSessionClosed(session);
		} else if (type == EventType.CLOSE) {
			super.fireFilterClose(session);
		}
	}

	private static void flushPendingDataQueues( SimPipeSessionImpl s ) {
		s.updateTrafficMask();
		s.getRemoteSession().updateTrafficMask();
	}

	@Override
	public void fireFilterClose(IoSession session) {
		Event minaEvent =new Event(EventType.CLOSE, null, session);
		pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireFilterWrite(IoSession session, WriteRequest writeRequest) {
		Event minaEvent =new Event(EventType.WRITE, writeRequest, session);
		pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireExceptionCaught(IoSession session, Throwable cause) {
		Event minaEvent =new Event(EventType.EXCEPTION, cause, session);
		pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireMessageSent(IoSession session, WriteRequest request) {
		Event minaEvent =new Event(EventType.SENT, request, session);
		pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireSessionClosed(IoSession session) {
		Event minaEvent =new Event(EventType.CLOSED, null, session);
		pushEvent(wrapMinaEvent(session, minaEvent));
	}

	
	private se.peertv.peertvsim.core.Event wrapMinaEvent(IoSession session, Event e){
		SimpPipeMessage msg = new SimpPipeMessage(e);	
		int dest =((SimPipeAddress)((SimPipeSessionImpl)session).getLocalAddress()).getPort();
		int src =((SimPipeAddress)((SimPipeSessionImpl)session).getRemoteAddress()).getPort();
		
		msg.setDest(dest);
		msg.setSource(src);
		
		long delay = Conf.CONNECTIONDELAY; 
		
		try {
			delay = DelayManager.getInstance().getDelay(src, dest);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		return new SimPipeDeliveryEvent(Scheduler.getInstance().now+delay,msg);		
	}
	
	@Override
	public void fireSessionCreated(IoSession session) {
		Event minaEvent = new Event(EventType.CREATED, null, session);
		pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireSessionIdle(IoSession session, IdleStatus status) {
		Event minaEvent = new Event(EventType.IDLE, status, session);
		pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireSessionOpened(IoSession session) {
//		Event minaEvent =new Event(EventType.OPENED, null, session);
//		pushEvent(wrapMinaEvent(session, minaEvent));
	}

	@Override
	public void fireMessageReceived(IoSession session, Object message) {
		Event minaEvent =new Event(EventType.RECEIVED, message, session);
		pushEvent(wrapMinaEvent(session, minaEvent));
	}


	@Override
	protected void doWrite(IoSession session, WriteRequest writeRequest) {
		SimPipeSessionImpl s = (SimPipeSessionImpl) session;
		if (s.isConnected()) {
//			if ( s.getTrafficMask().isWritable() && s.getLock().tryLock()) {
//			try {
			Object message = writeRequest.getMessage();

			int byteCount = 1;
			Object messageCopy = message;
			if (message instanceof ByteBuffer) {
				ByteBuffer rb = (ByteBuffer) message;
				rb.mark();
				byteCount = rb.remaining();
				ByteBuffer wb = ByteBuffer.allocate(rb.remaining());
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
			s.getRemoteSession().getFilterChain().fireMessageReceived(
					s.getRemoteSession(), messageCopy);
			s.getFilterChain().fireMessageSent(s, writeRequest);
//			} finally {
//			s.getLock().unlock();
//			}

			flushPendingDataQueues( s );
//			} else {
//			s.pendingDataQueue.add(writeRequest);                
//			}
		} else {
			writeRequest.getFuture().setWritten(false);
		}
	}


//	@Override
//	protected void doWrite(IoSession session, WriteRequest writeRequest) {
//	SimPipeSessionImpl s = (SimPipeSessionImpl) session;
//	if (s.isConnected()) {
//	if ( s.getTrafficMask().isWritable() && s.getLock().tryLock()) {
//	try {
//	Object message = writeRequest.getMessage();

//	int byteCount = 1;
//	Object messageCopy = message;
//	if (message instanceof ByteBuffer) {
//	ByteBuffer rb = (ByteBuffer) message;
//	rb.mark();
//	byteCount = rb.remaining();
//	ByteBuffer wb = ByteBuffer.allocate(rb.remaining());
//	wb.put(rb);
//	wb.flip();
//	rb.reset();
//	messageCopy = wb;
//	}

////	Avoid unwanted side effect that scheduledWrite* becomes negative
////	by increasing them.
//	s.increaseScheduledWriteBytes(byteCount);
//	s.increaseScheduledWriteRequests();

//	s.increaseWrittenBytes(byteCount);
//	s.increaseWrittenMessages();



////	HERE the communication happens


//	s.getRemoteSession().getFilterChain().fireMessageReceived(
//	s.getRemoteSession(), messageCopy);
//	s.getFilterChain().fireMessageSent(s, writeRequest);
//	} finally {
//	s.getLock().unlock();
//	}

//	flushPendingDataQueues( s );
//	} else {
//	s.pendingDataQueue.add(writeRequest);                
//	}
//	} else {
//	writeRequest.getFuture().setWritten(false);
//	}
//	}

	@Override
	protected void doClose(IoSession session) {
		SimPipeSessionImpl s = (SimPipeSessionImpl) session;

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


