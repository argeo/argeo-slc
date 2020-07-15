package org.argeo.slc.lib.jcr;

import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.argeo.jcr.JcrUtils;
import org.argeo.api.NodeUtils;
import org.argeo.slc.SlcException;

/** Backups a JCR repository */
public class JcrRepositoryBackup implements Runnable {
	private final static Log log = LogFactory.getLog(JcrRepositoryBackup.class);

	private String sourceRepo;
	private String sourceDatastore;
	private String targetFile;

	private String sourceWksp;

	private String sourceUsername;
	private char[] sourcePassword;

	private RepositoryFactory repositoryFactory;
	private FileSystemManager fileSystemManager;

	public void run() {
		Session sourceDefaultSession = null;
		try {
			long begin = System.currentTimeMillis();

			FileObject archiveRoot = fileSystemManager.resolveFile(targetFile);
			archiveRoot.createFolder();

			String datastoreFolderName = "datastore";
			if (hasDatastore())
				backupDataStore(archiveRoot.resolveFile(datastoreFolderName));

			Repository sourceRepository = NodeUtils.getRepositoryByUri(
					repositoryFactory, sourceRepo);
			Credentials sourceCredentials = null;
			if (sourceUsername != null)
				sourceCredentials = new SimpleCredentials(sourceUsername,
						sourcePassword);

			sourceDefaultSession = sourceRepository.login(sourceCredentials);
			for (String sourceWorkspaceName : sourceDefaultSession
					.getWorkspace().getAccessibleWorkspaceNames()) {
				if (Thread.interrupted()) {
					log.error("Workspace backup interrupted");
					Thread.currentThread().interrupt();
					return;
				}

				if (sourceWksp != null && !sourceWksp.trim().equals("")
						&& !sourceWorkspaceName.equals(sourceWksp))
					continue;
				Session sourceSession = null;
				JarOutputStream out = null;
				FileObject workspaceBackup = null;
				try {
					Manifest manifest = new Manifest();
					manifest.getMainAttributes().put(
							Attributes.Name.MANIFEST_VERSION, "1.0");
					manifest.getMainAttributes().putValue("Backup-UUID",
							UUID.randomUUID().toString());
					manifest.getMainAttributes().putValue("Backup-Timestamp",
							Long.toString(System.currentTimeMillis()));
					manifest.getMainAttributes().putValue(
							"Backup-JCR-Workspace", sourceWorkspaceName);
					workspaceBackup = fileSystemManager.resolveFile(targetFile
							+ "/" + sourceWorkspaceName + ".jar");

					out = new JarOutputStream(workspaceBackup.getContent()
							.getOutputStream(), manifest);
					sourceSession = sourceRepository.login(sourceCredentials,
							sourceWorkspaceName);
					backupWorkspace(sourceSession, out);
				} finally {
					JcrUtils.logoutQuietly(sourceSession);
					IOUtils.closeQuietly(out);
					if (workspaceBackup != null)
						workspaceBackup.close();
				}
			}

			// in case some binaries have been added during the backup
			if (hasDatastore())
				backupDataStore(archiveRoot.resolveFile(datastoreFolderName));

			long duration = (System.currentTimeMillis() - begin) / 1000;// s
			log.info("Backed-up " + sourceRepo + " in " + (duration / 60)
					+ "min " + (duration % 60) + "s");
		} catch (Exception e) {
			throw new SlcException("Cannot backup " + sourceRepo, e);
		} finally {
			JcrUtils.logoutQuietly(sourceDefaultSession);
		}
	}

	protected Boolean hasDatastore() {
		return sourceDatastore != null && !sourceDatastore.trim().equals("");
	}

	protected void backupWorkspace(Session sourceSession, JarOutputStream out) {
		try {
			if (log.isTraceEnabled())
				log.trace("Backup " + sourceSession.getWorkspace().getName()
						+ "...");
			Boolean skipBinaries = hasDatastore();
			for (NodeIterator it = sourceSession.getRootNode().getNodes(); it
					.hasNext();) {
				if (Thread.interrupted()) {
					log.error("Node backup interrupted");
					Thread.currentThread().interrupt();
					return;
				}
				Node node = it.nextNode();
				JarEntry entry = new JarEntry(node.getPath());
				out.putNextEntry(entry);
				sourceSession.exportSystemView(node.getPath(), out,
						skipBinaries, false);
				out.flush();
				out.closeEntry();
			}
			if (log.isDebugEnabled())
				log.debug("Backed up " + sourceSession.getWorkspace().getName());
		} catch (Exception e) {
			throw new SlcException("Cannot backup "
					+ sourceSession.getWorkspace().getName(), e);
		}
	}

	protected void backupDataStore(final FileObject targetDatastore) {
		try {
			targetDatastore.createFolder();
			final FileObject sourceDataStore = fileSystemManager
					.resolveFile(sourceDatastore);
			if (log.isDebugEnabled())
				log.debug("Backup " + sourceDatastore);
			targetDatastore.copyFrom(sourceDataStore, new FileSelector() {
				public boolean traverseDescendents(FileSelectInfo fileInfo)
						throws Exception {
					return true;
				}

				public boolean includeFile(FileSelectInfo fileInfo)
						throws Exception {
					String relativeName = fileInfo
							.getFile()
							.getName()
							.getPath()
							.substring(
									sourceDataStore.getName().getPath()
											.length());
					FileObject target = targetDatastore
							.resolveFile(relativeName);
					if (target.exists()) {
						return false;
					} else {
						return true;
					}
				}
			});
			if (log.isDebugEnabled())
				log.debug("Backed-up " + sourceDatastore);
		} catch (FileSystemException e) {
			throw new SlcException("Cannot backup datastore", e);
		}
	}

	public void setSourceRepo(String sourceRepo) {
		this.sourceRepo = sourceRepo;
	}

	public void setSourceWksp(String sourceWksp) {
		this.sourceWksp = sourceWksp;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setSourceUsername(String sourceUsername) {
		this.sourceUsername = sourceUsername;
	}

	public void setSourcePassword(char[] sourcePassword) {
		this.sourcePassword = sourcePassword;
	}

	public void setFileSystemManager(FileSystemManager fileSystemManager) {
		this.fileSystemManager = fileSystemManager;
	}

	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}

	public void setSourceDatastore(String sourceDatastore) {
		this.sourceDatastore = sourceDatastore;
	}

}
