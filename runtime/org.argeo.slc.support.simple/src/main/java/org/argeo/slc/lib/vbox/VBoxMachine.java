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
package org.argeo.slc.lib.vbox;

import org.argeo.slc.UnsupportedException;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.TargetData;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

public class VBoxMachine implements DeployedSystem, BeanNameAware,
		InitializingBean {
	private String deployedSystemId = null;
	private String name;
	private String beanName;

	public String getDeployedSystemId() {
		return deployedSystemId;
	}

	public DeploymentData getDeploymentData() {
		throw new UnsupportedException();
	}

	public Distribution getDistribution() {
		throw new UnsupportedException();
	}

	public TargetData getTargetData() {
		throw new UnsupportedException();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDeployedSystemId(String deployedSystemId) {
		this.deployedSystemId = deployedSystemId;
	}

	public void setBeanName(String beanName) {
		this.beanName = name;
	}

	public void afterPropertiesSet() throws Exception {
		if (name == null)
			name = beanName;
	}

}
