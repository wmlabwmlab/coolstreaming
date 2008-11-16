/*
 * Created on Jul 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author sameh
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package se.peertv.peertvsim.utils;

import java.awt.RadialGradientPaint;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import se.peertv.peertvsim.conf.Conf;

public class MultitonRandUtils {

	private static Map<String, Random> randomGenerators;

	public static Random getDedicatedRandom(String threadGroup) {

		Random rand = null;
		if (randomGenerators == null) {
			randomGenerators = new HashMap<String, Random>();
		}

		if (!randomGenerators.containsKey(threadGroup)) {
			rand = new Random(Conf.RANDOM_SEED);
			randomGenerators.put(threadGroup, rand);
		}

		return randomGenerators.get(threadGroup);
	}

	boolean firstInt = true;
	int firstIntVal = -1;

	public static void reset() {
		// System.out.println("rand reset; seed = " + Conf.RANDOM_SEED);

		if (randomGenerators != null)
			randomGenerators.clear();

	}

}
