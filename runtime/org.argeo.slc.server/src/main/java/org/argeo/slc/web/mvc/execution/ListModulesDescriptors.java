package org.argeo.slc.web.mvc.execution;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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

		List<ExecutionModuleDescriptor> descriptors = slcAgent
				.listExecutionModuleDescriptors();
		SortedSet<ExecutionModuleDescriptor> set = new TreeSet<ExecutionModuleDescriptor>(
				new Comparator<ExecutionModuleDescriptor>() {

					public int compare(ExecutionModuleDescriptor md1,
							ExecutionModuleDescriptor md2) {
						String str1 = md1.getLabel() != null ? md1.getLabel()
								: md1.getName();
						String str2 = md2.getLabel() != null ? md2.getLabel()
								: md2.getName();
						return str1.compareTo(str2);
					}
				});
		set.addAll(descriptors);

		modelAndView.addObject(new ObjectList(set));
	}

	public void setAgentFactory(SlcAgentFactory agentFactory) {
		this.agentFactory = agentFactory;
	}

}
