/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.services;

import org.argeo.slc.SlcException;
import org.argeo.slc.msg.MsgHandler;
import org.argeo.slc.msg.process.SlcExecutionStatusRequest;
import org.argeo.slc.msg.process.SlcExecutionStepsRequest;
import org.argeo.slc.msg.test.tree.AddTreeTestResultAttachmentRequest;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.process.SlcExecution;

public class ServiceMsgHandler implements MsgHandler {
	private TestManagerService testManagerService;
	private SlcExecutionService slcExecutionService;

	public Object handleMsg(Object msg) {
		if (msg instanceof SlcExecution)
			slcExecutionService.newExecution((SlcExecution) msg);
		else if (msg instanceof SlcExecutionStepsRequest)
			slcExecutionService.addSteps((SlcExecutionStepsRequest) msg);
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
		else if (msg instanceof AddTreeTestResultAttachmentRequest)
			testManagerService
					.addAttachment((AddTreeTestResultAttachmentRequest) msg);
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
