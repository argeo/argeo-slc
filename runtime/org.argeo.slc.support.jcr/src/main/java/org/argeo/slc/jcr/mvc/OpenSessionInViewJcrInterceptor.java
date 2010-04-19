package org.argeo.slc.jcr.mvc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

public class OpenSessionInViewJcrInterceptor implements WebRequestInterceptor {
	private final static Log log = LogFactory
			.getLog(OpenSessionInViewJcrInterceptor.class);

	public void preHandle(WebRequest request) throws Exception {
		if (log.isDebugEnabled())
			log.debug("preHandle: " + request);
	}

	public void postHandle(WebRequest request, ModelMap model) throws Exception {
		if (log.isDebugEnabled())
			log.debug("postHandle: " + request);
	}

	public void afterCompletion(WebRequest request, Exception ex)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("afterCompletion: " + request);

	}

}
