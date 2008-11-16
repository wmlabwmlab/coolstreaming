package simpipe.coolstreaming.implementations;

import java.util.concurrent.ScheduledFuture;

public class Member {
	int port;
	ScheduledFuture<?> scheduledTask;

	public Member(int port, ScheduledFuture<?> scheduledTask){
		this.port = port;
		this.scheduledTask = scheduledTask;
	}
}
