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
package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Credentials;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoMonitor;
import org.argeo.eclipse.ui.EclipseArgeoMonitor;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.util.security.Keyring;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Merge two workspaces
 */
public class MergeWorkspaces extends AbstractHandler {
	private final static Log log = LogFactory.getLog(MergeWorkspaces.class);

	public final static String ID = DistPlugin.ID + ".mergeWorkspaces";
	public final static String DEFAULT_LABEL = "Merge";

	public final static String PARAM_SOURCE_WORKSPACE_NAME = "srcWkspName";
	public final static String PARAM_SOURCE_REPO_PATH = "srcRepoPath";
	public final static String PARAM_TARGET_WORKSPACE_NAME = "targetWkspName";
	public final static String PARAM_TARGET_REPO_PATH = "targetRepoPath";

	// DEPENDENCY INJECTION
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;
	private Repository nodeRepository;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String targetRepoPath = event.getParameter(PARAM_TARGET_REPO_PATH);
		String targetWkspName = event.getParameter(PARAM_TARGET_WORKSPACE_NAME);
		String sourceRepoPath = event.getParameter(PARAM_SOURCE_REPO_PATH);
		String sourceWkspName = event.getParameter(PARAM_SOURCE_WORKSPACE_NAME);

		Session nodeSession = null;
		try {
			nodeSession = nodeRepository.login();
			Node srcRepoNode = nodeSession.getNode(sourceRepoPath);
			Repository srcRepository = RepoUtils.getRepository(
					repositoryFactory, keyring, srcRepoNode);
			Credentials srcCredentials = RepoUtils.getRepositoryCredentials(
					keyring, srcRepoNode);

			Node targetRepoNode = nodeSession.getNode(targetRepoPath);
			Repository targetRepository = RepoUtils.getRepository(
					repositoryFactory, keyring, targetRepoNode);
			Credentials targetCredentials = RepoUtils.getRepositoryCredentials(
					keyring, targetRepoNode);

			// String msg = "Are you sure you want to merge distribution ["
			// + sourceWkspName + "] in distribution [" + targetWkspName
			// + "] ?";
			//
			// boolean result = MessageDialog.openConfirm(
			// HandlerUtil.getActiveShell(event), "Confirm Merge", msg);

			// if (result) {
			// Open sessions here since the background thread
			// won't necessarily be authenticated.
			// Job should close the sessions.
			Session sourceSession = srcRepository.login(srcCredentials,
					sourceWkspName);
			Session targetSession;
			try {
				targetSession = targetRepository.login(targetCredentials,
						targetWkspName);
			} catch (NoSuchWorkspaceException e) {
				Session defaultSession = targetRepository
						.login(targetCredentials);
				try {
					defaultSession.getWorkspace().createWorkspace(
							targetWkspName);
				} catch (Exception e1) {
					throw new SlcException("Cannot create new workspace "
							+ targetWkspName, e);
				} finally {
					JcrUtils.logoutQuietly(defaultSession);
				}
				targetSession = targetRepository.login(targetCredentials,
						targetWkspName);
			}

			Job workspaceMergeJob = new WorkspaceMergeJob(sourceSession,
					targetSession);
			workspaceMergeJob.setUser(true);
			workspaceMergeJob.schedule();
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while merging workspaces.", re);
		} finally {
			JcrUtils.logoutQuietly(nodeSession);
		}
		return null;
	}

	private static class WorkspaceMergeJob extends Job {
		private Session sourceSession;
		private Session targetSession;

		public WorkspaceMergeJob(Session sourceSession, Session targetSession) {
			super("Workspace merge");
			this.sourceSession = sourceSession;
			this.targetSession = targetSession;
		}

		@Override
		protected IStatus run(IProgressMonitor eclipseMonitor) {
			long begin = System.currentTimeMillis();
			try {
				Query countQuery = sourceSession
						.getWorkspace()
						.getQueryManager()
						.createQuery("select file from [nt:file] as file",
								Query.JCR_SQL2);
				QueryResult result = countQuery.execute();
				Long expectedCount = result.getNodes().getSize();
				if (log.isDebugEnabled())
					log.debug("Will copy " + expectedCount + " files...");

				ArgeoMonitor monitor = new EclipseArgeoMonitor(eclipseMonitor);
				eclipseMonitor
						.beginTask("Copy files", expectedCount.intValue());

				Long count = JcrUtils.copyFiles(sourceSession.getRootNode(),
						targetSession.getRootNode(), true, monitor);

				monitor.done();
				long duration = (System.currentTimeMillis() - begin) / 1000;// in
																			// s
				if (log.isDebugEnabled())
					log.debug("Copied " + count + " files in "
							+ (duration / 60) + "min " + (duration % 60) + "s");

				return Status.OK_STATUS;
			} catch (RepositoryException e) {
				return new Status(IStatus.ERROR, DistPlugin.ID, "Cannot merge",
						e);
			} finally {
				JcrUtils.logoutQuietly(sourceSession);
				JcrUtils.logoutQuietly(targetSession);
			}
		}
	}

	/* DEPENDENCY INJECTION */
	public void setNodeRepository(Repository nodeRepository) {
		this.nodeRepository = nodeRepository;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}
}