package org.argeo.slc.web.mvc.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.attachment.AttachmentsStorage;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.oxm.Unmarshaller;
import org.springframework.web.servlet.ModelAndView;

/** Lists SLC executions possibly filtering them. */
public class GetSlcExecution extends AbstractServiceController {
	private final static Log log = LogFactory.getLog(GetSlcExecution.class);

	private SlcExecutionDao slcExecutionDao;
	private Unmarshaller unmarshaller;

	private AttachmentsStorage attachmentsStorage;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		String uuid = request.getParameter("uuid");
		SlcExecution slcExecution = slcExecutionDao.getSlcExecution(uuid);

		// StringSource source = new StringSource(slcExecution
		// .getRealizedFlowsXml());
		// ObjectList ol2 = (ObjectList) unmarshaller.unmarshal(source);
		// ol2.fill(slcExecution.getRealizedFlows());
		retrieveRealizedFlows(slcExecution);

		modelAndView.addObject(slcExecution);
	}

	protected void retrieveRealizedFlows(SlcExecution slcExecution) {
		Attachment attachment = NewSlcExecutionController
				.realizedFlowsAttachment(slcExecution.getRealizedFlowsXml(),
						slcExecution);

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		try {
			// TODO: optimize with piped streams
			out = new ByteArrayOutputStream();
			attachmentsStorage.retrieveAttachment(attachment, out);

			byte[] arr = out.toByteArray();
			in = new ByteArrayInputStream(arr);
			StreamSource source = new StreamSource(in);
			ObjectList ol = (ObjectList) unmarshaller.unmarshal(source);
			ol.fill(slcExecution.getRealizedFlows());
		} catch (Exception e) {
			log.error("Could not retrieve realized flows from attachment #"
					+ attachment.getUuid(), e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	public void setSlcExecutionDao(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	public void setAttachmentsStorage(AttachmentsStorage attachmentsStorage) {
		this.attachmentsStorage = attachmentsStorage;
	}

}
