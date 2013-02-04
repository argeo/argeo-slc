/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.lib.jcr;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

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
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.argeo.jcr.ArgeoJcrUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;

/** Backups a JCR repository */
public class JcrRepositoryBackup implements Runnable {
	private final static Log log = LogFactory.getLog(JcrRepositoryBackup.class);

	private String sourceRepo;
	private String targetFile;

	private String sourceWksp;

	private String sourceUsername;
	private char[] sourcePassword;

	private RepositoryFactory repositoryFactory;
	private FileSystemManager fileSystemManager;

	public void run() {
		FileObject archiveFo = null;
		Session sourceDefaultSession = null;
		try {
			long begin = System.currentTimeMillis();

			Repository sourceRepository = ArgeoJcrUtils.getRepositoryByUri(
					repositoryFactory, sourceRepo);
			Credentials sourceCredentials = null;
			if (sourceUsername != null)
				sourceCredentials = new SimpleCredentials(sourceUsername,
						sourcePassword);

			archiveFo = fileSystemManager.resolveFile(targetFile);
			FileObject archiveRoot = fileSystemManager
					.createFileSystem(archiveFo);

			Map<String, Exception> errors = new HashMap<String, Exception>();
			sourceDefaultSession = sourceRepository.login(sourceCredentials);
			for (String sourceWorkspaceName : sourceDefaultSession
					.getWorkspace().getAccessibleWorkspaceNames()) {
				if (sourceWksp != null && !sourceWksp.trim().equals("")
						&& !sourceWorkspaceName.equals(sourceWksp))
					continue;
				// if (sourceWorkspaceName.equals("security"))
				// continue;
				// if (sourceWorkspaceName.equals("localrepo"))
				// continue;
				Session sourceSession = null;
				OutputStream out = null;
				try {
					FileObject workspaceXml = archiveRoot
							.getChild(sourceWorkspaceName + ".xml");
					out = workspaceXml.getContent().getOutputStream();
					sourceSession = sourceRepository.login(sourceCredentials,
							sourceWorkspaceName);
					backupWorkspace(sourceSession, out);
					workspaceXml.close();
				} catch (Exception e) {
					errors.put("Could not sync workspace "
							+ sourceWorkspaceName, e);
				} finally {
					JcrUtils.logoutQuietly(sourceSession);
					IOUtils.closeQuietly(out);
				}
			}

			long duration = (System.currentTimeMillis() - begin) / 1000;// s
			log.info("Backed-up " + sourceRepo + " in " + (duration / 60)
					+ "min " + (duration % 60) + "s");

			if (errors.size() > 0) {
				throw new SlcException("Sync failed " + errors);
			}
		} catch (Exception e) {
			throw new SlcException("Cannot backup " + sourceRepo, e);
		} finally {
			JcrUtils.logoutQuietly(sourceDefaultSession);
			if (archiveFo != null)
				try {
					archiveFo.close();
				} catch (FileSystemException e) {
					// silent
				}
		}
	}

	protected void backupWorkspace(Session sourceSession, OutputStream out) {
		try {
			if (log.isDebugEnabled())
				log.debug("Syncing " + sourceSession.getWorkspace().getName()
						+ "...");
			for (NodeIterator it = sourceSession.getRootNode().getNodes(); it
					.hasNext();) {
				Node node = it.nextNode();
				if (node.getName().equals("jcr:system"))
					continue;

				sourceSession
						.exportSystemView(node.getPath(), out, true, false);
				if (log.isDebugEnabled())
					log.debug(" " + node.getPath());
			}
			if (log.isDebugEnabled())
				log.debug("Synced " + sourceSession.getWorkspace().getName());
		} catch (Exception e) {
			throw new SlcException("Cannot backup "
					+ sourceSession.getWorkspace().getName(), e);
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

}
