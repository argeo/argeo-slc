package org.argeo.slc.lib.linux;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.deploy.LocalFilesDeployment;
import org.argeo.slc.core.deploy.ResourceSet;
import org.argeo.slc.core.execution.tasks.SystemCall;
import org.argeo.slc.jsch.RemoteExec;
import org.argeo.slc.jsch.SshFilesDeployment;
import org.argeo.slc.jsch.SshTarget;

public class RedhatHostManager {

	private SimpleLinuxHost host;

	// SSH
	private Boolean useSsh = true;
	private SshTarget sshTarget = null;

	public void installPackages() {
		StringBuffer cmd = new StringBuffer("yum --nogpgcheck -y install");
		for (String pkg : ((RpmDistribution) host.getDistribution())
				.getAdditionalPackages()) {
			cmd.append(' ').append(pkg);
		}
		executeCommand(cmd.toString());

		RedhatDeploymentData rdd = (RedhatDeploymentData) host
				.getDeploymentData();
		executeCommand(rdd.getRunlevelsScript());
	}

	public void deployConfig() {
		RedhatDeploymentData rdd = (RedhatDeploymentData) host
				.getDeploymentData();
		deploy(rdd.getConfigurationFiles());
		executeCommand(rdd.getPermissionsScript());
	}

	// GENERIC?
	protected void deploy(ResourceSet resourceSet) {
		if (useSsh)
			new SshFilesDeployment(getSshTarget(), resourceSet).run();
		else
			new LocalFilesDeployment(resourceSet).run();

	}

	protected void executeCommand(String command) {
		if (command == null)
			return;

		if (useSsh) {
			RemoteExec rExec = new RemoteExec(getSshTarget(), command);
			rExec.setFailOnBadExitStatus(false);
			rExec.run();
		} else
			new SystemCall(command).run();
	}

	protected SshTarget getSshTarget() {
		if (sshTarget == null)
			throw new SlcException("No SSH target defined");
		return sshTarget;
	}

	public void setHost(SimpleLinuxHost host) {
		this.host = host;
	}

	public void setUseSsh(Boolean useSsh) {
		this.useSsh = useSsh;
	}

	public void setSshTarget(SshTarget sshTarget) {
		this.sshTarget = sshTarget;
	}

}
