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

package org.argeo.slc.msg.test.tree;

import java.io.Serializable;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.test.TestRunDescriptor;

public class CreateTreeTestResultRequest implements Serializable {
	private static final long serialVersionUID = 7443906609434527687L;
	private TreeTestResult treeTestResult;
	private TestRunDescriptor testRunDescriptor;

	public CreateTreeTestResultRequest() {

	}

	public CreateTreeTestResultRequest(TreeTestResult treeTestResult) {
		this.treeTestResult = treeTestResult;
		if (treeTestResult.getCurrentTestRun() != null)
			testRunDescriptor = new TestRunDescriptor(treeTestResult
					.getCurrentTestRun());
	}

	public TreeTestResult getTreeTestResult() {
		return treeTestResult;
	}

	public void setTreeTestResult(TreeTestResult treeTestResult) {
		this.treeTestResult = treeTestResult;
	}

	public TestRunDescriptor getTestRunDescriptor() {
		return testRunDescriptor;
	}

	public void setTestRunDescriptor(TestRunDescriptor testRunDescriptor) {
		this.testRunDescriptor = testRunDescriptor;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + treeTestResult.getUuid();
	}
}
