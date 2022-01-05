package org.argeo.slc.spring;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.argeo.api.cms.CmsAuth;
import org.argeo.api.cms.CmsLog;
import org.argeo.slc.SlcException;

/** Provides base method for executing code with system authorization. */
abstract class AbstractSystemExecution {
	private final static CmsLog log = CmsLog.getLog(AbstractSystemExecution.class);
	private final Subject subject = new Subject();

	/** Authenticate the calling thread */
	protected void authenticateAsSystem() {
		ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		try {
			LoginContext lc = new LoginContext(CmsAuth.LOGIN_CONTEXT_DATA_ADMIN, subject);
			lc.login();
		} catch (LoginException e) {
			throw new SlcException("Cannot login as system", e);
		} finally {
			Thread.currentThread().setContextClassLoader(origClassLoader);
		}
		if (log.isTraceEnabled())
			log.trace("System authenticated");
	}

	protected void deauthenticateAsSystem() {
		ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		try {
			LoginContext lc = new LoginContext(CmsAuth.LOGIN_CONTEXT_DATA_ADMIN, subject);
			lc.logout();
		} catch (LoginException e) {
			throw new SlcException("Cannot logout as system", e);
		} finally {
			Thread.currentThread().setContextClassLoader(origClassLoader);
		}
	}

	protected Subject getSubject() {
		return subject;
	}
}
