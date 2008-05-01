package org.argeo.slc.core.deploy;

import org.argeo.slc.core.build.Distribution;

/** An instance of a software system. */
public interface DeployedSystem {
	public String getDeployedSystemId();
	public Distribution getDistribution();
}
