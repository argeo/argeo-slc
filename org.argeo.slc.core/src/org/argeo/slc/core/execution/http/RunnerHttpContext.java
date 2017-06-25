package org.argeo.slc.core.execution.http;

import java.io.IOException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.cms.auth.HttpRequestCallbackHandler;
import org.argeo.node.NodeConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.http.HttpContext;

class RunnerHttpContext implements HttpContext {
	final static String HEADER_WWW_AUTHENTICATE = "WWW-Authenticate";
	private final static Log log = LogFactory.getLog(RunnerHttpContext.class);

	private final BundleContext bc = FrameworkUtil.getBundle(getClass()).getBundleContext();

	private final String httpAuthRealm;

	public RunnerHttpContext(String httpAuthrealm) {
		this.httpAuthRealm = httpAuthrealm;
	}

	@Override
	public boolean handleSecurity(final HttpServletRequest request, HttpServletResponse response) throws IOException {
		LoginContext lc;
		try {
			lc = new LoginContext(NodeConstants.LOGIN_CONTEXT_USER, new HttpRequestCallbackHandler(request, response));
			lc.login();
		} catch (LoginException e) {
			lc = processUnauthorized(request, response);
			if (lc == null)
				return false;
		}
		Subject.doAs(lc.getSubject(), new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				request.setAttribute(REMOTE_USER, AccessController.getContext());
				return null;
			}

		});

		return true;
	}

	@Override
	public URL getResource(String name) {
		return bc.getBundle().getResource(name);
	}

	@Override
	public String getMimeType(String name) {
		return null;
	}

	protected LoginContext processUnauthorized(HttpServletRequest request, HttpServletResponse response) {
		askForWwwAuth(request, response);
		return null;
	}

	protected void askForWwwAuth(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(401);
		// if (org.argeo.cms.internal.kernel.Activator.getAcceptorCredentials()
		// != null && !forceBasic)// SPNEGO
		// response.setHeader(HttpUtils.HEADER_WWW_AUTHENTICATE, "Negotiate");
		// else
		response.setHeader(HEADER_WWW_AUTHENTICATE, "Basic realm=\"" + httpAuthRealm + "\"");

	}

}
