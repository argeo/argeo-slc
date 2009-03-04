package org.argeo.slc.web.mvc.execution;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** . */
public class GetModuleDescriptor extends AbstractServiceController {
	private ExecutionModulesManager modulesManager;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String moduleName = request.getParameter("moduleName");
		String version = request.getParameter("version");

		ExecutionModuleDescriptor md = modulesManager
				.getExecutionModuleDescriptor(moduleName, version);
		modelAndView.addObject(md);
	}

	public void setModulesManager(ExecutionModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

}
