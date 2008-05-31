package org.argeo.slc.support.deploy;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.build.Distribution;
import org.argeo.slc.core.deploy.DeployEnvironment;
import org.argeo.slc.core.deploy.DeployedSystem;
import org.argeo.slc.core.deploy.DeploymentData;
import org.argeo.slc.core.deploy.ExecutableDeployment;
import org.argeo.slc.core.deploy.TargetData;

public class HttpdApplicationDeployment implements ExecutableDeployment {
	private static final Log log = LogFactory
			.getLog(HttpdApplicationDeployment.class);

	private HttpdApplicationTargetData targetData;
	private DeploymentData deploymentData;
	private SimpleHttpdApplication deployedSystem;
	private Distribution distribution;

	private DeployEnvironment deployEnvironment;

	public void execute() {
		try {
			deployEnvironment.unpackTo(getDistribution(), targetData
					.getTargetRootLocation(), null);
			
			// FIXME: make it generic
			String deployDataPath = targetData.getTargetRootLocation()
					.getCanonicalPath();
			
			deployEnvironment.unpackTo(getDeploymentData(), new File(
					deployDataPath), null);
			deployedSystem = new SimpleHttpdApplication();
			deployedSystem.setTargetData(targetData);

		} catch (Exception e) {
			throw new SlcException("Cannot deploy " + deploymentData + " to "
					+ targetData, e);
		}

	}

	public void setTargetData(TargetData targetData) {
		this.targetData = (HttpdApplicationTargetData) targetData;
	}

	public void setDeploymentData(DeploymentData deploymentData) {
		this.deploymentData = deploymentData;
	}

	public DeployedSystem getDeployedSystem() {
		return deployedSystem;
	}

	public DeploymentData getDeploymentData() {
		return deploymentData;
	}

	public TargetData getTargetData() {
		return targetData;
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

	public void setDeployEnvironment(DeployEnvironment deployEnvironment) {
		this.deployEnvironment = deployEnvironment;
	}

}
