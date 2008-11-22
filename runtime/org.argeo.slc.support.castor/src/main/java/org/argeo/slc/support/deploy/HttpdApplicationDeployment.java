package org.argeo.slc.support.deploy;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.build.Distribution;
import org.argeo.slc.core.deploy.DeployEnvironment;
import org.argeo.slc.core.deploy.DeployedSystem;
import org.argeo.slc.core.deploy.Deployment;
import org.argeo.slc.core.deploy.DeploymentData;
import org.argeo.slc.core.deploy.TargetData;

public class HttpdApplicationDeployment implements Deployment {
	private static final Log log = LogFactory
			.getLog(HttpdApplicationDeployment.class);

	private HttpdApplicationTargetData targetData;
	private DeploymentData deploymentData;
	private SimpleHttpdApplication deployedSystem;
	private Distribution distribution;

	private DeployEnvironment deployEnvironment;

	public void execute() {
		try {
			deployEnvironment.unpackTo(distribution, targetData
					.getTargetRootLocation(), null);

			// FIXME: make it generic
			String deployDataPath = targetData.getTargetRootLocation()
					.getCanonicalPath();

			deployEnvironment.unpackTo(deploymentData,
					new File(deployDataPath), null);
			deployedSystem = new SimpleHttpdApplication();
			deployedSystem.setTargetData(targetData);

			log.info("Deployed " + distribution + " to " + targetData);
		} catch (Exception e) {
			throw new SlcException("Cannot deploy " + distribution + " to "
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

	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

	public void setDeployEnvironment(DeployEnvironment deployEnvironment) {
		this.deployEnvironment = deployEnvironment;
	}

}
