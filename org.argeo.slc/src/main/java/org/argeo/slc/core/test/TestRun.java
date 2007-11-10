package org.argeo.slc.core.test;

import org.argeo.slc.core.deploy.DeployedSystem;

/** The actual run of a test */
public interface TestRun {
	/** Gets the related test definition. */
	public TestDefinition getTestDefinition();

	/** Gets the related test data */
	public TestData getTestData();

	/** Gets the related deployed system. */
	public DeployedSystem getDeployedSystem();

	/** Gets the related result where to record results. */
	public TestResult getTestResult();
}
