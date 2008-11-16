package simpipe.examples;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

public class TickerClient  extends IoHandlerAdapter  {
	
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println(message);
	}
}
