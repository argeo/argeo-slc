package org.argeo.api.slc.deploy;

import org.argeo.api.slc.build.Distribution;

/** An instance of a software system. */
public interface DeployedSystem extends TargetData {
	/** Unique ID for this system instance. */
	public String getDeployedSystemId();

	/** Underlying packages */
	public Distribution getDistribution();

	/** Data required to initialize the instance (e.g. DB dump, etc.). */
	public DeploymentData getDeploymentData();

	/** Resources required by the system (ports, disk location, etc.) */
	public TargetData getTargetData();
}
