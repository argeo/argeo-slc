package org.argeo.slc.core.test;

import org.argeo.slc.core.deploy.DeployedSystem;
import org.argeo.slc.core.process.SlcExecutionAware;

/** The actual run of a test */
public interface TestRun extends SlcExecutionAware{
	/** Gets UUID */
	public String getUuid();

	/** Gets the related test definition. */
	public <T extends TestDefinition> T getTestDefinition();

	/** Gets the related test data */
	public <T extends TestData> T getTestData();

	/** Gets the related deployed system. */
	public <T extends DeployedSystem> T getDeployedSystem();

	/** Gets the related result where to record results. */
	public <T extends TestResult> T getTestResult();
}
