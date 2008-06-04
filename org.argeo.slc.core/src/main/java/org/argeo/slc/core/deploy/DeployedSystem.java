package org.argeo.slc.core.deploy;

import org.argeo.slc.core.build.Distribution;

/** An instance of a software system. */
public interface DeployedSystem<DISTRIBUTION extends Distribution, TARGET_DATA extends TargetData>
		extends TargetData {
	public String getDeployedSystemId();

	public DISTRIBUTION getDistribution();

	public DeploymentData getDeploymentData();

	public TARGET_DATA getTargetData();
}
