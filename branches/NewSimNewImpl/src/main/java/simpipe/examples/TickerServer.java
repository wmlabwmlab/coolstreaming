package simpipe.examples;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

public class TickerServer  extends IoHandlerAdapter  {

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		for (int i = 0; i < 10; i++) {
			session.write(i);
		}
	}
}
