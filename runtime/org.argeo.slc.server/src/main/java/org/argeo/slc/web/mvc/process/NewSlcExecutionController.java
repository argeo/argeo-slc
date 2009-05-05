package org.argeo.slc.web.mvc.process;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import org.argeo.slc.msg.MsgConstants;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.oxm.Unmarshaller;
import org.springframework.web.servlet.ModelAndView;

/** Send a new SlcExecution. */
public class NewSlcExecutionController extends AbstractServiceController {
	private SlcAgentFactory agentFactory;
	private Unmarshaller unmarshaller;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String agentId = request
				.getParameter(MsgConstants.PROPERTY_SLC_AGENT_ID);

		StreamSource source = new StreamSource(request.getInputStream());
		SlcExecution slcExecution = (SlcExecution) unmarshaller
				.unmarshal(source);

		SlcAgent agent = agentFactory.getAgent(agentId);
		agent.runSlcExecution(slcExecution);
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	public void setAgentFactory(SlcAgentFactory agentFactory) {
		this.agentFactory = agentFactory;
	}

}
