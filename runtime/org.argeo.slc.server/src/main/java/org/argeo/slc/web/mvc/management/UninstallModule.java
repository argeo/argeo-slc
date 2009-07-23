package org.argeo.slc.web.mvc.management;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.build.BasicNameVersion;
import org.argeo.slc.build.NameVersion;
import org.argeo.slc.deploy.DynamicRuntime;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** . */
public class UninstallModule extends AbstractServiceController {
	private DynamicRuntime<?> dynamicRuntime;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		String name = request.getParameter("name");
		String version = request.getParameter("version");
		NameVersion nameVersion = new BasicNameVersion(name, version);
		dynamicRuntime.uninstallModule(nameVersion);
	}

	public void setDynamicRuntime(DynamicRuntime<?> dynamicRuntime) {
		this.dynamicRuntime = dynamicRuntime;
	}

}
