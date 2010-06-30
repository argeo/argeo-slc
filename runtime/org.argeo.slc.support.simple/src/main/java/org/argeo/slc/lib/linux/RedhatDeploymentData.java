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

package org.argeo.slc.lib.linux;

import org.argeo.slc.core.deploy.MultiResourceSet;
import org.argeo.slc.core.deploy.ResourceSet;
import org.argeo.slc.deploy.DeploymentData;

public class RedhatDeploymentData implements DeploymentData {
	private ResourceSet configurationFiles;
	private String runlevelsScript;
	private String permissionsScript;

	private RedhatDeploymentData parent;

	public ResourceSet getConfigurationFiles() {
		if (parent != null && parent.getConfigurationFiles() != null) {
			MultiResourceSet mrs = new MultiResourceSet();
			mrs.getResourceSets().add(parent.getConfigurationFiles());
			mrs.getResourceSets().add(configurationFiles);
			return mrs;
		} else {
			return configurationFiles;
		}
	}

	public String getRunlevelsScript() {
		if (parent != null && parent.getRunlevelsScript() != null)
			return parent.getRunlevelsScript() + "\n" + runlevelsScript;
		else
			return runlevelsScript;
	}

	public String getPermissionsScript() {
		if (parent != null && parent.getPermissionsScript() != null)
			return parent.getPermissionsScript() + "\n" + permissionsScript;
		else
			return permissionsScript;
	}

	public void setRunlevelsScript(String runlevelsScript) {
		this.runlevelsScript = runlevelsScript;
	}

	public void setConfigurationFiles(ResourceSet configurationFiles) {
		this.configurationFiles = configurationFiles;
	}

	public void setPermissionsScript(String permissionsScript) {
		this.permissionsScript = permissionsScript;
	}

	public void setParent(RedhatDeploymentData parentDeploymentData) {
		this.parent = parentDeploymentData;
	}

}
