package org.argeo.slc.deploy;

import org.argeo.slc.UnsupportedException;
import org.argeo.slc.build.Distribution;

public abstract class AbstractDeployedSystem implements DeployedSystem {
	public String getDeployedSystemId() {
		// TODO Auto-generated method stub
		return null;
	}

	public DeploymentData getDeploymentData() {
		throw new UnsupportedException("Method not supported");
	}

	public Distribution getDistribution() {
		throw new UnsupportedException("Method not supported");
	}

	public TargetData getTargetData() {
		throw new UnsupportedException("Method not supported");
	}

}
