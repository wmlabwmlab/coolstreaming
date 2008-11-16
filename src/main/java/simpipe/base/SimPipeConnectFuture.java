package simpipe.base;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.RuntimeIOException;
import org.apache.mina.common.support.DefaultConnectFuture;
import org.apache.mina.common.support.DefaultIoFuture;

public class SimPipeConnectFuture extends  DefaultIoFuture implements ConnectFuture { 

	public SimPipeConnectFuture(IoSession session) {
		super(session);
	}

	public static ConnectFuture newFailedFuture(Throwable exception) {

		DefaultConnectFuture failedFuture = new DefaultConnectFuture();

		failedFuture.setException(exception);

		return failedFuture;

	}

	@Override()
	public IoSession getSession() throws RuntimeIOException {

		Object v = getValue();

		if (v instanceof RuntimeIOException) {

			throw (RuntimeIOException) v;

		} else if (v instanceof Throwable) {

			throw (RuntimeIOException) new RuntimeIOException("Failed to get the session.").initCause((Throwable) v);

		} else {

			return (IoSession) v;

		}

	}

	public boolean isConnected() {

		return getValue() instanceof IoSession;

	}

	public void setSession(IoSession session) {

		setValue(session);

	}

	public void setException(Throwable exception) {

		setValue(exception);

	}
}
