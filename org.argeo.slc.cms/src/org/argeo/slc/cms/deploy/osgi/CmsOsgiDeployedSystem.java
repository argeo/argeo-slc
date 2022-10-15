package org.argeo.slc.cms.deploy.osgi;

import org.argeo.slc.build.Distribution;
import org.argeo.slc.build.ModularDistribution;
import org.argeo.slc.cms.deploy.CmsDeployedSystem;
import org.argeo.slc.cms.deploy.CmsDeploymentData;
import org.argeo.slc.cms.deploy.CmsTargetData;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.TargetData;
import org.osgi.framework.BundleContext;

public class CmsOsgiDeployedSystem implements CmsDeployedSystem {
	private ModularDistribution distribution;
	private CmsTargetData targetData;
	private CmsDeploymentData deploymentData;

	private BundleContext systemBundleContext;

	public CmsOsgiDeployedSystem(BundleContext systemBundleContext, ModularDistribution distribution,
			CmsTargetData targetData, CmsDeploymentData deploymentData) {
		this.systemBundleContext = systemBundleContext;

		this.distribution = distribution;
		this.targetData = targetData;
		this.deploymentData = deploymentData;
	}

	@Override
	public String getDeployedSystemId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Distribution getDistribution() {
		return distribution;
	}

	@Override
	public DeploymentData getDeploymentData() {
		return deploymentData;
	}

	@Override
	public TargetData getTargetData() {
		return targetData;
	}

}
