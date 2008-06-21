package org.argeo.slc.example.appli;

import org.argeo.slc.ant.AntExecutionContext;
import org.argeo.slc.cli.DefaultSlcRuntime;
import org.argeo.slc.cli.SlcMain;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.runtime.SlcExecutionOutput;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

import static org.argeo.slc.unit.test.tree.UnitTestTreeUtil.assertPart;

import junit.framework.TestCase;

public class SubCategory2Test extends TestCase implements
		SlcExecutionOutput<AntExecutionContext> {
	public void testSimpleRun() {
		new DefaultSlcRuntime().executeScript(
				"exampleSlcAppli/root/Category1/SubCategory2/build.xml", this);
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
