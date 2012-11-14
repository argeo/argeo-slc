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
