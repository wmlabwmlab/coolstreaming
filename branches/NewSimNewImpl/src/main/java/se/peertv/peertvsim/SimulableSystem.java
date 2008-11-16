package se.peertv.peertvsim;

import java.io.File;

import se.peertv.peertvsim.core.Scheduler;

public class SimulableSystem {

	private static boolean isSimulation = true;

	private static boolean isMinaSimTraceEnabled = false;

	private static boolean isSimPipeTraceEnabled = false;

	private static boolean isWriteContentToFileEnabled = true;
	
	private static long timeoffset;

	public static final String fakePublisherDir = System.getProperty(
			"myp2p.fakePublisherDir", "src/main/resources/PublisherFakeDB/");

	public static boolean isClientMonitorEnabled = false;

	private static boolean isSimulationFinished = false;

	public static long currentTimeMillis() {
		if (!isSimulation) {
			return System.currentTimeMillis() - getTimeoffset(); // adjust the time with the offset so we can have global unified time. Is done for GlobalTime class
		} else {
			return Scheduler.getInstance().getNow();
		}
	}

	public static long getTimeoffset() {
		return timeoffset;
	}

	public static void setTimeoffset(long timeoffset) {
		SimulableSystem.timeoffset = timeoffset;
	}

	public static boolean isSimulation() {
		return isSimulation;
	}

	public static void setSimulation(final boolean isSimulationSet) {
		isSimulation = isSimulationSet;
	}

	public static void println(final String str) {
	}

	public static boolean isMinaSimTraceEnabled() {
		return isMinaSimTraceEnabled;
	}

	public static boolean isSimPipeTraceEnabled() {
		return isSimPipeTraceEnabled;
	}

	public static void setSimPipeLoggingEnabled(
			final boolean isSimPipeTraceEnabled) {
		SimulableSystem.isSimPipeTraceEnabled = isSimPipeTraceEnabled;
	}

	public static void setMinaSimTraceEnabled(
			final boolean isMinaSimTraceEnabled) {
		SimulableSystem.isMinaSimTraceEnabled = isMinaSimTraceEnabled;
	}

	public static boolean assertSimOrRealConstraints() {
		final String workingDir = new File(System.getProperty("user.dir"))
				.getName();
		if (workingDir.equals("MyP2PWorld"))
			if (!SimulableSystem.isSimulation()) {
				System.out
						.println("MyP2PWorld can only be run in simulation mode.\nTurn ON SimulableSystem.isSimulation.");
				return false;
			} else
				return true;
		else // Not in MyP2PWorld
		if (SimulableSystem.isSimulation()) {
			System.out
					.println("Simulations can only be done from MyP2PWorld.\nTurn OFF SimulableSystem.isSimulation");
			return false;
		}
		return true;
	}

	public static boolean isRealMode() {
		return isWriteContentToFileEnabled;
	}

	public static boolean isWriteContentToFileIfSimulation() {
		if (!isSimulation) {
			return true;
		} else {
			if (isWriteContentToFileEnabled)
				return true;
		}
		return false;
	}

	public static void setWriteContentToFile(final boolean isRealMode) {
		SimulableSystem.isWriteContentToFileEnabled = isRealMode;
	}

	public static boolean isSimulationFinished() {
		return isSimulationFinished;
	}

	public static void setSimulationFinished(boolean isSimulationFinished) {
		SimulableSystem.isSimulationFinished = isSimulationFinished;
	}

	public static synchronized void setClientMonitorEnabled(
			boolean isClientMonitorEnabled) {
		SimulableSystem.isClientMonitorEnabled = isClientMonitorEnabled;
	}

}
