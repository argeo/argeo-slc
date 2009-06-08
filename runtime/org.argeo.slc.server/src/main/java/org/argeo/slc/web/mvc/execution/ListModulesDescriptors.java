package org.argeo.slc.web.mvc.execution;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** . */
public class ListModulesDescriptors extends AbstractServiceController {
	private SlcAgentFactory agentFactory;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		// TODO: use centralized agentId property (from MsgConstants)?
		String agentId = request.getParameter("agentId");
		
		SlcAgent slcAgent = agentFactory.getAgent(agentId);

		List<ExecutionModuleDescriptor> descriptors = slcAgent.listExecutionModuleDescriptors();

		modelAndView.addObject(new ObjectList(descriptors));
	}

	public void setAgentFactory(SlcAgentFactory agentFactory) {
		this.agentFactory = agentFactory;
	}

}
