package org.argeo.slc.msg.test.tree;

import java.util.Map;
import java.util.SortedMap;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestRunDescriptor;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;

public class ResultPartRequest {
	private String resultUuid;
	private SimpleResultPart resultPart;
	private TreeSPath path;
	private Map<TreeSPath, StructureElement> relatedElements;
	private TestRunDescriptor testRunDescriptor;
	private Map<String, String> attributes;

	public ResultPartRequest() {

	}

	public ResultPartRequest(TreeTestResult ttr, TreeSPath path,
			SimpleResultPart resultPart) {
		resultUuid = ttr.getUuid();
		this.resultPart = resultPart;
		this.path = (path != null ? path : ttr.getCurrentPath());
		relatedElements = ttr.getRelatedElements(this.path);
		if (ttr.getCurrentTestRun() != null)
			testRunDescriptor = new TestRunDescriptor(ttr.getCurrentTestRun());
		attributes = ttr.getAttributes();
	}

	public ResultPartRequest(TreeTestResult ttr) {
		resultUuid = ttr.getUuid();
		this.path = ttr.getCurrentPath();

		PartSubList lst = ttr.getResultParts().get(path);
		if (lst.getParts().size() < 1) {
			throw new SlcException("Cannot find part for path " + path
					+ " in result " + resultUuid);
		}

		this.resultPart = (SimpleResultPart) lst.getParts().get(
				lst.getParts().size() - 1);
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

	public SimpleResultPart getResultPart() {
		return resultPart;
	}

	public void setResultPart(SimpleResultPart resultPart) {
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

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(SortedMap<String, String> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + resultUuid + " " + path;
	}
}
