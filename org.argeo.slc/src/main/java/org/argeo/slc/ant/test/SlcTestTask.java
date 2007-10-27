package org.argeo.slc.ant.test;

import org.apache.tools.ant.BuildException;

import org.argeo.slc.ant.spring.AbstractSpringArg;
import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.core.deploy.DeployedSystem;
import org.argeo.slc.core.test.TestData;
import org.argeo.slc.core.test.TestDefinition;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestRun;

/** Ant task wrapping a test run. */
public class SlcTestTask extends SAwareTask implements TestRun {

	private TestDefinitionArg testDefinitionArg;
	private TestDataArg testDataArg;

	@Override
	public void executeActions(String mode) throws BuildException {
		TestDefinition testDefinition = testDefinitionArg.getTestDefinition();
		testDefinition.execute(this);
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

	public DeployedSystem getDeployedSystem() {
		throw new RuntimeException("Not yet implemented.");
	}

	public TestDefinition getTestDefinition() {
		return testDefinitionArg.getTestDefinition();
	}

	public TestData getTestData() {
		return testDataArg.getTestData();
	}

	public TestResult getTestResult() {
		throw new RuntimeException("Not yet implemented.");
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
