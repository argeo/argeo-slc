/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
import java.net.URL;

import org.argeo.slc.build.Distribution;
import org.argeo.slc.deploy.DeploymentData;

public class SimpleHttpdApplication implements WebApplication {
	private HttpdApplicationTargetData targetData;
	private Distribution distribution;

	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

	public URL getBaseUrl() {
		return targetData.getTargetBaseUrl();
	}

	public File getRootLocation() {
		return targetData.getTargetRootLocation();
	}

	public String getDeployedSystemId() {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpdApplicationTargetData getTargetData() {
		return targetData;
	}

	public void setTargetData(HttpdApplicationTargetData targetData) {
		this.targetData = targetData;
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public DeploymentData getDeploymentData() {
		// TODO Auto-generated method stub
		return null;
	}

}
