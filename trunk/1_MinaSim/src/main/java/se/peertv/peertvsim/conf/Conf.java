package se.peertv.peertvsim.conf;

import java.lang.reflect.Field;

import se.peertv.peertvsim.data.DataBlob;


public class Conf {
	public static long CONNECTIONDELAY = 5;


	// Network Parameters
	public static int MAX_LINK_DELAY = 300; 


	// Simulator Parameters
	public static long MAX_SIMULATION_TIME = 1000*30; //1000*60*10; // 10 minutes

	public static int RANDOM_SEED = 4;
		
	public static String SOURCES_BANDWIDTH = "125000 1250000";

	public static boolean BW_SANITY_CHECK = false;

    public static boolean STDOT_TRACE = true;

	public static void dump() throws Exception {
		for (Field f : Conf.class.getDeclaredFields()) {
			String name = f.getName();
			Object value = f.get((Object) Conf.class);
			System.out.println(name + " =  " + value);
		}
	}
	
	public static DataBlob blobify() throws Exception {
		DataBlob db = new DataBlob();
		for (Field f : Conf.class.getDeclaredFields()) {
			String name = f.getName();
			Object value = f.get((Object) Conf.class);
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
				f.set((Object) Conf.class, o);
		}

	}

	public static void main(String[] args) throws Exception {
		dump();
	}
}
