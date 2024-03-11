package org.argeo.api.slc.test;

import org.argeo.api.slc.deploy.DeployedSystem;

/** Test run whose various components can be externally set. */
public interface WritableTestRun extends ExecutableTestRun {
	public void setDeployedSystem(DeployedSystem deployedSystem);

	public void setTestData(TestData testData);

	public void setTestDefinition(TestDefinition testDefinition);

	public void setTestResult(TestResult testResult);
}
