package simpipe.support;


//FIXME Copied and pasted from {@link ExecutorFilter}.
public class EventType {
	public static final EventType CREATED = new EventType("CREATED");

	public static final EventType OPENED = new EventType("OPENED");

	public static final EventType CLOSED = new EventType("CLOSED");

	public static final EventType RECEIVED = new EventType("RECEIVED");

	public static final EventType SENT = new EventType("SENT");

	public static final EventType IDLE = new EventType("IDLE");

	public static final EventType EXCEPTION = new EventType("EXCEPTION");

	public static final EventType WRITE = new EventType("WRITE");

	public static final EventType CLOSE = new EventType("CLOSE");

	private final String value;

	private EventType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}