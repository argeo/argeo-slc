package org.argeo.slc.jsch;

import java.util.Map;

import org.argeo.slc.core.deploy.RelativeResourceSet;
import org.springframework.core.io.Resource;

public class SshFilesDeployment implements Runnable {
	private String targetBase = "";
	private RelativeResourceSet resourceSet;
	private SshTarget sshTarget;

	public void run() {
		JschMultiTasks multiTasks = new JschMultiTasks();
		multiTasks.setSshTarget(sshTarget);

		Map<String, Resource> resources = resourceSet.listResources();
		for (String relPath : resources.keySet()) {
			Resource resource = resources.get(relPath);
			ScpTo scpTo = new ScpTo();
			scpTo.setLocalResource(resource);
			scpTo.setRemotePath(targetBase + "/" + relPath);
			multiTasks.getTasks().add(scpTo);
		}

		multiTasks.run();
	}

	public void setTargetBase(String targetBase) {
		this.targetBase = targetBase;
	}

	public void setResourceSet(RelativeResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

	public void setSshTarget(SshTarget sshTarget) {
		this.sshTarget = sshTarget;
	}

}
