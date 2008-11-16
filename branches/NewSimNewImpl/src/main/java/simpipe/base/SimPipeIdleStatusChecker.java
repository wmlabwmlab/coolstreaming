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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoSession;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.executor.SchedulingExecutor;
import simpipe.base.support.SimPipeAddress;
import simpipe.base.support.peers.PeerManager;

/**
 * Dectects idle sessions and fires <tt>sessionIdle</tt> events to them.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 585077 $, $Date: 2007-10-16 17:36:14 +0900 (화, 16 10월 2007) $
 */
public class SimPipeIdleStatusChecker {
	private static final SimPipeIdleStatusChecker INSTANCE = new SimPipeIdleStatusChecker();

	public static SimPipeIdleStatusChecker getInstance() {
		return INSTANCE;
	}

	private final Set<SimPipeSessionImpl> sessions = new HashSet<SimPipeSessionImpl>();

	private final Worker worker = new Worker();

	private final SchedulingExecutor checker;

	private final static long DELAY = 1000;

	private final List<IoSession> toBeRemoved;

	private SimPipeIdleStatusChecker() {
		toBeRemoved = new ArrayList<IoSession>();
		checker = new SchedulingExecutor(222222222);
		checker.scheduleAtFixedRate(worker, true, DELAY, DELAY, TimeUnit.MILLISECONDS);
	}

	public void addSession(SimPipeSessionImpl session) {
		synchronized (sessions) {
			sessions.add(session);
		}
	}

	private class Worker implements Runnable {

		@Override
		public void run() {
			long currentTime = SimulableSystem.currentTimeMillis();

			// synchronized (sessions) {
			// System.out.println("Idle checker loop: " + sessions);

			/*
			 * removing old sessions
			 */
			for (IoSession session : toBeRemoved) {
				sessions.remove(session);
			}

			Iterator<SimPipeSessionImpl> it = sessions.iterator();
			while (it.hasNext()) {
				SimPipeSessionImpl session = it.next();
				if (!session.isConnected()) {
					it.remove();
				} else {

					PeerManager.setHandlingTCP(true);

					if (PeerManager.getPeer(((SimPipeAddress) session.getLocalAddress()).getOwnerGroup()) != null)
						notifyIdleSession(session, currentTime);
				}
			}
			// }
		}
	}

	private void notifyIdleSession(SimPipeSessionImpl session, long currentTime) {
		notifyIdleSession0(session, currentTime, session.getIdleTimeInMillis(IdleStatus.BOTH_IDLE), IdleStatus.BOTH_IDLE, Math.max(session.getLastIoTime(), session.getLastIdleTime(IdleStatus.BOTH_IDLE)));
		notifyIdleSession0(session, currentTime, session.getIdleTimeInMillis(IdleStatus.READER_IDLE), IdleStatus.READER_IDLE, Math.max(session.getLastReadTime(), session.getLastIdleTime(IdleStatus.READER_IDLE)));
		notifyIdleSession0(session, currentTime, session.getIdleTimeInMillis(IdleStatus.WRITER_IDLE), IdleStatus.WRITER_IDLE, Math.max(session.getLastWriteTime(), session.getLastIdleTime(IdleStatus.WRITER_IDLE)));
	}

	private void notifyIdleSession0(SimPipeSessionImpl session, long currentTime, long idleTime, IdleStatus status, long lastIoTime) {
		if (idleTime > 0 && lastIoTime != 0 && (currentTime - lastIoTime) >= idleTime) {
			session.increaseIdleCount(status);
			session.getFilterChain().fireSessionIdle(session, status);
		}
	}

	public void remove(IoSession session) {
		toBeRemoved.add(session);
	}

}