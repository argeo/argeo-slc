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

import org.argeo.slc.msg.test.tree.AddTreeTestResultAttachmentRequest;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.test.TestRunDescriptor;

/** Performs complex business operations. */
public interface TestManagerService {
	/** Performs operations related to the addition of a new test run. */
	public void registerTestRunDescriptor(TestRunDescriptor testRunDescriptor);

	/** Adds a result in a collection based on their ids. */
	public void addResultToCollection(String collectionId, String resultUuid);

	/** Removes a result from a collection based on their ids. */
	public void removeResultFromCollection(String collectionId,
			String resultUuid);

	public void createTreeTestResult(CreateTreeTestResultRequest msg);

	public void addResultPart(ResultPartRequest msg);

	public void addAttachment(AddTreeTestResultAttachmentRequest msg);

	public void closeTreeTestResult(CloseTreeTestResultRequest msg);
}
