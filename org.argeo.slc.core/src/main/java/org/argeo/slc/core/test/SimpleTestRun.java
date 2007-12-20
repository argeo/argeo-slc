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

	public <T extends DeployedSystem> T getDeployedSystem() {
		return (T)deployedSystem;
	}

	public void setDeployedSystem(DeployedSystem deployedSystem) {
		this.deployedSystem = deployedSystem;
	}

	public <T extends TestData> T getTestData() {
		return (T)testData;
	}

	public void setTestData(TestData testData) {
		this.testData = testData;
	}

	public <T extends TestDefinition> T getTestDefinition() {
		return (T)testDefinition;
	}

	public void setTestDefinition(TestDefinition testDefinition) {
		this.testDefinition = testDefinition;
	}

	public <T extends TestResult> T getTestResult() {
		return (T)testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

}
