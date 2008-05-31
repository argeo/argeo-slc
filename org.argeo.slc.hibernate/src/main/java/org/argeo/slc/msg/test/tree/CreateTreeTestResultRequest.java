package org.argeo.slc.msg.test.tree;

import org.argeo.slc.core.test.tree.TreeTestResult;

public class CreateTreeTestResultRequest {
	private TreeTestResult treeTestResult;

	public CreateTreeTestResultRequest() {

	}

	public CreateTreeTestResultRequest(TreeTestResult treeTestResult) {
		this.treeTestResult = treeTestResult;
	}

	public TreeTestResult getTreeTestResult() {
		return treeTestResult;
	}

	public void setTreeTestResult(TreeTestResult treeTestResult) {
		this.treeTestResult = treeTestResult;
	}
}
