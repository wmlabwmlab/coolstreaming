package se.peertv.peertvsim.network.udp.bw;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.TreeMap;

import se.peertv.peertvsim.utils.A;

public class BandwidthManager {

	private static Class<? extends BandwidthModelVaryingSize> bwModelImplClass;

	private static Map<String, NodeBandwidth> nodesBandwidth = new TreeMap<String, NodeBandwidth>();

	public static void init(Class<? extends BandwidthModelVaryingSize> bwModelImplClassConstr) {
		if (bwModelImplClass == null) {
			bwModelImplClass = bwModelImplClassConstr;

		} else {

			try {
				A.ssert(false, "BandwidthManger already initialized");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void registerPeerBandwidth(String groupName, long downWindowSize, long downBandwidth, long upWidnowSize, long upBandwidth) {

		NodeBandwidth nodeBandwidth = new NodeBandwidth(downBandwidth, downWindowSize, upBandwidth, upWidnowSize);
		nodesBandwidth.put(groupName, nodeBandwidth);

	}

	public static NodeBandwidth getNodeBandwidth(String groupName) {
		return nodesBandwidth.get(groupName);
	}

	// private static BandwidthModelVaryingSize getBWModelInstance(String groupName, boolean isDownBW) {
	//
	// if (nodesBandwidth.containsKey(groupName)) {
	//
	// NodeBandwidth nodeBandwidth = nodesBandwidth.get(groupName);
	//
	// long windowSize;
	// long bandwidth;
	//
	// if (isDownBW) {
	// windowSize = nodeBandwidth.getDownWindowSize();
	// bandwidth = nodeBandwidth.getDownBandwidth();
	// } else {
	// windowSize = nodeBandwidth.getUpWindowSize();
	// bandwidth = nodeBandwidth.getUpBandwidth();
	// }
	//
	// return getCurrentBWModelInstance(bandwidth, windowSize, false);
	// } else {
	//
	// try {
	// A.ssert(false, "Bandwidth definition for " + groupName + " not available");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return null;
	// }
	//
	// }
	//
	// public static BandwidthModelVaryingSize getUpBWModelInstance(String groupName) {
	// return getBWModelInstance(groupName, false);
	// }
	//
	// public static BandwidthModelVaryingSize getDownBWModelInstance(String groupName) {
	// return getBWModelInstance(groupName, true);
	// }

	public static BandwidthModelVaryingSize getCurrentBWModelInstance(long bytesPerWindow, long windowSize, boolean trace) {
		if (bwModelImplClass == null) {

			try {
				A.ssert(false, "BandwidthManger not inititialized ");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;

		} else {

			BandwidthModelVaryingSize model = null;

			try {
				Constructor<?> modelConstructor = bwModelImplClass.getConstructor(long.class, long.class, boolean.class);
				model = (BandwidthModelVaryingSize) modelConstructor.newInstance(bytesPerWindow, windowSize, trace);

			} catch (Exception e) {
				System.err.println("Cannot instanciate bw model implementation");
				e.printStackTrace();
			}
			return model;
		}
	}

}
