package org.argeo.slc.core.execution.http;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.cms.auth.HttpRequestCallbackHandler;
import org.argeo.node.NodeConstants;
import org.osgi.service.http.context.ServletContextHelper;

public class RunnerServletContextHelper extends ServletContextHelper {
	final static String HEADER_WWW_AUTHENTICATE = "WWW-Authenticate";
	private final String httpAuthRealm = "Runner";

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

	protected LoginContext processUnauthorized(HttpServletRequest request, HttpServletResponse response) {
		askForWwwAuth(request, response);
		return null;
	}

	protected void askForWwwAuth(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(401);
		response.setHeader(HEADER_WWW_AUTHENTICATE, "Basic realm=\"" + httpAuthRealm + "\"");

	}

}
