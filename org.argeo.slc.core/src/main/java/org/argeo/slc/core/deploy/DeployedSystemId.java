package org.argeo.slc.core.deploy;

import org.argeo.slc.core.build.DistributionId;

/** The id uniquely identifying a deployed system. */
public interface DeployedSystemId {
	public DistributionId getDistributionId();
}
