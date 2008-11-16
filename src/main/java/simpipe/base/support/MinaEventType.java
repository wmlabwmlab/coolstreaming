package simpipe.base.support;

import java.io.Serializable;


//FIXME Copied and pasted from {@link ExecutorFilter}.
public class MinaEventType implements Serializable{
	private static final long serialVersionUID = 7672755068408516834L;

	public static final MinaEventType CREATED = new MinaEventType("CREATED");

	public static final MinaEventType OPENED = new MinaEventType("OPENED");

	public static final MinaEventType CLOSED = new MinaEventType("CLOSED");

	public static final MinaEventType RECEIVED = new MinaEventType("RECEIVED");

	public static final MinaEventType SENT = new MinaEventType("SENT");

	public static final MinaEventType IDLE = new MinaEventType("IDLE");

	public static final MinaEventType EXCEPTION = new MinaEventType("EXCEPTION");

	public static final MinaEventType WRITE = new MinaEventType("WRITE");

	public static final MinaEventType CLOSE = new MinaEventType("CLOSE");

	private final String value;

	private MinaEventType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}