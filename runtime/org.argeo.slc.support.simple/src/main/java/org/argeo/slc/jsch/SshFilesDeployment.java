package org.argeo.slc.jsch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.argeo.slc.core.deploy.ResourceSet;
import org.springframework.core.io.Resource;

import com.jcraft.jsch.Session;

public class SshFilesDeployment extends AbstractJschTask implements Runnable {
	private String targetBase = "";
	private ResourceSet resourceSet;

	@Override
	void run(Session session) {
		JschMultiTasks multiTasks = new JschMultiTasks();

		List<String> subDirs = new ArrayList<String>();
		Map<String, Resource> resources = resourceSet.listResources();
		for (String relPath : resources.keySet()) {
			// Create dir if necessary
			String dir;
			int lastIndexSubDir = relPath.lastIndexOf('/');
			if (lastIndexSubDir > 0)
				dir = targetBase + '/' + relPath.substring(0, lastIndexSubDir);
			else
				dir = targetBase;

			if (!subDirs.contains(dir)) {
				subDirs.add(dir);
			}

			// Copy resource
			Resource resource = resources.get(relPath);
			ScpTo scpTo = new ScpTo();
			scpTo.setLocalResource(resource);
			scpTo.setRemotePath(targetBase + "/" + relPath);
			multiTasks.getTasks().add(scpTo);

			// TODO: set permissions
		}

		RemoteExec remoteExec = new RemoteExec();
		for (String dir : subDirs) {
			remoteExec.getCommands().add("mkdir -p " + dir);
		}
		multiTasks.getTasks().add(0, remoteExec);

		multiTasks.setSshTarget(getSshTarget());
		multiTasks.run(session);
	}

	public void setTargetBase(String targetBase) {
		this.targetBase = targetBase;
	}

	public void setResourceSet(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}
}
