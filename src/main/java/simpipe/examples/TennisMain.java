package simpipe.examples;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.support.BaseIoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketConnector;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.core.EventLoop;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.executor.SchedulingExecutor;
import se.peertv.peertvsim.network.udp.bw.BandwidthManager;
import se.peertv.peertvsim.network.udp.bw.BandwidthModelImpl5WithVaryingSize;
import simpipe.base.support.SimPipeAddress;
import simpipe.protocol.SimpPipeUDPMessage;
import simpipe.tcp.SimPipeAcceptor;
import simpipe.tcp.SimPipeConnector;
import simpipe.udp.SimPipeUDPAcceptor;
import simpipe.udp.SimPipeUDPConnector;

public class TennisMain extends EventLoop {

	final static boolean SIM_MODE = true;
	final static boolean UDP_MODE = true;
	final static int PORT = 5000;
	SocketAddress serverAddress;
	private IoAcceptor acceptor;

	final static int example = 1; // 1 = tennis player, 2 = ticker, 3 = byte (bandwidth test)

	public TennisMain() {
		super();
	}

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();

		if (SIM_MODE) {
			SimulableSystem.setSimulation(true);
		}

		if (UDP_MODE) {
			BandwidthManager.init(BandwidthModelImpl5WithVaryingSize.class);

			BandwidthManager.registerPeerBandwidth("server", 1000, 200, 1000, 200);
			BandwidthManager.registerPeerBandwidth("client", 1000, 200, 1000, 200);
		}

		TennisMain m = new TennisMain();
		Reflection.setExecutingGroup("server");
		m.createServer();

		// ================================================================
		// CLIENT
		// ================================================================

		Reflection.setExecutingGroup("client");
		m.startClient();
		// m.startClient();
		// m.startClient();
		// m.startClient();
		// acceptor.unbind(address);

		// if (SIM_MODE) {
		// new SchedulingExecutor(1111111).schedule(m.new Unbinder(), 2000, TimeUnit.MILLISECONDS);
		// // Scheduler.getInstance().enqueue(new SimPipeKillEvent(550, "client"));
		// m.run();
		// }

		m.run();
	}

	public void createServer() throws Exception {

		IoHandlerAdapter handler = null;
		if (example == 1)
			handler = new TennisPlayer();
		else if (example == 2)
			handler = new TickerServer();
		else if (example == 3)
			handler = new ByteServer();
		IoServiceConfig config;

		// BaseIoConnector connector;

		// ---------------------
		// 1. Create an Acceptor
		// ---------------------
		if (SIM_MODE) {
			if (UDP_MODE)
				acceptor = new SimPipeUDPAcceptor();
			else
				acceptor = new SimPipeAcceptor();
		} else
			acceptor = new SocketAcceptor();

		// ---------------------
		// 2. Create a Config and get the chain
		// ---------------------
		if (SIM_MODE) {
			config = acceptor.getDefaultConfig();
		} else {
			config = acceptor.getDefaultConfig();
			DefaultIoFilterChainBuilder chain = config.getFilterChain();
			chain.addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		}

		// ---------------------
		// 4. Bind the acceptor with the config and an address
		// ---------------------
		if (SIM_MODE) {
			serverAddress = new SimPipeAddress(PORT);
			acceptor.bind(serverAddress, handler, config);
		} else {
			serverAddress = new InetSocketAddress(PORT);
			acceptor.bind(serverAddress, handler, config);
		}

	}

	void startClient() {
		BaseIoConnector connector;
		IoHandlerAdapter handler = null;
		if (example == 1)
			handler = new TennisPlayer();
		else if (example == 2)
			handler = new TickerClient();
		else if (example == 3)
			handler = new ByteClient();

		// Connect to the server.
		if (SIM_MODE) {
			if (UDP_MODE)
				connector = new SimPipeUDPConnector();
			else
				connector = new SimPipeConnector();
		} else {
			connector = new SocketConnector();
			connector.getDefaultConfig().getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		}

		ConnectFuture future = connector.connect(serverAddress, handler);
		// future.join();
		// IoSession session = future.getSession();
		//
		//		
		//
		// // Send the first ping message
		// session.write(msg);

		// Wait until the match ends.
		// session.getCloseFuture().join();
	}

	@Override
	public void postSimulationLoop() {

	}

	class Unbinder implements Runnable {

		@Override
		public void run() {
			for (IoSession session : acceptor.getManagedSessions(serverAddress)) {
				session.close();
			}
			acceptor.unbindAll();
			System.out.println("unbound");
		}

	}

	@Override
	public boolean postEventExecution() {
		// TODO Auto-generated method stub
		return false;
	}

}
