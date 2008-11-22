package org.argeo.slc.core.deploy;

import org.argeo.slc.core.build.Distribution;

/** An instance of a software system. */
public interface DeployedSystem extends TargetData {
	public String getDeployedSystemId();

	public Distribution getDistribution();

	public DeploymentData getDeploymentData();

	public TargetData getTargetData();
}
