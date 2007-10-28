package org.argeo.slc.ant.test;

import org.apache.tools.ant.BuildException;

import org.argeo.slc.ant.SlcAntConfig;
import org.argeo.slc.ant.spring.AbstractSpringArg;
import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.test.SimpleTestRun;
import org.argeo.slc.core.test.TestData;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestResult;

/** Ant task wrapping a test run. */
public class SlcTestTask extends SAwareTask {

	private TestDefinitionArg testDefinitionArg;
	private TestDataArg testDataArg;

	@Override
	public void executeActions(String mode) throws BuildException {
		SimpleTestRun simpleTestRun = (SimpleTestRun) getContext().getBean(
				getProject().getUserProperty(
						SlcAntConfig.DEFAULT_TEST_RUN_PROPERTY));
		
		if (testDataArg != null)
			simpleTestRun.setTestData(testDataArg.getTestData());
		if (testDefinitionArg != null)
			simpleTestRun.setTestDefinition(testDefinitionArg
					.getTestDefinition());

		TestResult result = simpleTestRun.getTestResult();
		if(result!=null && result instanceof StructureAware){
			((StructureAware)result).notifyCurrentPath(getRegistry(), getPath());
		}
		
		simpleTestRun.execute();
	}

	public TestDefinitionArg createTestDefinition() {
		testDefinitionArg = new TestDefinitionArg();
		addSAwareArg(testDefinitionArg);
		return testDefinitionArg;
	}

	public TestDataArg createTestData() {
		testDataArg = new TestDataArg();
		addSAwareArg(testDataArg);
		return testDataArg;
	}

}

class TestDefinitionArg extends AbstractSpringArg {
	public TestDefinition getTestDefinition() {
		return (TestDefinition) getBeanInstance();
	}
}

class TestDataArg extends AbstractSpringArg {
	public TestData getTestData() {
		return (TestData) getBeanInstance();
	}

}
