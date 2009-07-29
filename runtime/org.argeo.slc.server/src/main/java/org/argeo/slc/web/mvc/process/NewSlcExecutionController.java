package org.argeo.slc.web.mvc.process;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.attachment.AttachmentsStorage;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.msg.MsgConstants;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;
import org.argeo.slc.services.SlcExecutionService;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

/** Send a new SlcExecution. */
public class NewSlcExecutionController extends AbstractServiceController {
	private final static Log log = LogFactory
			.getLog(NewSlcExecutionController.class);

	private SlcAgentFactory agentFactory;
	private Unmarshaller unmarshaller;
	private Marshaller marshaller;
	private SlcExecutionService slcExecutionService;

	private AttachmentsStorage attachmentsStorage;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		if (log.isTraceEnabled()) {
			log.debug("Content-Type: " + request.getContentType());
			log.debug("Content-Length: " + request.getContentLength());
		}

		String agentId = request
				.getParameter(MsgConstants.PROPERTY_SLC_AGENT_ID);
		Assert.notNull(agentId, "agent id");

		String answer = request.getParameter("body");
		if (answer == null) {
			// lets read the message body instead
			BufferedReader reader = request.getReader();
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while (((line = reader.readLine()) != null)) {
				buffer.append(line);
			}
			answer = buffer.toString();
		}

		if (log.isTraceEnabled())
			log.debug("Received message:\n" + answer);

		StringSource source = new StringSource(answer);
		SlcExecution slcExecution = (SlcExecution) unmarshaller
				.unmarshal(source);

		// Workaround for https://www.argeo.org/bugzilla/show_bug.cgi?id=86
		if (slcExecution.getUuid() == null
				|| slcExecution.getUuid().length() < 8)
			slcExecution.setUuid(UUID.randomUUID().toString());

		slcExecution.setStatus(SlcExecution.STATUS_SCHEDULED);
		slcExecution.getSteps().add(
				new SlcExecutionStep(SlcExecutionStep.TYPE_START,
						"Process started from the Web UI"));

		// ObjectList ol = new ObjectList(slcExecution.getRealizedFlows());
		// StringResult result = new StringResult();
		// marshaller.marshal(ol, result);
		// slcExecution.setRealizedFlowsXml(result.toString());
		storeRealizedFlows(slcExecution);

		slcExecutionService.newExecution(slcExecution);

		SlcAgent agent = agentFactory.getAgent(agentId);
		agent.runSlcExecution(slcExecution);
	}

	protected void storeRealizedFlows(SlcExecution slcExecution) {
		Attachment attachment = realizedFlowsAttachment(UUID.randomUUID()
				.toString(), slcExecution);
		InputStream in = null;
		try {

			ObjectList ol = new ObjectList(slcExecution.getRealizedFlows());
			StringResult result = new StringResult();
			marshaller.marshal(ol, result);

			in = new ByteArrayInputStream(result.toString().getBytes());
			attachmentsStorage.storeAttachment(attachment, in);

			slcExecution.setRealizedFlowsXml(attachment.getUuid());

		} catch (Exception e) {
			log.error("Could not store realized flows as attachment #"
					+ attachment.getUuid(), e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	public void setAgentFactory(SlcAgentFactory agentFactory) {
		this.agentFactory = agentFactory;
	}

	public void setSlcExecutionService(SlcExecutionService slcExecutionService) {
		this.slcExecutionService = slcExecutionService;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void setAttachmentsStorage(AttachmentsStorage attachmentsStorage) {
		this.attachmentsStorage = attachmentsStorage;
	}

	/** Unify labelling in the package */
	static Attachment realizedFlowsAttachment(String attachmentUuid,
			SlcExecution slcExecution) {
		return new SimpleAttachment(attachmentUuid,
				"RealizedFlows of SlcExecution #" + slcExecution.getUuid(),
				"text/xml");
	}
}
