package simpipe.coolstreaming;

import se.peertv.peertvsim.core.Timer;

public class Member {
		int port;
		Timer timer;

		public Member(int port, Timer timer){
			this.port = port;
			this.timer = timer;
		}
}
