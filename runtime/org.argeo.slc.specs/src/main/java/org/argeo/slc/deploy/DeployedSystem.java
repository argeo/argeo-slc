/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

import org.argeo.slc.build.Distribution;

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
