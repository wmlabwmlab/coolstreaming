package simpipe.base.support;

import java.io.Serializable;
import java.net.SocketAddress;

import org.apache.mina.common.IoSession;

public class MinaEvent implements Serializable {
	private static final long serialVersionUID = 5458374620767502895L;

	private final MinaEventType type;

	private final Object data;

	private final String targetSessionId;

	public MinaEvent(MinaEventType type, Object data, String sessionId) {
		super();
		this.type = type;
		this.data = data;
		this.targetSessionId = sessionId;
	}

	public Object getData() {
		return data;
	}

	public MinaEventType getType() {
		return type;
	}

	public String getTargetSessionId() {
		return targetSessionId;
	}

}