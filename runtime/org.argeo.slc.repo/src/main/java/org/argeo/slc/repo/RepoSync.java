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
package org.argeo.slc.repo;

import java.util.Calendar;

import javax.jcr.Credentials;
import javax.jcr.ImportUUIDBehavior;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.ArgeoJcrUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/** Sync to from software repositories */
public class RepoSync implements Runnable {
	private final static Log log = LogFactory.getLog(RepoSync.class);

	private String sourceRepo;
	private String targetRepo;

	private String sourceWksp;

	private String sourceUsername;
	private char[] sourcePassword;

	private RepositoryFactory repositoryFactory;

	public void run() {
		Session sourceDefaultSession = null;
		Session targetDefaultSession = null;
		try {
			long begin = System.currentTimeMillis();

			Repository sourceRepository = ArgeoJcrUtils.getRepositoryByUri(
					repositoryFactory, sourceRepo);
			Repository targetRepository = ArgeoJcrUtils.getRepositoryByUri(
					repositoryFactory, targetRepo);
			Credentials sourceCredentials = null;
			if (sourceUsername != null)
				sourceCredentials = new SimpleCredentials(sourceUsername,
						sourcePassword);
			Credentials targetCredentials = null;

			sourceDefaultSession = sourceRepository.login(sourceCredentials);
			targetDefaultSession = targetRepository.login(targetCredentials);
			for (String sourceWorkspaceName : sourceDefaultSession
					.getWorkspace().getAccessibleWorkspaceNames()) {
				Session sourceSession = null;
				Session targetSession = null;
				try {
					try {
						targetSession = targetRepository.login(
								targetCredentials, sourceWorkspaceName);
					} catch (NoSuchWorkspaceException e) {
						targetDefaultSession.getWorkspace().createWorkspace(
								sourceWorkspaceName);
						targetSession = targetRepository.login(
								targetCredentials, sourceWorkspaceName);
					}
					sourceSession = sourceRepository.login(sourceCredentials,
							sourceWorkspaceName);
					syncWorkspace(sourceSession, targetSession);
				} finally {
					JcrUtils.logoutQuietly(sourceSession);
					JcrUtils.logoutQuietly(targetSession);
				}
			}
			// Session sourceSession = sourceRepository.login(sourceCredentials,
			// sourceWksp);
			//
			// Credentials targetCredentials = null;
			// Session targetSession = targetRepository.login(targetCredentials,
			// sourceWksp);
			//
			// Long count = JcrUtils.copyFiles(sourceSession.getRootNode(),
			// targetSession.getRootNode(), true, null);

			long duration = (System.currentTimeMillis() - begin) / 1000;// s
			log.info("Sync " + sourceRepo + " to " + targetRepo + " in "
					+ (duration / 60)

					+ "min " + (duration % 60) + "s");
		} catch (RepositoryException e) {
			throw new SlcException("Cannot sync " + sourceRepo + " to "
					+ targetRepo, e);
		} finally {
			JcrUtils.logoutQuietly(sourceDefaultSession);
			JcrUtils.logoutQuietly(targetDefaultSession);
		}
	}

	protected void syncWorkspace(Session sourceSession, Session targetSession) {
		try {
			if (log.isDebugEnabled())
				log.debug("Syncing " + sourceSession.getWorkspace().getName()
						+ "...");
			for (NodeIterator it = sourceSession.getRootNode().getNodes(); it
					.hasNext();) {
				Node node = it.nextNode();
				if (node.getName().equals("jcr:system"))
					continue;
				// ContentHandler targetHandler = targetSession
				// .getWorkspace()
				// .getImportContentHandler(
				// "/",
				// ImportUUIDBehavior.IMPORT_UUID_COLLISION_REMOVE_EXISTING);
				// sourceSession.exportSystemView(node.getPath(), targetHandler,
				// true, false);
				// if (log.isDebugEnabled())
				// log.debug(" " + node.getPath());
				syncNode(node, targetSession.getRootNode());
			}
			if (log.isDebugEnabled())
				log.debug("Synced " + sourceSession.getWorkspace().getName());
		} catch (Exception e) {
			throw new SlcException("Cannot sync "
					+ sourceSession.getWorkspace().getName() + " to "
					+ targetSession.getWorkspace().getName(), e);
		}
	}

	protected void syncNode(Node sourceNode, Node targetParentNode)
			throws RepositoryException, SAXException {
		Boolean noRecurse = noRecurse(sourceNode);
		if (!targetParentNode.hasNode(sourceNode.getName())) {
			ContentHandler contentHandler = targetParentNode
					.getSession()
					.getWorkspace()
					.getImportContentHandler(targetParentNode.getPath(),
							ImportUUIDBehavior.IMPORT_UUID_COLLISION_THROW);
			sourceNode.getSession().exportSystemView(sourceNode.getPath(),
					contentHandler, false, noRecurse);
			if (log.isDebugEnabled())
				log.debug("Add " + sourceNode.getPath());
		} else {
			Node targetNode = targetParentNode.getNode(sourceNode.getName());
			if (sourceNode.isNodeType(NodeType.MIX_LAST_MODIFIED)) {
				Calendar sourceLastModified = sourceNode.getProperty(
						Property.JCR_LAST_MODIFIED).getDate();
				Calendar targetLastModified = null;
				if (targetNode.isNodeType(NodeType.MIX_LAST_MODIFIED)) {
					targetLastModified = targetNode.getProperty(
							Property.JCR_LAST_MODIFIED).getDate();
				}

				if (targetLastModified == null
						|| targetLastModified.before(sourceLastModified)) {
					ContentHandler contentHandler = targetParentNode
							.getSession()
							.getWorkspace()
							.getImportContentHandler(
									targetParentNode.getPath(),
									ImportUUIDBehavior.IMPORT_UUID_COLLISION_REMOVE_EXISTING);
					sourceNode.getSession().exportSystemView(
							sourceNode.getPath(), contentHandler, false,
							noRecurse);
					if (log.isDebugEnabled())
						log.debug("Update " + targetNode.getPath());
				} else {
					if (log.isDebugEnabled())
						log.debug("Skip up to date " + targetNode.getPath());
				}
			}
		}

		if (noRecurse) {
			// recurse
			Node targetNode = targetParentNode.getNode(sourceNode.getName());
			for (NodeIterator it = sourceNode.getNodes(); it.hasNext();) {
				syncNode(it.nextNode(), targetNode);
			}
		}

	}

	protected Boolean noRecurse(Node sourceNode) throws RepositoryException {
		if (sourceNode.isNodeType(NodeType.NT_FILE))
			return false;
		return true;
	}

	public void setSourceRepo(String sourceRepo) {
		this.sourceRepo = sourceRepo;
	}

	public void setTargetRepo(String targetRepo) {
		this.targetRepo = targetRepo;
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
}
