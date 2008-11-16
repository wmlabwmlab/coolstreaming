package simpipe.examples;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

public class ByteServer extends IoHandlerAdapter {

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		ByteBuffer bb = ByteBuffer.allocate(1000000);
		session.write(bb);
	}	
}
