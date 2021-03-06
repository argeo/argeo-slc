package org.argeo.slc.support.deploy;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.deploy.DeployEnvironment;
import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.deploy.Deployment;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.TargetData;

public class HttpdApplicationDeployment implements Deployment {
	private static final Log log = LogFactory
			.getLog(HttpdApplicationDeployment.class);

	private HttpdApplicationTargetData targetData;
	private DeploymentData deploymentData;
	private SimpleHttpdApplication deployedSystem;
	private Distribution distribution;

	private DeployEnvironment deployEnvironment;

	public void run() {
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
