package org.argeo.slc.msg.test.tree;

import java.util.Map;

import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.core.test.TestRunDescriptor;
import org.argeo.slc.core.test.tree.TreeTestResult;

public class ResultPartRequest {
	private String resultUuid;
	private TestResultPart resultPart;
	private TreeSPath path;
	private Map<TreeSPath, StructureElement> relatedElements;
	private TestRunDescriptor testRunDescriptor;

	public ResultPartRequest() {

	}

	public ResultPartRequest(TreeTestResult ttr, TreeSPath path,
			TestResultPart resultPart) {
		resultUuid = ttr.getUuid();
		this.resultPart = resultPart;
		this.path = path;
		relatedElements = ttr.getRelatedElements(path);
		if (ttr.getCurrentTestRun() != null)
			testRunDescriptor = new TestRunDescriptor(ttr.getCurrentTestRun());
	}

	public String getResultUuid() {
		return resultUuid;
	}

	public void setResultUuid(String resultUuid) {
		this.resultUuid = resultUuid;
	}

	public TestResultPart getResultPart() {
		return resultPart;
	}

	public void setResultPart(TestResultPart resultPart) {
		this.resultPart = resultPart;
	}

	public TreeSPath getPath() {
		return path;
	}

	public void setPath(TreeSPath path) {
		this.path = path;
	}

	public TestRunDescriptor getTestRunDescriptor() {
		return testRunDescriptor;
	}

	public void setTestRunDescriptor(TestRunDescriptor testRunDescriptor) {
		this.testRunDescriptor = testRunDescriptor;
	}

	public Map<TreeSPath, StructureElement> getRelatedElements() {
		return relatedElements;
	}

	public void setRelatedElements(
			Map<TreeSPath, StructureElement> relatedElements) {
		this.relatedElements = relatedElements;
	}

}
