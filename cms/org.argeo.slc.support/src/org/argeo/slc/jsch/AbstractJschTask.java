package org.argeo.slc.jsch;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivilegedAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserAuthGSSAPIWithMIC;

public abstract class AbstractJschTask implements Runnable {
	private final Log log = LogFactory.getLog(getClass());

	private SshTarget sshTarget;

	protected Session openSession() {
		if (sshTarget.getSession() != null) {
			Session session = sshTarget.getSession();
			if (session.isConnected()) {
				if (log.isTraceEnabled())
					log.debug("Using cached session to " + getSshTarget() + " via SSH");
				return session;
			}
		}

		try {
			JSch jsch = new JSch();
			if (sshTarget.getUsePrivateKey() && sshTarget.getLocalPrivateKey().exists())
				jsch.addIdentity(sshTarget.getLocalPrivateKey().getAbsolutePath());
			Session session = jsch.getSession(getSshTarget().getUser(), getSshTarget().getHost(),
					getSshTarget().getPort());

			session.setUserInfo(getSshTarget().getUserInfo());
			session.setConfig("userauth.gssapi-with-mic", UserAuthGSSAPIWithMIC.class.getName());
			session.setServerAliveInterval(1000);
			session.connect();
			if (log.isTraceEnabled())
				log.trace("Connected to " + getSshTarget() + " via SSH");
			if (sshTarget.getSession() != null) {
				if (log.isTraceEnabled())
					log.trace("The cached session to " + getSshTarget() + " was disconnected and was reset.");
				sshTarget.setSession(session);
			}
			return session;
		} catch (JSchException e) {
			if (sshTarget.getUserInfo() instanceof SimpleUserInfo)
				((SimpleUserInfo) sshTarget.getUserInfo()).reset();
			throw new SlcException("Could not open session to " + getSshTarget(), e);
		}
	}

	public void run() {
		Session session = openSession();
		try {
			run(session);
		} finally {
			if (sshTarget != null && sshTarget.getSession() == null) {
				session.disconnect();
				if (log.isTraceEnabled())
					log.trace("Disconnected from " + getSshTarget() + " via SSH");
			}
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
			return b;// throw new SlcException("SSH ack returned -1");
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
		if (sshTarget == null)
			throw new SlcException("No SSH target defined.");
		return sshTarget;
	}

	public void setSshTarget(SshTarget sshTarget) {
		this.sshTarget = sshTarget;
	}

	PrivilegedAction<Void> asPrivilegedAction() {
		return new PrivilegedAction<Void>() {
			public Void run() {
				AbstractJschTask.this.run();
				return null;
			}
		};
	}

	static {
		JSch.setLogger(new JschLogger());
	}

	private static class JschLogger implements Logger {
		private final Log log = LogFactory.getLog(JschLogger.class);

		// TODO better support levels
		@Override
		public boolean isEnabled(int level) {
			if (log.isTraceEnabled())
				return true;
			return false;
		}

		@Override
		public void log(int level, String message) {
			log.trace(message);
		}

	}
}
