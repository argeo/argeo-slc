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

package org.argeo.slc.core.test.tree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestStatus;

/**
 * Listener logging tree-based test results to the underlying logging system.
 * 
 * @see TreeTestResult
 * 
 */
public class TreeTestResultLogger implements TreeTestResultListener {

	private static Log log = LogFactory.getLog(TreeTestResultLogger.class);

	private Boolean logExceptionMessages = false;

	public void resultPartAdded(TreeTestResult testResult,
			TestResultPart testResultPart) {
		String msg = testResultPart + " - " + testResult.getUuid() + ":"
				+ testResult.getCurrentPath();
		if (testResultPart.getStatus().equals(TestStatus.PASSED)) {
			log.info(msg);
		} else if (testResultPart.getStatus().equals(TestStatus.FAILED)) {
			log.warn(msg);
		} else if (testResultPart.getStatus().equals(TestStatus.ERROR)) {
			if (logExceptionMessages)
				msg = msg + "\n" + testResultPart.getExceptionMessage();

			log.error(msg);

			if (!logExceptionMessages || log.isDebugEnabled())
				log.debug(testResultPart.getExceptionMessage());

		} else {
			log.error("Unknow test status: " + msg);
		}
	}

	public void close(TreeTestResult testResult) {
		log.info("Test result " + testResult.getUuid() + " closed.");
	}

	public void setLogExceptionMessages(Boolean logExceptionMessages) {
		this.logExceptionMessages = logExceptionMessages;
	}

	public void addAttachment(TreeTestResult treeTestResult,
			Attachment attachment) {
		if (log.isDebugEnabled())
			log.debug("Attachment " + attachment + " added to "
					+ treeTestResult.getUuid());
	}

}
