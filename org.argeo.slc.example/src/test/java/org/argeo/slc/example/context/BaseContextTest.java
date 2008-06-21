package org.argeo.slc.example.context;

import org.argeo.slc.ant.AntExecutionContext;
import org.argeo.slc.ant.unit.SlcAntAppliTestCase;
import org.argeo.slc.cli.DefaultSlcRuntime;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.runtime.SlcExecutionOutput;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

import junit.framework.TestCase;

public class BaseContextTest extends SlcAntAppliTestCase {
	public void testExecute() {
		execute("Context/build.xml", "testBaseContext");
	}

	public void postExecution(AntExecutionContext executionContext) {
		TreeTestResult testResult1 = executionContext.getBean("testResult");
		String basePath = "/Context/project/testBaseContext/";
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test0/reference", 0, TestStatus.PASSED,
				"Values matched for key 'reference'");
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test1/reference", 0, TestStatus.PASSED,
				"Values matched for key 'reference'");
		UnitTestTreeUtil.assertPart(testResult1, basePath
				+ "slc.test1/varIntern", 0, TestStatus.PASSED,
				"Values matched for key 'varIntern'");
	}

	@Override
	protected String getRootDir() {
		return "exampleSlcAppli/root";
	}

}
