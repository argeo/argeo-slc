package org.argeo.slc.core.test;

import org.argeo.slc.core.deploy.DeployedSystem;

/** The actual run of a test */
public interface TestRun {
	public TestDefinition getTestDefinition();

	public TestData getTestData();

	public DeployedSystem getDeployedSystem();

	public TestResult getTestResult();
}
