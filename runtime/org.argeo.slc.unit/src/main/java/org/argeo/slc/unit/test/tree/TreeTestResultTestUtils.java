/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.unit.test.tree;

import java.util.UUID;


import org.argeo.slc.build.Distribution;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.SimpleTestRun;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.TargetData;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;
import org.argeo.slc.test.TestStatus;
import org.argeo.slc.unit.process.SlcExecutionTestUtils;

public abstract class TreeTestResultTestUtils {

	public static TreeTestResult createSimpleTreeTestResult() {
		TreeTestResult treeTestResult = new TreeTestResult();
		treeTestResult.setUuid(UUID.randomUUID().toString());
		return treeTestResult;
	}

	public static TreeTestResult createCompleteTreeTestResult() {
		SlcExecution slcExecution = SlcExecutionTestUtils
				.createSimpleSlcExecution();
		SlcExecutionStep step = new SlcExecutionStep("JUnit step");
		slcExecution.getSteps().add(step);

		TreeTestResult ttr = createMinimalConsistentTreeTestResult(slcExecution);

		ttr.addResultPart(createSimpleResultPartPassed());
		ttr.addResultPart(createSimpleResultPartFailed());
		ttr.addResultPart(createSimpleResultPartError());
		return ttr;
	}

	public static TreeTestResult createComplexeTreeTestResult() {
		SlcExecution slcExecution = SlcExecutionTestUtils
				.createSimpleSlcExecution();
		SlcExecutionStep step = new SlcExecutionStep("JUnit step");
		slcExecution.getSteps().add(step);

		TreeTestResult ttr = createMinimalConsistentTreeTestResult(slcExecution);

		ttr.addResultPart(createSimpleResultPartPassed());
		ttr.addResultPart(createSimpleResultPartFailed());
		//ttr.addResultPart(createSimpleResultPartError());
		
		SimpleAttachment sa = new SimpleAttachment(UUID.randomUUID().toString(),"AnAttachment","UTF8");
		SimpleAttachment sa2 = new SimpleAttachment(UUID.randomUUID().toString(),"AnOtherAttachment","UTF8");
		ttr.addAttachment(sa);
		ttr.addAttachment(sa2);
		return ttr;
	}

	public static TreeTestResult createMinimalConsistentTreeTestResult(
			SlcExecution slcExecution) {
		SimpleTestRun testRun = new SimpleTestRun();
		testRun.setUuid(UUID.randomUUID().toString());

		// Doesn't work in hibernate with such a path.
		//String pathStr = "/fileDiff/testcases/issue";
		String pathStr = "/test";
		TreeSPath path = new TreeSPath(pathStr);

		TreeSRegistry registry = new TreeSRegistry();
		SimpleSElement elem = new SimpleSElement("Unit Test");
		elem.getTags().put("myTag", "myTagValue");
		registry.register(path, elem);

		TreeTestResult ttr = createSimpleTreeTestResult();
		ttr.getAttributes().put("testCase", "UNIT");

		// Simulate test run
		ttr.notifyCurrentPath(registry, path);
		ttr.notifyTestRun(testRun);
		testRun.setTestResult(ttr);
		testRun.setDeployedSystem(new DeployedSystem() {
			private String uuid = UUID.randomUUID().toString();

			public String getDeployedSystemId() {
				return uuid;
			}

			public Distribution getDistribution() {
				return null;
			}

			public DeploymentData getDeploymentData() {
				// TODO Auto-generated method stub
				return null;
			}

			public TargetData getTargetData() {
				// TODO Auto-generated method stub
				return null;
			}

		});
		testRun.notifySlcExecution(slcExecution);
		return ttr;
	}

	public static SimpleResultPart createSimpleResultPartPassed() {
		SimpleResultPart partPassed = new SimpleResultPart();
		String msgPassed = "message\nnew line";
		partPassed.setStatus(TestStatus.PASSED);
		partPassed.setMessage(msgPassed);
		return partPassed;
	}

	public static SimpleResultPart createSimpleResultPartFailed() {
		SimpleResultPart partFailed = new SimpleResultPart();
		String msgFailed = "too bad";
		partFailed.setStatus(TestStatus.FAILED);
		partFailed.setMessage(msgFailed);
		return partFailed;
	}

	public static SimpleResultPart createSimpleResultPartError() {
		SimpleResultPart partFailed = new SimpleResultPart();
		String msgFailed = "crashed\nanother line";
		partFailed.setStatus(TestStatus.ERROR);
		partFailed.setMessage(msgFailed);
		partFailed.setException(new Exception("Test Exception"));
		return partFailed;
	}

	public static ResultPartRequest createSimpleResultPartRequest(
			TreeTestResult ttr) {
		TreeSPath path = ttr.getCurrentPath();
		PartSubList lst = ttr.getResultParts().get(path);
		SimpleResultPart part = (SimpleResultPart) lst.getParts().get(2);

		ResultPartRequest req = new ResultPartRequest(ttr, path, part);
		req.setPath(ttr.getCurrentPath());

		return req;
	}

	private TreeTestResultTestUtils() {

	}
}
