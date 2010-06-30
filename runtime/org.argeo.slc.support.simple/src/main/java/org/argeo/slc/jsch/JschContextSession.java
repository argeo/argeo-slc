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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.jcraft.jsch.Session;

/** Caches a JSCH session in the the ssh target. */
public class JschContextSession extends AbstractJschTask implements
		InitializingBean, DisposableBean {
	private final static Log log = LogFactory.getLog(JschContextSession.class);
	private Boolean autoconnect = false;

	@Override
	void run(Session session) {
		// clear();
		getSshTarget().setSession(session);
		if (log.isDebugEnabled())
			log.debug("Cached SSH context session to " + getSshTarget());
	}

	public void afterPropertiesSet() throws Exception {
		// if (log.isDebugEnabled())
		// log.debug(getClass() + ".afterPropertiesSet(), " + beanName + ", "
		// + this);
		if (autoconnect)
			try {
				run();
			} catch (Exception e) {
				log.error("Could not automatically open session", e);
			}
	}

	public void destroy() throws Exception {
		clear();
	}

	public void clear() {
		SshTarget sshTarget = getSshTarget();
		synchronized (sshTarget) {
			if (sshTarget.getSession() != null) {
				sshTarget.getSession().disconnect();
				sshTarget.setSession(null);
				if (log.isDebugEnabled())
					log.debug("Cleared cached SSH context session to "
							+ getSshTarget());
			}
		}
	}

	public void setAutoconnect(Boolean autoconnect) {
		this.autoconnect = autoconnect;
	}

}
