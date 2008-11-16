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
package simpipe.examples;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

import simpipe.protocol.SimpPipeUDPMessage;

/**
 * A {@link IoHandler} implementation which plays a tennis game.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 $
 */
public class TennisPlayer extends IoHandlerAdapter {
	private static int nextId = 0;

	/** Player ID * */
	private final int id = nextId++;

	@Override
	public void sessionCreated(final IoSession session) {
		System.out.println("Player-" + id + ": Created ");
		if (id != 0){
			SimpPipeUDPMessage msg = new SimpPipeUDPMessage(new TennisBall(5));
			session.write(msg);
		}
		
		session.setIdleTime(IdleStatus.READER_IDLE, 10000);
	}

	@Override
	public void sessionClosed(final IoSession session) {
		System.out.println("Player-" + id + ": QUIT");
	}

	@Override
	public void messageReceived(final IoSession session, final Object message) {
		System.out.println("Player-" + id + ": RCVD " + message);

		TennisBall ball = (TennisBall) ((SimpPipeUDPMessage) message).getPayload();

		// Stroke: TTL decreases and PING/PONG state changes.
		ball = ball.stroke();

		if (ball.getTTL() > 0) {
			// If the ball is still alive, pass it back to peer.
			session.write(message);
		} else {
			// // If the ball is dead, this player loses.
			System.out.println("Player-" + id + ": LOSE");
			session.close();
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		System.out.println("Player-" + id + ": IDLE");
		session.close();
	}

	@Override
	public void messageSent(final IoSession session, final Object message) {
		System.out.println("Player-" + id + ": SENT " + message);
	}

	@Override
	public void exceptionCaught(final IoSession session, final Throwable cause) {
		cause.printStackTrace();
		session.close();
	}
}