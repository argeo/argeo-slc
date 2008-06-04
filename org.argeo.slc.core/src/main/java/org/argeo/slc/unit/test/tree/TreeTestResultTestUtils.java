package org.argeo.slc.unit.test.tree;

import java.util.UUID;

import org.argeo.slc.core.build.Distribution;
import org.argeo.slc.core.deploy.DeployedSystem;
import org.argeo.slc.core.deploy.DeploymentData;
import org.argeo.slc.core.deploy.TargetData;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionStep;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.SimpleTestRun;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
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
		SlcExecutionStep step = new SlcExecutionStep("LOG", "JUnit step");
		slcExecution.getSteps().add(step);

		SimpleTestRun testRun = new SimpleTestRun();
		testRun.setUuid(UUID.randomUUID().toString());

		String pathStr = "/test";
		TreeSPath path = new TreeSPath(pathStr);

		TreeSRegistry registry = new TreeSRegistry();
		SimpleSElement elem = new SimpleSElement("Unit Test");
		elem.getTags().put("myTag", "myTagValue");
		registry.register(path, elem);

		TreeTestResult ttr = createSimpleTreeTestResult();
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

		ttr.addResultPart(createSimpleResultPartPassed());
		ttr.addResultPart(createSimpleResultPartFailed());
		ttr.addResultPart(createSimpleResultPartError());
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
