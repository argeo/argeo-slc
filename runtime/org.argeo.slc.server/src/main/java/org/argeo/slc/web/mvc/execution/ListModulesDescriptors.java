package org.argeo.slc.web.mvc.execution;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.execution.ExecutionModule;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** . */
public class ListModulesDescriptors extends AbstractServiceController {
	private ExecutionModulesManager modulesManager;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		List<ExecutionModule> modules = modulesManager.listExecutionModules();

		List<ExecutionModuleDescriptor> descriptors = new ArrayList<ExecutionModuleDescriptor>();
		for (ExecutionModule module : modules) {
			ExecutionModuleDescriptor md = new ExecutionModuleDescriptor();
			md.setName(module.getName());
			md.setVersion(module.getVersion());
			descriptors.add(md);
		}

		modelAndView.addObject(new ObjectList(descriptors));
	}

	public void setModulesManager(ExecutionModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

}
