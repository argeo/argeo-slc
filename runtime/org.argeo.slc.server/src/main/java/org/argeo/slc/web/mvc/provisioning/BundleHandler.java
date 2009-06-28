package org.argeo.slc.web.mvc.provisioning;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.StreamReadable;
import org.argeo.slc.UnsupportedException;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.build.ModularDistribution;
import org.springframework.web.HttpRequestHandler;

public class BundleHandler implements HttpRequestHandler {
	private final static Log log = LogFactory.getLog(BundleHandler.class);

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();

		if (log.isDebugEnabled())
			log.debug("Bundle jar Requested: " + path);
		response.setContentType("application/java-archive");

		String moduleName = request.getParameter("moduleName");
		if (moduleName == null)
			moduleName = request.getAttribute("moduleName").toString();
		String moduleVersion = request.getParameter("moduleVersion");
		if (moduleVersion == null)
			moduleVersion = request.getAttribute("moduleVersion").toString();

		ModularDistribution modularDistribution = (ModularDistribution) request
				.getAttribute("modularDistribution");
		Distribution distribution = modularDistribution.getModuleDistribution(
				moduleName, moduleVersion);
		if (distribution instanceof StreamReadable)
			IOUtils.copy(((StreamReadable) distribution).getInputStream(),
					response.getOutputStream());
		else
			throw new UnsupportedException("distribution", distribution);
	}
}
