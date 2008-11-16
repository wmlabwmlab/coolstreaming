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

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoFilterChain;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoService;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.IoSessionConfig;
import org.apache.mina.common.TransportType;
import org.apache.mina.common.IoFilter.WriteRequest;
import org.apache.mina.common.support.BaseIoSession;
import org.apache.mina.common.support.BaseIoSessionConfig;
import org.apache.mina.common.support.IoServiceListenerSupport;

import se.peertv.peertvsim.SimulableSystem;

/**
 * A {@link IoSession} for in-VM transport (VM_PIPE).
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 575603 $, $Date: 2007-09-14 19:04:45 +0900
 */
public class SimPipeSessionImpl extends BaseIoSession {
	private static final IoSessionConfig CONFIG = new BaseIoSessionConfig() {
	};

	public static final Map<String, SimPipeSessionImpl> sessionsMap=new ConcurrentHashMap<String, SimPipeSessionImpl>();
	
	private final IoService service;

	private final IoServiceConfig serviceConfig;

	private final IoServiceListenerSupport serviceListeners;

	private final SocketAddress localAddress;

	private final SocketAddress remoteAddress;

	private final SocketAddress serviceAddress;

	private final IoHandler handler;

	private final SimPipeFilterChain filterChain;

	private final SimPipeSessionImpl remoteSession;

	private final Lock lock;

	final BlockingQueue<Object> pendingDataQueue;

	// Configuration variables
	private int idleTimeForRead;

	private int idleTimeForWrite;

	private int idleTimeForBoth;

	private long lastReadTime;

	private long lastWriteTime;

	private int idleCountForBoth;

	private int idleCountForRead;

	private int idleCountForWrite;

	private long lastIdleTimeForBoth;

	private long lastIdleTimeForRead;

	private long lastIdleTimeForWrite;
	
	private final String sessionId;

	/*
	 * Constructor for client-side session.
	 */
	public SimPipeSessionImpl(final IoService service, final IoServiceConfig serviceConfig, final IoServiceListenerSupport serviceListeners, final SocketAddress localAddress, final IoHandler handler,
			final SimPipe remoteEntry) {

		lastReadTime = lastWriteTime = lastIdleTimeForBoth = lastIdleTimeForRead = lastIdleTimeForWrite = SimulableSystem.currentTimeMillis();

		this.service = service;
		this.serviceConfig = serviceConfig;
		this.serviceListeners = serviceListeners;
		lock = new ReentrantLock();
		this.localAddress = localAddress;
		// this code is always executed by the connector
		remoteAddress = serviceAddress = remoteEntry.getAddress();

		this.handler = handler;
		filterChain = createFilterChain();
		pendingDataQueue = new LinkedBlockingQueue<Object>();

		remoteSession = createRemoteSession(remoteEntry);
		
		this.sessionId=localAddress+":"+remoteAddress;
		
		sessionsMap.put(sessionId,this);
	}

	protected SimPipeSessionImpl createRemoteSession(final SimPipe remoteEntry) {
		return new SimPipeSessionImpl(this, remoteEntry);
	}

	/*
	 * Constructor for server-side session.
	 */
	protected SimPipeSessionImpl(final SimPipeSessionImpl remoteSession, final SimPipe entry) {

		lastReadTime = lastWriteTime = lastIdleTimeForBoth = lastIdleTimeForRead = lastIdleTimeForWrite = SimulableSystem.currentTimeMillis();

		service = entry.getAcceptor();
		serviceConfig = entry.getConfig();
		serviceListeners = entry.getListeners();
		lock = remoteSession.lock;
		localAddress = serviceAddress = remoteSession.remoteAddress;
		remoteAddress = remoteSession.localAddress;

		handler = entry.getHandler();
		filterChain = createFilterChain();
		this.remoteSession = remoteSession;
		pendingDataQueue = new LinkedBlockingQueue<Object>();
		
		this.sessionId=localAddress+":"+remoteAddress;
		
		sessionsMap.put(sessionId,this);
	}

	protected SimPipeFilterChain createFilterChain() {
		return new SimPipeFilterChain(this);
	}

	public IoService getService() {
		return service;
	}

	IoServiceListenerSupport getServiceListeners() {
		return serviceListeners;
	}

	public IoServiceConfig getServiceConfig() {
		return serviceConfig;
	}

	public IoSessionConfig getConfig() {
		return CONFIG;
	}

	public IoFilterChain getFilterChain() {
		return filterChain;
	}

	public SimPipeSessionImpl getRemoteSession() {
		return remoteSession;
	}

	public IoHandler getHandler() {
		return handler;
	}

	@Override
	protected void close0() {
		filterChain.fireFilterClose(this);
	}

	// session.write.. comes first back to us here.
	@Override
	protected void write0(final WriteRequest writeRequest) {
		filterChain.fireFilterWrite(this, writeRequest);
	}

	public TransportType getTransportType() {
		return TransportType.VM_PIPE;
	}

	public SocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public SocketAddress getLocalAddress() {
		return localAddress;
	}

	public SocketAddress getServiceAddress() {
		return serviceAddress;
	}

