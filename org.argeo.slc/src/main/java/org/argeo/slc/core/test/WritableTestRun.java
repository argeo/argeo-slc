package org.argeo.slc.core.test;

import org.argeo.slc.core.deploy.DeployedSystem;

public interface WritableTestRun extends TestRun {
	public void setDeployedSystem(DeployedSystem deployedSystem);

	public void setTestData(TestData testData);

	public void setTestDefinition(TestDefinition testDefinition);

	public void setTestResult(TestResult testResult);
}
