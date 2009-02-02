package se.peertv.peertvsim.conf;

import java.lang.reflect.Field;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.data.DataBlob;

public class Conf {
	public static boolean USE_BANDWIDTH_MODEL = false;

	public static long CONNECTIONDELAY = 5;

	// Network Parameters
	public static int MAX_LINK_DELAY = 400;

	// Simulator Parameters
	public static long MAX_SIMULATION_TIME = 1000 * 60 * 15; // 15 minutes
	// public static long MAX_SIMULATION_TIME = 1000 * 10 * 4; // 10 minutes

	public static int RANDOM_SEED = 4;

	public static float UDP_FAILURE_PERCENT = 0;

	public static int UDP_REORDERING_PERCENTILE = 0;

	public static long SOURCES_BANDWIDTH ;

	public static String PEERS_BANDWIDTH = "0.5:125000-125000 0.5:125000-250000";

	public static boolean BW_SANITY_CHECK = false;

	public static boolean STDOT_TRACE = SimulableSystem.isMinaSimTraceEnabled();

	public static void dump() throws Exception {
		for (Field f : Conf.class.getDeclaredFields()) {
			String name = f.getName();
			Object value = f.get(Conf.class);
			System.out.println(name + " =  " + value);
		}
	}

	public static DataBlob blobify() throws Exception {
		DataBlob db = new DataBlob();
		for (Field f : Conf.class.getDeclaredFields()) {
			String name = f.getName();
			Object value = f.get(Conf.class);
			db.set(name, value);
		}
		return db;
	}

	public static void init(DataBlob params) throws Exception {
		for (Field f : Conf.class.getDeclaredFields()) {
			String name = f.getName();
			Object o = params.get(name);
			if (o != null)
				// FIXME: check presence firest
				f.set(Conf.class, o);
		}

	}

	public static void main(String[] args) throws Exception {
		dump();
	}
}
