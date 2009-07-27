package org.argeo.slc.jsch;

import java.io.IOException;
import java.io.InputStream;

import org.argeo.slc.SlcException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public abstract class AbstractJschTask implements Runnable {
	private SshTarget sshTarget;

	protected Session openSession() {
		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(getSshTarget().getUser(),
					getSshTarget().getHost(), getSshTarget().getPort());

			session.setUserInfo(getSshTarget().getUserInfo());
			session.connect();
			return session;
		} catch (JSchException e) {
			throw new SlcException("Could not open session to "
					+ getSshTarget(), e);
		}
	}

	public final void run() {
		Session session = openSession();
		try {
			run(session);
		} finally {
			session.disconnect();
		}
	}

	abstract void run(Session session);

	protected int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		else if (b == -1)
			return b;//throw new SlcException("SSH ack returned -1");
		else if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				throw new SlcException("SSH ack error: " + sb.toString());
			}
			if (b == 2) { // fatal error
				throw new SlcException("SSH fatal error: " + sb.toString());
			}
		}
		return b;
	}

	public SshTarget getSshTarget() {
		return sshTarget;
	}

	public void setSshTarget(SshTarget sshTarget) {
		this.sshTarget = sshTarget;
	}

}
