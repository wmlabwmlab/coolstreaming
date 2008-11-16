/**
 * =============================================== 
 *  File     : $Id: Network.java,v 1.5 2007/06/06 09:22:48 sameh Exp $
 *  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
 *  Version  :$Revision: 1.5 $
 *  Tag	  : $Name:  $
 *  Last edited by   : $Author: sameh $
 *  Last updated:    $Date: 2007/06/06 09:22:48 $
 *===============================================
 */
package se.peertv.peertvsim.network;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import se.peertv.peertvsim.utils.A;

public class Network {

	private Map<Integer, Node> aliveNodes;
	private Set<Integer> removedNodes;

	private static Network singeltonNetwork = null;

	private Network() {
		aliveNodes = new HashMap<Integer, Node>();
		removedNodes = new HashSet<Integer>();
	}

	public static Network getInstance() {
		if (singeltonNetwork == null)
			singeltonNetwork = new Network();
		return singeltonNetwork;
	}

	public Node get(int nodeId) {
		return aliveNodes.get(nodeId);
	}

	public Node add(Node node) throws Exception {
		A.ssert(!aliveNodes.containsKey(node.getId()), "node not present");

		// if (removedNodes.contains(node.getId())) {
		// removedNodes.remove(node.getId());
		// }

		return aliveNodes.put(node.getId(), node);
	}

	public Node remove(int id) {
		// removedNodes.add(id);
		return aliveNodes.remove(id);
	}

	public boolean contains(int dest) {
		return aliveNodes.containsKey(dest);
	}

	public boolean isNodeInNetwork(int id) {
		return aliveNodes.containsKey(id);
	}

	// public synchronized boolean isRemovedNode(int id) {
	// return removedNodes.contains(id);
	// }

	public void clear() {
		aliveNodes.clear();
		Message.reset();
	}

}
