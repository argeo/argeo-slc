package org.argeo.slc.web.mvc.execution;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** . */
public class GetModuleDescriptor extends AbstractServiceController {
	private SlcAgentFactory agentFactory;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String agentId = request.getParameter("agentId");
		String moduleName = request.getParameter("moduleName");
		String version = request.getParameter("version");

		SlcAgent slcAgent = agentFactory.getAgent(agentId);

		ExecutionModuleDescriptor md = slcAgent.getExecutionModuleDescriptor(
				moduleName, version);
		modelAndView.addObject(md);
	}

	public void setAgentFactory(SlcAgentFactory agentFactory) {
		this.agentFactory = agentFactory;
	}

}
