package org.argeo.slc.services;

import org.argeo.slc.SlcException;
import org.argeo.slc.msg.MsgHandler;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.services.process.SlcExecutionService;
import org.argeo.slc.services.test.TestManagerService;

public class ServiceMsgHandler implements MsgHandler {
	private TestManagerService testManagerService;
	private SlcExecutionService slcExecutionService;

	public Object handleMsg(Object msg) {
		if (msg instanceof SlcExecution)
			slcExecutionService.newExecution((SlcExecution) msg);
		else if (msg instanceof SlcExecutionStatusRequest)
			slcExecutionService.updateStatus((SlcExecutionStatusRequest) msg);
		else if (msg instanceof CreateTreeTestResultRequest)
			testManagerService
					.createTreeTestResult((CreateTreeTestResultRequest) msg);
		else if (msg instanceof ResultPartRequest)
			testManagerService.addResultPart((ResultPartRequest) msg);
		else if (msg instanceof CloseTreeTestResultRequest)
			testManagerService
					.closeTreeTestResult((CloseTreeTestResultRequest) msg);
		else
			throw new SlcException("Unrecognized message type " + msg);
		return null;
	}

	public void setTestManagerService(TestManagerService testManagerService) {
		this.testManagerService = testManagerService;
	}

	public void setSlcExecutionService(SlcExecutionService slcExecutionService) {
		this.slcExecutionService = slcExecutionService;
	}

}
