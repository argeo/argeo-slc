package org.argeo.slc.jsch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.deploy.DigestCheck;
import org.argeo.slc.core.deploy.ResourceSet;
import org.springframework.core.io.Resource;

import com.jcraft.jsch.Session;

public class SshFilesDeployment extends AbstractJschTask implements Runnable {
	private final static Log log = LogFactory.getLog(SshFilesDeployment.class);
	private String targetBase = "";
	private ResourceSet resourceSet;
	/**
	 * Activate with algorithm as per
	 * http://java.sun.com/j2se/1.5.0/docs/guide/security/CryptoSpec.html#AppA
	 */
	private String checksum = "MD5";
	private int remoteChecksumsPerCall = 20;

	@Override
	void run(Session session) {
		JschMultiTasks multiTasks = new JschMultiTasks();

		Map<String, Resource> resources = resourceSet.listResources();

		// Analyze set
		List<String> subDirs = new ArrayList<String>();
		Map<String, String> targetPaths = new HashMap<String, String>();
		for (String relPath : resources.keySet()) {
			String parentDir;
			int lastIndexSubDir = relPath.lastIndexOf('/');
			if (lastIndexSubDir > 0)
				parentDir = targetBase + '/'
						+ relPath.substring(0, lastIndexSubDir);
			else
				parentDir = targetBase;

			boolean skipDir = false;
			registerDirs: for (String registeredDir : new ArrayList<String>(
					subDirs)) {
				if (parentDir.equals(registeredDir)) {
					if (log.isTraceEnabled())
						log.trace("Already registered, skip " + parentDir);
					skipDir = true;
					break registerDirs;
				}

				if (parentDir.startsWith(registeredDir))
					if (subDirs.contains(registeredDir)) {
						subDirs.remove(registeredDir);
						if (log.isTraceEnabled())
							log.trace("Remove parent " + registeredDir + " of "
									+ parentDir);
						continue registerDirs;
					}

				if (registeredDir.startsWith(parentDir)) {
					skipDir = true;
					if (log.isTraceEnabled())
						log.trace("Skip " + parentDir
								+ " because child already registered.");
					break registerDirs;
				}
			}

			if (!subDirs.contains(parentDir) && !skipDir) {
				subDirs.add(parentDir);
			}

			targetPaths.put(relPath, targetBase + "/" + relPath);
		}

		// checksum
		List<String> targetPathsEqualsToLocal = new ArrayList<String>();
		if (checksum != null) {
			Map<String, String> remoteChecksums = new HashMap<String, String>();
			List<String> csLines = new ArrayList<String>();
			String csExecutable;
			if ("MD5".equals(checksum))
				csExecutable = "/usr/bin/md5sum";
			else if ("SHA".equals(checksum))
				csExecutable = "/usr/bin/sha1sum";
			else if ("SHA-256".equals(checksum))
				csExecutable = "/usr/bin/sha256sum";
			else if ("SHA-512".equals(checksum))
				csExecutable = "/usr/bin/sha512sum";
			else
				throw new SlcException(
						"Don't know how to remotely execute checksum "
								+ checksum);

			StringBuffer csCmd = new StringBuffer(csExecutable);
			int numberOfPaths = targetPaths.size();
			int count = 0;
			for (String targetPath : targetPaths.values()) {
				csCmd.append(" ").append(targetPath);
				count++;

				if ((count % remoteChecksumsPerCall == 0)
						|| count == numberOfPaths) {
					RemoteExec remoteCs = new RemoteExec();
					remoteCs.setSshTarget(getSshTarget());
					remoteCs.setCommand(csCmd.toString());
					remoteCs.setStdOutLines(csLines);
					remoteCs.setFailOnBadExitStatus(false);
					remoteCs.run(session);
					csCmd = new StringBuffer(csExecutable);
				}

			}

			remoteChecksums: for (String csLine : csLines) {
				StringTokenizer st = new StringTokenizer(csLine, ": ");
				String cs = st.nextToken();
				if (cs.equals(csExecutable)) {
					// remote does not exist
					continue remoteChecksums;
				} else {
					String targetPath = st.nextToken();
					if (log.isTraceEnabled())
						log.trace("REMOTE: " + targetPath + "=" + cs);
					remoteChecksums.put(targetPath, cs);
				}
			}

			// Local checksums
			for (String relPath : resources.keySet()) {
				Resource resource = resources.get(relPath);
				String targetPath = targetPaths.get(relPath);
				if (remoteChecksums.containsKey(targetPath)) {
					String cs = DigestCheck.digest(checksum, resource);
					if (log.isTraceEnabled())
						log.trace("LOCAL : " + targetPath + "=" + cs);
					if (remoteChecksums.get(targetPath).equals(cs))
						targetPathsEqualsToLocal.add(targetPath);
				}
			}
		}

		// Prepare multitask

		// Create dirs
		StringBuffer mkdirCmd = new StringBuffer("mkdir -p");
		RemoteExec remoteExec = new RemoteExec();
		for (String dir : subDirs) {
			// remoteExec.getCommands().add("mkdir -p " + dir);
			mkdirCmd.append(' ');
			if (dir.indexOf(' ') >= 0)
				mkdirCmd.append('\"').append(dir).append('\"');
			else
				mkdirCmd.append(dir);
		}
		remoteExec.setCommand(mkdirCmd.toString());
		multiTasks.getTasks().add(remoteExec);

		// Perform copies
		int copied = 0;
		int skipped = 0;
		copy: for (String relPath : resources.keySet()) {
			String targetPath = targetPaths.get(relPath);
			if (targetPathsEqualsToLocal.contains(targetPath)) {
				if (log.isTraceEnabled())
					log.trace("Skip copy of " + relPath
							+ " since it is equal to remote " + targetPath);
				skipped++;
				continue copy;
			}
			// Copy resource
			Resource resource = resources.get(relPath);
			ScpTo scpTo = new ScpTo();
			scpTo.setLocalResource(resource);
			scpTo.setRemotePath(targetPath);
			multiTasks.getTasks().add(scpTo);
			copied++;
			// TODO: set permissions
		}

		multiTasks.setSshTarget(getSshTarget());
		multiTasks.run(session);

		if (checksum != null && log.isDebugEnabled())
			log.debug("Copied " + copied + " files, skipped " + skipped
					+ " with same checksum.");
	}

	public void setTargetBase(String targetBase) {
		this.targetBase = targetBase;
	}

	public void setResourceSet(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	/** Number of remote checksums per remote call */
	public void setRemoteChecksumsPerCall(int remoteChecksumsPerCall) {
		this.remoteChecksumsPerCall = remoteChecksumsPerCall;
	}
}
