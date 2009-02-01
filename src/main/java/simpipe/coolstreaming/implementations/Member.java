package simpipe.coolstreaming.implementations;

import se.peertv.peertvsim.SimulableSystem;

public class Member {
	int port;
	long latestAlive ;
	boolean alive = true;

	public Member(int port, boolean alive, long latestAlive){
		this.port = port;
		this.alive = alive;
		this.latestAlive = SimulableSystem.currentTimeMillis();
	}
}
