package org.argeo.slc.cms.deploy.osgi;

import org.argeo.api.slc.build.Distribution;
import org.argeo.api.slc.build.ModularDistribution;
import org.argeo.api.slc.deploy.DeploymentData;
import org.argeo.api.slc.deploy.TargetData;
import org.argeo.slc.cms.deploy.CmsDeployedSystem;
import org.argeo.slc.cms.deploy.CmsDeploymentData;
import org.argeo.slc.cms.deploy.CmsTargetData;
import org.osgi.framework.BundleContext;

/** A deployed OSGi-based Argeo CMS system. */
public class OsgiCmsDeployedSystem implements CmsDeployedSystem {
	private ModularDistribution distribution;
	private CmsTargetData targetData;
	private CmsDeploymentData deploymentData;

	// private BundleContext systemBundleContext;

	public OsgiCmsDeployedSystem(BundleContext systemBundleContext, ModularDistribution distribution,
			CmsTargetData targetData, CmsDeploymentData deploymentData) {
		// this.systemBundleContext = systemBundleContext;

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
