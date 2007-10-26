package org.argeo.slc.ant.test;

import org.apache.tools.ant.BuildException;

import org.argeo.slc.ant.structure.SAwareArg;
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
		sAwareArgs.add(testDefinitionArg);
		return testDefinitionArg;
	}

	public TestDataArg createTestData() {
		testDataArg = new TestDataArg();
		sAwareArgs.add(testDataArg);
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

class TestDefinitionArg extends SAwareArg {
	private TestDefinition testDefinition;

	public TestDefinition getTestDefinition() {
		if (testDefinition == null) {
			// don't call Spring each time in order not to multi-instantiate
			// prototype
			testDefinition = (TestDefinition) getBeanInstance();
		}
		return testDefinition;
	}
}

class TestDataArg extends SAwareArg {
	private TestData testData;

	public TestData getTestData() {
		if (testData == null) {
			// don't call Spring each time in order not to multi-instantiate
			// prototype
			testData = (TestData) getBeanInstance();
		}
		return testData;
	}

}
