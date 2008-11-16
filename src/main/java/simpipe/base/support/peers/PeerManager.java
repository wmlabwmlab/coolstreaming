package simpipe.base.support.peers;

import java.util.HashMap;
import java.util.Map;

import se.peertv.peertvsim.conf.Conf;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.network.Network;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.network.tcp.bw.gen.BandwidthDistributionGenerator;
import se.peertv.peertvsim.network.tcp.bw.gen.BandwidthDistributionGenerator.NodeBandwith;
import simpipe.base.SimPipe;
import simpipe.base.support.SimPipeAddress;

public class PeerManager {

	private static boolean isTCP = true;

	private static HashMap<String/* peer name */, Node> peerMapUDP = new HashMap<String, Node>();
	private static HashMap<String/* peer name */, Node> peerMapTCP = new HashMap<String, Node>();

	private static HashMap<String/* peer name */, HashMap<Integer/* port */, SimPipe>> bindingMap = new HashMap<String, HashMap<Integer, SimPipe>>();
	private static int nextId = 0;

	// hackish way to make things working w/out modifing the enormous amount of code that depends that SimPipeAddress only takes a port!
	// this maps a port to the server that listens to it
	public static HashMap<Integer/* port */, String/* peerName */> portMapping = new HashMap<Integer, String>();

	// used for clients (connectors)
	public static Node createTCPPeer(long downCapacity, long upCapacity) throws Exception {
		String peerName = Reflection.getExecutingGroup();

		isTCP = true;
		
		if (peerExists(peerName)) {
			return getPeer();
		}

		final Node peer = new TCPPeer(nextId(), downCapacity, upCapacity);

		try {
			Network.getInstance().add(peer);
		} catch (Exception e) {
			throw new Exception("Can't add node", e);
		}

		addPeerMapping(peer);

		return peer;
	}

	public static Node createUDPPeer() throws Exception {
		return createUDPPeer(0, 0, 0, 0);
	}

	public static Node createUDPPeer(long downBandwidth, long downWindowSize, long upBandwidth, long upWindowSize) throws Exception {
		String peerName = Reflection.getExecutingGroup();

		isTCP = false;

		if (peerExists(peerName)) {
			return getPeer();
		}

		final Node peer;

		if (downBandwidth == 0 && downWindowSize == 0) {
			peer = new UDPPeer(nextId());
		} else {
			peer = new UDPPeer(nextId(), downBandwidth, downWindowSize, upBandwidth, upWindowSize);
		}
		try {
			Network.getInstance().add(peer);
		} catch (Exception e) {
			throw new Exception("Can't add node", e);
		}

		addPeerMapping(peer);

		return peer;
	}

	public static void killPeer(String threadGroup) throws Exception {

		Node node = null;
		try {
			isTCP = true;
			if ((node = getPeer(threadGroup)) != null) {
				Network.getInstance().remove(node.getId());
				peerMapTCP.remove(threadGroup);
			}
			isTCP = false;
			if ((node = getPeer(threadGroup)) != null) {
				Network.getInstance().remove(node.getId());
				peerMapUDP.remove(threadGroup);
			}

		} catch (Exception e) {
			throw new Exception("Can't remove node", e);
		}

	}

	private static int nextId() {
		return nextId++;
	}

	private static Node getPeer() {

		String peerName = Reflection.getExecutingGroup();

		return getPeer(peerName);
	}

	public static Node getPeer(String peerName) {
		Node node = null;

		if (isTCP) {
			node = peerMapTCP.get(peerName);
		} else {
			node = peerMapUDP.get(peerName);
		}

		return node;
	}

	public static boolean peerExists(String peerName) {

		if (isTCP) {
			return peerMapTCP.containsKey(peerName);

		} else {

			return peerMapUDP.containsKey(peerName);

		}

	}

	private static void addPeerMapping(Node peer) {
		String peerName = Reflection.getExecutingGroup();

		if (isTCP) {
			peerMapTCP.put(peerName, peer);
		} else {
			peerMapUDP.put(peerName, peer);
		}

		if (bindingMap.get(peerName) == null)
			bindingMap.put(peerName, new HashMap<Integer, SimPipe>());
	}

	// used for servers
	public static void bind(SimPipe pipe) throws Exception {
		String peerName = Reflection.getExecutingGroup();

		if (!peerExists(peerName))
			throw new Exception("Peer not created");

		bindingMap.get(peerName).put(pipe.getAddress().getPort(), pipe);
		portMapping.put(pipe.getAddress().getPort(), peerName);
	}

	public static String getPeerName(int port) {
		return portMapping.get(port);
	}

	public static SimPipe getBinding(SimPipeAddress address) {
		String peerName = portMapping.get(address.getPort()); // Reflection.getExecutingGroup();

		if (!peerExists(peerName))
			return null;

		return bindingMap.get(peerName).get(address.getPort());
	}

	public static void unbind(int port, boolean isTCP) {
		String peerName = Reflection.getExecutingGroup();

		if (!peerExists(peerName))
			return;

		bindingMap.get(peerName).remove(port);
		portMapping.remove(port);
	}

	public static Map<Integer, SimPipe> getBoundPorts(String threadGroup) {
		return bindingMap.get(threadGroup);
	}

	public static void setHandlingTCP(boolean isTCP) {
		PeerManager.isTCP = isTCP;
	}

}
