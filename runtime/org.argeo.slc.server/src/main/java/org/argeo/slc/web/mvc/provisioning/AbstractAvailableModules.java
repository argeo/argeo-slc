package org.argeo.slc.web.mvc.provisioning;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.build.ModularDistribution;
import org.springframework.web.HttpRequestHandler;

/** List of modules for a distribution. */
public abstract class AbstractAvailableModules implements HttpRequestHandler {
	protected abstract void print(Writer out, String baseUrl,
			ModularDistribution md) throws IOException;

	public final void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(getContentType());

		ModularDistribution md = (ModularDistribution) request
				.getAttribute("modularDistribution");

		String baseUrl = "http://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath() + "/"
				+ md.getName() + "/" + md.getVersion() + "/";

		print(response.getWriter(), baseUrl, md);
	}

	public String getContentType() {
		return "text/plain";
	}

}
