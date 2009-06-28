package org.argeo.slc.web.mvc.provisioning;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.SlcException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class BundleJarInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String path = request.getPathInfo();
		StringTokenizer stS = new StringTokenizer(path, "/");
		String fileName = null;
		while (stS.hasMoreTokens()) {
			String token = stS.nextToken();
			if (!stS.hasMoreTokens()) {
				fileName = token;
			}
		}

		int ind_ = fileName.indexOf('-');
		String moduleName;
		if (ind_ > -1)
			moduleName = fileName.substring(0, ind_);
		else
			throw new SlcException("Cannot determine version for " + fileName);

		String versionAndExtension = fileName.substring(ind_ + 1);
		int indExt = versionAndExtension.lastIndexOf('.');
		String moduleVersion = versionAndExtension.substring(0, indExt);

		request.setAttribute("moduleName", moduleName);
		request.setAttribute("moduleVersion", moduleVersion);

		return true;
	}
}
