package simpipe.support;

import org.apache.mina.common.IoFilterChain;
import org.apache.mina.common.IoSession;


public class Event {
	private final EventType type;

	private final Object data;

	private final IoSession targetSession;
	

	public Event(EventType type, Object data, IoSession targetSession) {
		super();
		this.type = type;
		this.data = data;
		this.targetSession = targetSession;
	}

	public Object getData() {
		return data;
	}

	public EventType getType() {
		return type;
	}

	public IoSession getTargetSession() {
		return targetSession;
	}



}