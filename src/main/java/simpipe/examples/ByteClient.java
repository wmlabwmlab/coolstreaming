package simpipe.examples;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

public class ByteClient extends IoHandlerAdapter {
	@Override
	public void messageReceived(IoSession session, Object msg) throws Exception {
		System.out.println("Recieved " + ((ByteBuffer)msg).capacity() + " bytes");
	}
}
