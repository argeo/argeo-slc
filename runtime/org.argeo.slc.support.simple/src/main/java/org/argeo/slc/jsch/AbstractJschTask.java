/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.jsch;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public abstract class AbstractJschTask implements Runnable {
	private final Log log = LogFactory.getLog(getClass());

	private SshTarget sshTarget;

	protected Session openSession() {
		if (sshTarget.getSession() != null) {
			Session session = sshTarget.getSession();
			if (session.isConnected()) {
				if (log.isTraceEnabled())
					log.debug("Using cached session to " + getSshTarget()
							+ " via SSH");
				return session;
			}
		}

		try {
			JSch jsch = new JSch();
			if (sshTarget.getUsePrivateKey()
					&& sshTarget.getLocalPrivateKey().exists())
				jsch.addIdentity(sshTarget.getLocalPrivateKey()
						.getAbsolutePath());
			Session session = jsch.getSession(getSshTarget().getUser(),
					getSshTarget().getHost(), getSshTarget().getPort());

			session.setUserInfo(getSshTarget().getUserInfo());
			session.connect();
			if (log.isDebugEnabled())
				log.debug("Connected to " + getSshTarget() + " via SSH");
			if (sshTarget.getSession() != null) {
				if (log.isDebugEnabled())
					log.debug("The cached session to " + getSshTarget()
							+ " was disconnected and was reset.");
				sshTarget.setSession(session);
			}
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
			if (sshTarget.getSession() == null) {
				session.disconnect();
				if (log.isDebugEnabled())
					log.debug("Disconnected from " + getSshTarget()
							+ " via SSH");
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

}
