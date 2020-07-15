package org.argeo.slc.test;

import org.argeo.slc.deploy.DeployedSystem;

/** Test run whose various components can be externally set. */
public interface WritableTestRun extends ExecutableTestRun {
	public void setDeployedSystem(DeployedSystem deployedSystem);

	public void setTestData(TestData testData);

	public void setTestDefinition(TestDefinition testDefinition);

	public void setTestResult(TestResult testResult);
}
