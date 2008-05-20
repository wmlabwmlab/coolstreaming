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
import java.util.Map;

import se.peertv.peertvsim.utils.A;


public class Network {


	Map<Integer, Node> nodes;
	private static Network singeltonNetwork = null;

	public static Network getInstance() {
		if (singeltonNetwork == null)
			singeltonNetwork = new Network();
		return singeltonNetwork;
	}


	public Node get(int nodeId) {
		return nodes.get(nodeId);
	}

	public Node add( Node node) throws Exception{
		A.ssert(!nodes.containsKey(node.getId()), "node not present");
		return nodes.put(node.getId(),node);
	}

	private Network(){
		nodes = new HashMap<Integer, Node>();
	}
	public Collection<Node> getNodes(){
		return nodes.values();
	}


	public boolean contains(int dest) {
		return nodes.containsKey(dest);
	}


	public void clear() {
		nodes.clear();
		Message.reset();
	}

}
