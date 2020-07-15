package org.argeo.cms.ui.workbench.rap;

import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.api.NodeConstants;
import org.argeo.cms.CmsException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * RAP entry point which authenticates the subject as anonymous, for public
 * unauthenticated access.
 */
public class AnonymousEntryPoint implements EntryPoint {
	private final static Log log = LogFactory.getLog(AnonymousEntryPoint.class);

	/**
	 * How many seconds to wait before invalidating the session if the user has
	 * not yet logged in.
	 */
	private Integer sessionTimeout = 5 * 60;

	@Override
	public int createUI() {
		RWT.getRequest().getSession().setMaxInactiveInterval(sessionTimeout);

		// if (log.isDebugEnabled())
		// log.debug("Anonymous THREAD=" + Thread.currentThread().getId()
		// + ", sessionStore=" + RWT.getSessionStore().getId());

		final Display display = PlatformUI.createDisplay();
		Subject subject = new Subject();

		final LoginContext loginContext;
		try {
			loginContext = new LoginContext(NodeConstants.LOGIN_CONTEXT_ANONYMOUS,
					subject);
			loginContext.login();
		} catch (LoginException e1) {
			throw new CmsException("Cannot initialize login context", e1);
		}

		// identify after successful login
		if (log.isDebugEnabled())
			log.debug("Authenticated " + subject);
		final String username = subject.getPrincipals().iterator().next()
				.getName();

		// Logout callback when the display is disposed
		display.disposeExec(new Runnable() {
			public void run() {
				log.debug("Display disposed");
				logout(loginContext, username);
			}
		});

		//
		// RUN THE WORKBENCH
		//
		Integer returnCode = null;
		try {
			returnCode = Subject.doAs(subject, new PrivilegedAction<Integer>() {
				public Integer run() {
					RapWorkbenchAdvisor workbenchAdvisor = new RapWorkbenchAdvisor(
							null);
					int result = PlatformUI.createAndRunWorkbench(display,
							workbenchAdvisor);
					return new Integer(result);
				}
			});
			logout(loginContext, username);
			if (log.isTraceEnabled())
				log.trace("Return code " + returnCode);
		} finally {
			display.dispose();
		}
		return 1;
	}

	private void logout(LoginContext loginContext, String username) {
		try {
			loginContext.logout();
			log.info("Logged out " + (username != null ? username : "")
					+ " (THREAD=" + Thread.currentThread().getId() + ")");
		} catch (LoginException e) {
			log.error("Erorr when logging out", e);
		}
	}
}