	@Override
	protected void updateTrafficMask() {
		if (getTrafficMask().isReadable() || getTrafficMask().isWritable()) {
			final List<Object> data = new ArrayList<Object>();

			pendingDataQueue.drainTo(data);

			for (final Object aData : data) {
				if (aData instanceof WriteRequest) {
					// TODO Optimize unefficient data transfer.
					// Data will be returned to pendingDataQueue
					// if getTraffic().isWritable() is false.
					final WriteRequest wr = (WriteRequest) aData;
					filterChain.doWrite(this, wr);
				} else {
					// TODO Optimize unefficient data transfer.
					// Data will be returned to pendingDataQueue
					// if getTraffic().isReadable() is false.
					filterChain.fireMessageReceived(this, aData);
				}
			}
		}
	}

	Lock getLock() {
		return lock;
	}

	@Override
	public String toString() {
		return "(SIM_PIPE, R: " + getRemoteAddress() + ", L: " + getLocalAddress() + ", S: " + getServiceAddress() + ')';
	}

	@Override
	public int getIdleTime(IdleStatus status) {
		if (status == IdleStatus.BOTH_IDLE)
			return idleTimeForBoth;

		if (status == IdleStatus.READER_IDLE)
			return idleTimeForRead;

		if (status == IdleStatus.WRITER_IDLE)
			return idleTimeForWrite;

		throw new IllegalArgumentException("Unknown idle status: " + status);
	}

	@Override
	public long getIdleTimeInMillis(IdleStatus status) {
		return getIdleTime(status) /* *1000L */;
	}

	@Override
	public void increaseReadBytes(int increment) {
		super.increaseReadBytes(increment);
		if (increment > 0) {
			lastReadTime = SimulableSystem.currentTimeMillis();
			idleCountForBoth = 0;
			idleCountForRead = 0;
		}
	}

	@Override
	public void increaseWrittenBytes(int increment) {
		super.increaseWrittenBytes(increment);
		if (increment > 0) {
			lastWriteTime = SimulableSystem.currentTimeMillis();
			idleCountForBoth = 0;
			idleCountForWrite = 0;
		}
	}

	@Override
	public void increaseReadMessages() {
		super.increaseReadMessages();
		lastReadTime = SimulableSystem.currentTimeMillis();
	}

	@Override
	public long getLastIoTime() {
		return Math.max(lastReadTime, lastWriteTime);
	}

	@Override
	public long getLastReadTime() {
		return lastReadTime;
	}

	@Override
	public long getLastWriteTime() {
		return lastWriteTime;
	}

	@Override
	public void setIdleTime(IdleStatus status, int idleTime) {
		if (idleTime < 0)
			throw new IllegalArgumentException("Illegal idle time: " + idleTime);

		if (status == IdleStatus.BOTH_IDLE)
			idleTimeForBoth = idleTime;
		else if (status == IdleStatus.READER_IDLE)
			idleTimeForRead = idleTime;
		else if (status == IdleStatus.WRITER_IDLE)
			idleTimeForWrite = idleTime;
		else
			throw new IllegalArgumentException("Unknown idle status: " + status);
	}

	@Override
	public boolean isIdle(IdleStatus status) {
		if (status == IdleStatus.BOTH_IDLE)
			return idleCountForBoth > 0;

		if (status == IdleStatus.READER_IDLE)
			return idleCountForRead > 0;

		if (status == IdleStatus.WRITER_IDLE)
			return idleCountForWrite > 0;

		throw new IllegalArgumentException("Unknown idle status: " + status);
	}

	@Override
	public int getIdleCount(IdleStatus status) {
		if (status == IdleStatus.BOTH_IDLE)
			return idleCountForBoth;

		if (status == IdleStatus.READER_IDLE)
			return idleCountForRead;

		if (status == IdleStatus.WRITER_IDLE)
			return idleCountForWrite;

		throw new IllegalArgumentException("Unknown idle status: " + status);
	}

	@Override
	public long getLastIdleTime(IdleStatus status) {
		if (status == IdleStatus.BOTH_IDLE)
			return lastIdleTimeForBoth;

		if (status == IdleStatus.READER_IDLE)
			return lastIdleTimeForRead;

		if (status == IdleStatus.WRITER_IDLE)
			return lastIdleTimeForWrite;

		throw new IllegalArgumentException("Unknown idle status: " + status);
	}

	@Override
	public void increaseIdleCount(IdleStatus status) {
		if (status == IdleStatus.BOTH_IDLE) {
			idleCountForBoth++;
			lastIdleTimeForBoth = SimulableSystem.currentTimeMillis();
		} else if (status == IdleStatus.READER_IDLE) {
			idleCountForRead++;
			lastIdleTimeForRead = SimulableSystem.currentTimeMillis();
		} else if (status == IdleStatus.WRITER_IDLE) {
			idleCountForWrite++;
			lastIdleTimeForWrite = SimulableSystem.currentTimeMillis();
		} else
			throw new IllegalArgumentException("Unknown idle status: " + status);
	}

	public String getSessionId() {
		return sessionId;
	}
}
