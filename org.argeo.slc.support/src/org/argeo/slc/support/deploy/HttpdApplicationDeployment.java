/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
