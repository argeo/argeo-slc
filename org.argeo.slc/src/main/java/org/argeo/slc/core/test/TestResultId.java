package org.argeo.slc.core.test;

import org.argeo.slc.core.deploy.DeployedSystemId;

/** The unique id referencing a test result.*/
public interface TestResultId {
	public DeployedSystemId getDeployedSystemId();
	
}
