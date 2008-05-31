package org.argeo.slc.ws.test.tree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.test.TestResultListener;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.ws.client.WebServiceUtils;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class WebServiceTreeTestResultNotifier implements
		TestResultListener<TreeTestResult> {
	private WebServiceTemplate template;
	private Boolean onlyOnClose = false;

	private Log log = LogFactory.getLog(getClass());

	private Boolean cannotConnect = false;

	public void resultPartAdded(TreeTestResult testResult,
			TestResultPart testResultPart) {
		if (onlyOnClose)
			return;

		if (cannotConnect)
			return;

		try {
			if (testResult.getResultParts().size() == 1
					&& testResult.getResultParts().values().iterator().next()
							.getParts().size() == 1) {
				CreateTreeTestResultRequest req = new CreateTreeTestResultRequest(
						testResult);

				if (log.isDebugEnabled())
					log.debug("Send create result request for result "
							+ testResult.getUuid());

				WebServiceUtils.marshalSendAndReceive(template, req);
			} else {
				ResultPartRequest req = new ResultPartRequest(testResult);

				if (log.isDebugEnabled())
					log.debug("Send result parts for result "
							+ testResult.getUuid());

				WebServiceUtils.marshalSendAndReceive(template, req);
			}
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);

		} catch (WebServiceIOException e) {
			manageIoException(e);
		}
	}

	public void close(TreeTestResult testResult) {
		if (cannotConnect)
			return;

		try {
			if (onlyOnClose) {
				CreateTreeTestResultRequest req = new CreateTreeTestResultRequest(
						testResult);

				if (log.isDebugEnabled())
					log.debug("Send create result request for result "
							+ testResult.getUuid());

				WebServiceUtils.marshalSendAndReceive(template, req);
			} else {
				CloseTreeTestResultRequest req = new CloseTreeTestResultRequest(
						testResult);

				if (log.isDebugEnabled())
					log.debug("Send close result request for result "
							+ testResult.getUuid());

				WebServiceUtils.marshalSendAndReceive(template, req);

			}
		} catch (SoapFaultClientException e) {
			WebServiceUtils.manageSoapException(e);
		} catch (WebServiceIOException e) {
			manageIoException(e);
		}
	}

	public void setTemplate(WebServiceTemplate template) {
		this.template = template;
	}

	public void setOnlyOnClose(Boolean onlyOnClose) {
		this.onlyOnClose = onlyOnClose;
	}

	protected void manageIoException(WebServiceIOException e) {
		if (!cannotConnect) {
			log.error("Cannot connect to " + template.getDefaultUri()
					+ ". Won't try again.", e);
			cannotConnect = true;
		}
	}
}
