package org.argeo.slc.core.test;

import org.argeo.slc.core.deploy.DeployedSystem;

/**
 * A basic bean implementation of a <code>WritableTestRun</code>, holding
 * references to the various parts of a test run.
 */
public class SimpleTestRun implements WritableTestRun, ExecutableTestRun {
	private DeployedSystem deployedSystem;
	private TestData testData;
	private TestDefinition testDefinition;
	private TestResult testResult;

	/** Executes the underlying test definition. */
	public void execute() {
		testDefinition.execute(this);
	}

	public DeployedSystem getDeployedSystem() {
		return deployedSystem;
	}

	public void setDeployedSystem(DeployedSystem deployedSystem) {
		this.deployedSystem = deployedSystem;
	}

	public TestData getTestData() {
		return testData;
	}

	public void setTestData(TestData testData) {
		this.testData = testData;
	}

	public TestDefinition getTestDefinition() {
		return testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

}
