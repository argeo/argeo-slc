package org.argeo.slc.core.test.tree;

import java.util.Date;

import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionStep;
import org.argeo.slc.core.process.SlcExecutionTestUtils;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.core.test.NumericTRId;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestStatus;

public abstract class TreeTestResultTestUtils {

	public static TreeTestResult createSimpleTreeTestResult() {
		TreeTestResult treeTestResult = new TreeTestResult();
		treeTestResult.setNumericResultId(new NumericTRId(System
				.currentTimeMillis()));
		treeTestResult.setCloseDate(new Date());
		return treeTestResult;
	}

	public static TreeTestResult createCompleteTreeTestResult() {
		SlcExecution slcExecution = SlcExecutionTestUtils
				.createSimpleSlcExecution();
		SlcExecutionStep step = new SlcExecutionStep("LOG", "JUnit step");
		slcExecution.getSteps().add(step);

		String pathStr = "/test";
		TreeSPath path = TreeSPath.parseToCreatePath(pathStr);

		TreeSRegistry registry = new TreeSRegistry();
		registry.register(path, new SimpleSElement("Unit Test"));

		TreeTestResult ttr = createSimpleTreeTestResult();
		ttr.notifySlcExecution(slcExecution);
		ttr.notifyCurrentPath(registry, path);

		ttr.addResultPart(createSimpleResultPartPassed());
		ttr.addResultPart(createSimpleResultPartFailed());
		ttr.addResultPart(createSimpleResultPartError());
		return ttr;
	}

	public static SimpleResultPart createSimpleResultPartPassed() {
		SimpleResultPart partPassed = new SimpleResultPart();
		String msgPassed = "message";
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
		String msgFailed = "crashed";
		partFailed.setStatus(TestStatus.ERROR);
		partFailed.setMessage(msgFailed);
		partFailed.setException(new Exception("Test Exception"));
		return partFailed;
	}

	private TreeTestResultTestUtils() {

	}
}
