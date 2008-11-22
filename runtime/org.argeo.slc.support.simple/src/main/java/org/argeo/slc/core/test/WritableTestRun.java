package org.argeo.slc.core.test;

import org.argeo.slc.core.deploy.DeployedSystem;

/** Test run whose various components can be externally set. */
public interface WritableTestRun extends ExecutableTestRun {
	public void setDeployedSystem(DeployedSystem deployedSystem);

	public void setTestData(TestData testData);

	public void setTestDefinition(TestDefinition testDefinition);

	public void setTestResult(TestResult testResult);
}
