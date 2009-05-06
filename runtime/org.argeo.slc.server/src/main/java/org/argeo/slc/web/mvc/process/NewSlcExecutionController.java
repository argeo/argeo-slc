package org.argeo.slc.web.mvc.process;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.msg.MsgConstants;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.oxm.Unmarshaller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.xml.transform.StringSource;

/** Send a new SlcExecution. */
public class NewSlcExecutionController extends AbstractServiceController {
	private final static Log log = LogFactory
			.getLog(NewSlcExecutionController.class);

	private SlcAgentFactory agentFactory;
	private Unmarshaller unmarshaller;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		
		if(log.isDebugEnabled()){
			log.debug("Content-Type: "+request.getContentType());
			log.debug("Content-Length: "+request.getContentLength());
		}

		String agentId = request
				.getParameter(MsgConstants.PROPERTY_SLC_AGENT_ID);

		String answer = request.getParameter("body");
		if (answer == null) {
			// lets read the message body instead
			BufferedReader reader = request.getReader();
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while (((line = reader.readLine())!=null)) {
				buffer.append(line);
				buffer.append("\n");
			}
			answer = buffer.toString();
		}

		if (log.isDebugEnabled())
			log.debug("Received message:\n" + answer);

		StringSource source = new StringSource(answer);
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
