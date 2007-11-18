package org.argeo.slc.ant.deploy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;

import org.argeo.slc.ant.SlcAntConfig;
import org.argeo.slc.ant.spring.AbstractSpringArg;
import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.core.deploy.DeploymentData;
import org.argeo.slc.core.deploy.TargetData;
import org.argeo.slc.core.deploy.WritableDeployment;

/** Ant task wrapping a deployment. */
public class SlcDeployTask extends SAwareTask {
	private Log log = LogFactory.getLog(SlcDeployTask.class);

	private String deploymentBean = null;

	private DeploymentDataArg deploymentDataArg;
	private TargetDataArg targetDataArg;

	@Override
	public void executeActions(String mode) throws BuildException {
		WritableDeployment deployment = (WritableDeployment) getContext()
				.getBean(deploymentBean);

		// set overridden references
		if (deploymentDataArg != null) {
			deployment.setDeploymentData(deploymentDataArg.getDeploymentData());
			log.trace("Overrides deployment data");
		}

		if (targetDataArg != null) {
			deployment.setTargetData(targetDataArg.getTargetData());
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
	public DeploymentDataArg createDeploymentData() {
		deploymentDataArg = new DeploymentDataArg();
		return deploymentDataArg;
	}

	/** Creates target data sub tag. */
	public TargetDataArg createTargetData() {
		targetDataArg = new TargetDataArg();
		return targetDataArg;
	}
}

class DeploymentDataArg extends AbstractSpringArg {
	DeploymentData getDeploymentData() {
		return (DeploymentData) getBeanInstance();
	}

}

class TargetDataArg extends AbstractSpringArg {
	TargetData getTargetData() {
		return (TargetData) getBeanInstance();
	}

}
