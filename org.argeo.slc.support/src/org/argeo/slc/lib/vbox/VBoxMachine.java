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
