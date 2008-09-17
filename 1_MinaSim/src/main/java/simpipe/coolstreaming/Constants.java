package simpipe.coolstreaming;

public interface Constants {

	//General
	public static final int REQUEST_ACCEPTED = 1;
	public static final int REQUEST_REJECTED = 0;
	public static final String MESSAGE_SEPARATOR = "-";	
	//Server
	public static final int SERVER_PORT = 30000;
	
	//Peer Protocol Operations
	public static final char DEPUTY_MESSAGE = 'd';
	public static final char CONNECTION_REQUEST = 'c';
	public static final char PARTNERSHIP_ACCEPTANCE ='p';
	public static final char PARTNERSHIP_REQUEST = 'a' ;
	public static final char PARTNERSHIP_RESPONSE = 's' ;
	public static final char BANDWIDTH_REQUEST = 'm' ;
	public static final char BANDWIDTH_RESPONSE = 'n';
	public static final char LEAVE_NETWORK = 't' ;
	public static final char SEGMENT_REQUEST = 'x' ;
	public static final char SEGMENT_RESPONSE = 'y';
	public static final char BUFFERMAP_REQUEST = 'r' ;
	public static final char BUFFERMAP_RESPONSE = 'b';
	public static final char GOSSIPING = 'g';
	public static final char STABLEIZED = 'z';
	
}
