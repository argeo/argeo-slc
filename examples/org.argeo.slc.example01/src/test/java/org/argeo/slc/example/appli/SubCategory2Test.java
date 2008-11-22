package org.argeo.slc.example.appli;

import static org.argeo.slc.unit.test.tree.UnitTestTreeUtil.assertPart;

import org.argeo.slc.ant.AntExecutionContext;
import org.argeo.slc.ant.unit.AntSlcApplicationTestCase;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.tree.TreeTestResult;

public class SubCategory2Test extends AntSlcApplicationTestCase {
	public void testSimpleRun() {
		execute("/Category1/SubCategory2/build.xml");
	}

	public void postExecution(AntExecutionContext executionContext) {
		TreeTestResult testResult1 = executionContext.getBean("testResult");
		assertPart(testResult1,
				"/Category1/SubCategory2/testProject/testComplex/slc.test0/0",
				0, TestStatus.PASSED, null);
		assertPart(testResult1,
				"/Category1/SubCategory2/testProject/testSimple/slc.test0", 1,
				TestStatus.FAILED, null);
		assertPart(testResult1,
				"/Category1/SubCategory2/testProject/testError/slc.test0", 0,
				TestStatus.ERROR, null);

		TreeTestResult testResult2 = executionContext.getBean("testResult2");
		assertPart(testResult2,
				"/Category1/SubCategory2/testProject/testSimple/slc.test2", 1,
				TestStatus.PASSED, null);
		assertPart(testResult2,
				"/Category1/SubCategory2/testProject/testSimple/slc.test3", 1,
				TestStatus.FAILED, null);
	}

}
