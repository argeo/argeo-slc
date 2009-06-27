package org.argeo.slc.web.mvc.provisioning;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.web.HttpRequestHandler;

public class PluginHandler implements HttpRequestHandler {
	private final static Log log = LogFactory.getLog(PluginHandler.class);

	private FileProvider provider;

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// log.debug(request.getContextPath());
		// log.debug(request.getServletPath());
		// log.debug(request.getPathInfo());
		// log.debug(request.getPathTranslated());
		// log.debug(request.getLocalName());
		// log.debug(request.getLocalAddr());
		// log.debug(request.getQueryString());
		// log.debug(request.getRequestURL());
		// log.debug(request.getRequestURI());

		String path = request.getPathInfo();

		if (log.isDebugEnabled())
			log.debug("Request " + path);

		StringTokenizer stS = new StringTokenizer(path, "/");
		String distribution = stS.nextToken();
		stS.nextToken();// plugins
		String fileName = stS.nextToken();

		int ind_ = fileName.indexOf('_');
		String moduleName;
		if (ind_ > -1)
			moduleName = fileName.substring(0, ind_);
		else
			throw new SlcException("Cannot determine version for " + fileName);

		String versionAndExtension = fileName.substring(ind_ + 1);
		int indExt = versionAndExtension.lastIndexOf('.');
		String moduleVersion = versionAndExtension.substring(0, indExt);

		provider.read(distribution, moduleName, moduleVersion, response
				.getOutputStream());
	}

	public void setProvider(FileProvider provider) {
		this.provider = provider;
	}

}
