package org.argeo.slc.ant.deploy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.argeo.slc.ant.spring.SpringArg;
import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.deploy.Deployment;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.TargetData;

/** Ant task wrapping a deployment. */
public class SlcDeployTask extends SAwareTask {
	private Log log = LogFactory.getLog(SlcDeployTask.class);

	private String deploymentBean = null;

	private SpringArg<DeploymentData> deploymentDataArg;
	private SpringArg<TargetData> targetDataArg;
	private SpringArg<Distribution> distributionArg;

	@Override
	public void executeActions(String mode) throws BuildException {
		Deployment deployment = (Deployment) getContext().getBean(
				deploymentBean);

		// set overridden references
		if (distributionArg != null) {
			deployment.setDistribution(distributionArg.getInstance());
			log.trace("Overrides distribution");
		}

		if (deploymentDataArg != null) {
			deployment.setDeploymentData(deploymentDataArg.getInstance());
			log.trace("Overrides deployment data");
		}

		if (targetDataArg != null) {
			deployment.setTargetData(targetDataArg.getInstance());
			log.trace("Overrides target data");
		}

		deployment.execute();
	}

	/**
	 * The bean name of the test run to use. If not set the default is used.
	 * 
	 * @see SlcAntConfig
	 */
	public void setDeployment(String deploymentBean) {
		this.deploymentBean = deploymentBean;
	}

	/** Creates deployment data sub tag. */
	public SpringArg<DeploymentData> createDeploymentData() {
		deploymentDataArg = new SpringArg<DeploymentData>();
		return deploymentDataArg;
	}

	/** Creates target data sub tag. */
	public SpringArg<TargetData> createTargetData() {
		targetDataArg = new SpringArg<TargetData>();
		return targetDataArg;
	}

	public SpringArg<Distribution> createDistribution() {
		distributionArg = new SpringArg<Distribution>();
		return distributionArg;
	}
}
