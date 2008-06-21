package org.argeo.slc.example.context;

import org.argeo.slc.ant.AntExecutionContext;
import org.argeo.slc.ant.unit.SlcAntAppliTestCase;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

public class ContextTest extends SlcAntAppliTestCase {
	public void testExecute() {
		execute("Context/build.xml");
	}

	public void postExecution(AntExecutionContext executionContext) {
		TreeTestResult testResult1 = executionContext.getBean("testResult");
		String basePath = "/Context/project/testContext/";
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test0/reference", 0, TestStatus.PASSED,
				"Values matched for key 'reference'");
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test1/reference2", 0, TestStatus.PASSED,
				"Values matched for key 'reference2'");
		UnitTestTreeUtil.assertPart(testResult1, basePath + "slc.test1/var", 0,
				TestStatus.PASSED, "Values matched for key 'var'");
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test1/varIntern", 0, TestStatus.PASSED,
				"Values matched for key 'varIntern'");
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test1/varExtern", 0, TestStatus.PASSED,
				"Values matched for key 'varExtern'");
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test1/greeting", 0, TestStatus.PASSED,
				"Values matched for key 'greeting'");
	}
}
