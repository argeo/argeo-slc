package org.argeo.slc.msg.test.tree;

import org.argeo.slc.core.test.TestRunDescriptor;
import org.argeo.slc.core.test.tree.TreeTestResult;

public class CreateTreeTestResultRequest {
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
