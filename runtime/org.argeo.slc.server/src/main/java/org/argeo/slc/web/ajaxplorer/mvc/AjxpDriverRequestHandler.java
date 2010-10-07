package org.argeo.slc.web.ajaxplorer.mvc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.web.ajaxplorer.AjxpAnswer;
import org.argeo.slc.web.ajaxplorer.AjxpDriver;
import org.springframework.web.HttpRequestHandler;

public class AjxpDriverRequestHandler implements HttpRequestHandler {

	protected final Log log = LogFactory.getLog(getClass());
	private AjxpDriver driver;

	public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		long id = System.currentTimeMillis();
		try {
			if (log.isDebugEnabled())
				logRequest(id, req.getMethod(), req);

			AjxpAnswer answer = driver.executeAction(req);
			answer.updateResponse(resp);

			if (log.isTraceEnabled())
				log.trace(id + " " + req.getMethod() + " completed");
		} catch (Exception e) {
			log.error(id + " Cannot process request.", e);
			throw new ServletException("Cannot process request " + id, e);
		}

	}

	protected void logRequest(long id, String method, HttpServletRequest req) {
		if (log.isDebugEnabled()) {
			StringBuffer buf = new StringBuffer(id + " Received " + method
					+ ": ");
			buf.append('{');
			@SuppressWarnings("unchecked")
			Map<String, String[]> params = req.getParameterMap();
			int count1 = 0;
			for (Map.Entry<String, String[]> entry : params.entrySet()) {
				if (count1 != 0)
					buf.append(", ");
				buf.append(entry.getKey()).append("={");
				int count2 = 0;
				for (String value : entry.getValue()) {
					if (count2 != 0)
						buf.append(',');
					buf.append(value);
					count2++;
				}
				buf.append('}');
				count1++;
			}
			buf.append('}');
			log.debug(buf.toString());
		}
	}

	public void setDriver(AjxpDriver driver) {
		this.driver = driver;
	}

}
