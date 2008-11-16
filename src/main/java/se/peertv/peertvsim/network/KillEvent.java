package se.peertv.peertvsim.network;



public class KillEvent extends se.peertv.peertvsim.core.Event {

	private static final long serialVersionUID = -4965494267531429804L;

	public KillEvent(long time, Object args) {
		super(time, args);
	}

	@Override
	public void handle() throws Throwable {
		int id = (Integer) args;
		Network.getInstance().remove(id);
	}

	@Override
	public String getThreadGroup() {
		return "noGroup";
	}

}
