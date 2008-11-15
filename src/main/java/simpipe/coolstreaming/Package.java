package simpipe.coolstreaming;

import simpipe.coolstreaming.interfaces.Membership;
import simpipe.coolstreaming.interfaces.Partnership;
import simpipe.coolstreaming.interfaces.Scheduler;

public class Package {

	Partnership partners;
    Membership members;
    Scheduler scheduler;
    
	public Partnership getPartners() {
		return partners;
	}
	public void setPartners(Partnership partners) {
		this.partners = partners;
	}
	public Membership getMembers() {
		return members;
	}
	public void setMembers(Membership members) {
		this.members = members;
	}
	public Scheduler getScheduler() {
		return scheduler;
	}
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
}
