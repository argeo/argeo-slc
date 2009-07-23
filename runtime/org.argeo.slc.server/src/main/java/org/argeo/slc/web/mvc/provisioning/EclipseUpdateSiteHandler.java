package org.argeo.slc.web.mvc.provisioning;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.build.ModularDistribution;
import org.springframework.web.HttpRequestHandler;

/** An Eclipse update site, serving site.xml features/* and plugins/*. */
public class EclipseUpdateSiteHandler implements HttpRequestHandler {
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ModularDistribution modularDistribution = (ModularDistribution) request
				.getAttribute("modularDistribution");
		response.getWriter().write(
				modularDistribution.getModulesDescriptor("eclipse").toString());
	}
}
