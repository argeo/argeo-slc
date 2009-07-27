package org.argeo.slc.lib.vbox;

import org.argeo.slc.UnsupportedException;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.TargetData;
import org.springframework.beans.factory.BeanNameAware;

public class VBoxMachine implements DeployedSystem, BeanNameAware {
	private String deployedSystemId = null;
	private String name;

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

	public void setBeanName(String name) {
		this.name = name;
	}

}
