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

import java.util.Map;
import java.util.SortedMap;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.structure.StructureElement;
import org.argeo.slc.test.TestRunDescriptor;

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
