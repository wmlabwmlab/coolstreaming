package simpipe.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.core.Reflection;
import se.peertv.peertvsim.core.Scheduler;
import simpipe.base.support.peers.PeerManager;

public class SimPipeKillEvent extends se.peertv.peertvsim.core.Event {

	private static final long serialVersionUID = 2629634728342673798L;
	
	private static final Logger log = LoggerFactory.getLogger(SimPipeKillEvent.class);

	public SimPipeKillEvent(long time, Object args) {
		super(time, args);
	}

	@Override
	public void handle() throws Throwable {

		String threadGroup = (String) args;
		PeerManager.killPeer(threadGroup);

		if (SimulableSystem.isSimPipeTraceEnabled())
			log.info("T(" + Scheduler.getInstance().getNow() + ")," + Reflection.getExecutingGroup() + "+++++++++++ KILLED ++++++++++++ " + args);

		System.out.println("Killed: " + args + " at " + Scheduler.getInstance().getNow());
	}

	@Override
	public String getThreadGroup() {
		return "noGroup";
	}

}
