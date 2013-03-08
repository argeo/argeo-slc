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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoMonitor;
import org.argeo.jcr.ArgeoJcrUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/** Sync to from software repositories */
public class RepoSync implements Runnable {
	private final static Log log = LogFactory.getLog(RepoSync.class);

	// Centralizes definition of workspaces that must be ignored by the sync.
	private final static List<String> IGNORED_WSKP_LIST = Arrays.asList(
			"security", "localrepo");

	private final Calendar zero;
	private Session sourceDefaultSession = null;
	private Session targetDefaultSession = null;

	private Repository sourceRepository;
	private Credentials sourceCredentials;
	private Repository targetRepository;
	private Credentials targetCredentials;

	// if Repository and Credentials objects are not explicitly set
	private String sourceRepoUri;
	private String sourceUsername;
	private char[] sourcePassword;
	private String targetRepoUri;
	private String targetUsername;
	private char[] targetPassword;

	private RepositoryFactory repositoryFactory;

	private ArgeoMonitor monitor;
	private List<String> sourceWkspList;

	public RepoSync() {
		zero = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		zero.setTimeInMillis(0);
	}

	/**
	 * 
	 * Shortcut to instantiate a RepoSync with already known repositories and
	 * credentials.
	 * 
	 * @param sourceRepository
	 * @param sourceCredentials
	 * @param targetRepository
	 * @param targetCredentials
	 */
	public RepoSync(Repository sourceRepository, Credentials sourceCredentials,
			Repository targetRepository, Credentials targetCredentials) {
		this();
		this.sourceRepository = sourceRepository;
		this.sourceCredentials = sourceCredentials;
		this.targetRepository = targetRepository;
		this.targetCredentials = targetCredentials;
	}

	public void run() {
		try {
			long begin = System.currentTimeMillis();

			// Setup
			if (sourceRepository == null)
				sourceRepository = ArgeoJcrUtils.getRepositoryByUri(
						repositoryFactory, sourceRepoUri);
			if (sourceCredentials == null && sourceUsername != null)
				sourceCredentials = new SimpleCredentials(sourceUsername,
						sourcePassword);
			sourceDefaultSession = sourceRepository.login(sourceCredentials);

			if (targetRepository == null)
				targetRepository = ArgeoJcrUtils.getRepositoryByUri(
						repositoryFactory, targetRepoUri);
			if (targetCredentials == null && targetUsername != null)
				targetCredentials = new SimpleCredentials(targetUsername,
						targetPassword);
			targetDefaultSession = targetRepository.login(targetCredentials);

			// FIXME implement a cleaner way to compute job size.
			// Compute job size
			if (monitor != null) {
				monitor.beginTask("Computing fetch size...", -1);
				Long totalAmount = 0l;
				if (sourceWkspList != null) {
					for (String wkspName : sourceWkspList) {
						totalAmount += getNodesNumber(wkspName);
					}
				} else
					for (String sourceWorkspaceName : sourceDefaultSession
							.getWorkspace().getAccessibleWorkspaceNames()) {
						totalAmount += getNodesNumber(sourceWorkspaceName);
					}
				monitor.beginTask("Fetch", totalAmount.intValue());

				if (log.isDebugEnabled())
					log.debug("Nb of nodes to sync: " + totalAmount.intValue());
			}

			Map<String, Exception> errors = new HashMap<String, Exception>();
			for (String sourceWorkspaceName : sourceDefaultSession
					.getWorkspace().getAccessibleWorkspaceNames()) {

				if (sourceWkspList != null
						&& !sourceWkspList.contains(sourceWorkspaceName))
					continue;
				if (IGNORED_WSKP_LIST.contains(sourceWorkspaceName))
					continue;

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
				} catch (Exception e) {
					errors.put("Could not sync workspace "
							+ sourceWorkspaceName, e);
					if (log.isDebugEnabled())
						e.printStackTrace();
				} finally {
					JcrUtils.logoutQuietly(sourceSession);
					JcrUtils.logoutQuietly(targetSession);
				}
			}

			if (monitor != null && monitor.isCanceled())
				log.info("Sync has been canceled by user");

			long duration = (System.currentTimeMillis() - begin) / 1000;// s
			log.info("Sync " + sourceRepoUri + " to " + targetRepoUri + " in "
					+ (duration / 60)

					+ "min " + (duration % 60) + "s");

			if (errors.size() > 0) {
				throw new SlcException("Sync failed " + errors);
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot sync " + sourceRepoUri + " to "
					+ targetRepoUri, e);
		} finally {
			JcrUtils.logoutQuietly(sourceDefaultSession);
			JcrUtils.logoutQuietly(targetDefaultSession);
		}
	}

	private long getNodesNumber(String wkspName) {
		if (IGNORED_WSKP_LIST.contains(wkspName))
			return 0l;
		Session sourceSession = null;
		try {
			sourceSession = sourceRepository.login(sourceCredentials, wkspName);
			Query countQuery = sourceDefaultSession
					.getWorkspace()
					.getQueryManager()
					.createQuery("select file from [nt:base] as file",
							Query.JCR_SQL2);
			QueryResult result = countQuery.execute();
			Long expectedCount = result.getNodes().getSize();
			return expectedCount;
		} catch (RepositoryException e) {
			throw new SlcException("Unexpected error while computing "
					+ "the size of the fetch for workspace " + wkspName, e);
		} finally {
			JcrUtils.logoutQuietly(sourceSession);
		}
	}

	protected void syncWorkspace(Session sourceSession, Session targetSession) {
		try {
			String msg = "Synchronizing workspace: "
					+ sourceSession.getWorkspace().getName();
			if (monitor != null)
				monitor.setTaskName(msg);
			if (log.isDebugEnabled())
				log.debug(msg);
			for (NodeIterator it = sourceSession.getRootNode().getNodes(); it
					.hasNext();) {
				Node node = it.nextNode();
				if (node.getName().equals("jcr:system"))
					continue;
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

	/** factorizes monitor management */
	private void updateMonitor(String msg) {
		if (monitor != null) {
			monitor.worked(1);
			monitor.subTask(msg);
		}
	}

	protected void syncNode(Node sourceNode, Node targetParentNode)
			throws RepositoryException, SAXException {

		// enable cancelation of the current fetch process
		// FIXME insure the repository stays in a stable state
		if (monitor != null && monitor.isCanceled()) {
			updateMonitor("Fetched has been canceled, "
					+ "process is terminating");
			return;
		}

		Boolean noRecurse = noRecurse(sourceNode);
		Calendar sourceLastModified = null;
		if (sourceNode.isNodeType(NodeType.MIX_LAST_MODIFIED)) {
			sourceLastModified = sourceNode.getProperty(
					Property.JCR_LAST_MODIFIED).getDate();
		}

		if (sourceNode.getDefinition().isProtected())
			log.warn(sourceNode + " is protected.");

		if (!targetParentNode.hasNode(sourceNode.getName())) {
			String msg = "Adding " + sourceNode.getPath();
			updateMonitor(msg);
			if (log.isDebugEnabled())
				log.debug(msg);
			ContentHandler contentHandler = targetParentNode
					.getSession()
					.getWorkspace()
					.getImportContentHandler(targetParentNode.getPath(),
							ImportUUIDBehavior.IMPORT_UUID_COLLISION_THROW);
			sourceNode.getSession().exportSystemView(sourceNode.getPath(),
					contentHandler, false, noRecurse);
		} else {
			Node targetNode = targetParentNode.getNode(sourceNode.getName());
			if (sourceLastModified != null) {
				Calendar targetLastModified = null;
				if (targetNode.isNodeType(NodeType.MIX_LAST_MODIFIED)) {
					targetLastModified = targetNode.getProperty(
							Property.JCR_LAST_MODIFIED).getDate();
				}

				if (targetLastModified == null
						|| targetLastModified.before(sourceLastModified)) {
					String msg = "Updating " + targetNode.getPath();
					updateMonitor(msg);
					if (log.isDebugEnabled())
						log.debug(msg);
					ContentHandler contentHandler = targetParentNode
							.getSession()
							.getWorkspace()
							.getImportContentHandler(
									targetParentNode.getPath(),
									ImportUUIDBehavior.IMPORT_UUID_COLLISION_REMOVE_EXISTING);
					sourceNode.getSession().exportSystemView(
							sourceNode.getPath(), contentHandler, false,
							noRecurse);
				} else {
					String msg = "Skipped up to date " + targetNode.getPath();
					updateMonitor(msg);
					if (log.isDebugEnabled())
						log.debug(msg);
					return;
				}
			}
		}

		if (noRecurse) {
			// recurse
			Node targetNode = targetParentNode.getNode(sourceNode.getName());
			if (sourceLastModified != null) {
				Calendar zero = new GregorianCalendar();
				zero.setTimeInMillis(0);
				targetNode.setProperty(Property.JCR_LAST_MODIFIED, zero);
				targetNode.getSession().save();
			}

			for (NodeIterator it = sourceNode.getNodes(); it.hasNext();) {
				syncNode(it.nextNode(), targetNode);
			}

			if (sourceLastModified != null) {
				targetNode.setProperty(Property.JCR_LAST_MODIFIED,
						sourceLastModified);
				targetNode.getSession().save();
			}
		}
	}

	protected Boolean noRecurse(Node sourceNode) throws RepositoryException {
		if (sourceNode.isNodeType(NodeType.NT_FILE))
			return false;
		return true;
	}

	/** synchronise only one workspace retrieved by name */
	public void setSourceWksp(String sourceWksp) {
		if (sourceWksp != null && !sourceWksp.trim().equals("")) {
			List<String> list = new ArrayList<String>();
			list.add(sourceWksp);
			setSourceWkspList(list);
		}
	}

	/** synchronise a list workspace that will be retrieved by name */
	public void setSourceWkspList(List<String> sourceWkspList) {
		// clean the list to ease later use
		this.sourceWkspList = null;
		if (sourceWkspList != null) {
			for (String wkspName : sourceWkspList) {
				if (!wkspName.trim().equals("")) {
					// only instantiate if needed
					if (this.sourceWkspList == null)
						this.sourceWkspList = new ArrayList<String>();
					this.sourceWkspList.add(wkspName);
				}
			}
		}
	}

	public void setMonitor(ArgeoMonitor monitor) {
		this.monitor = monitor;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setSourceRepoUri(String sourceRepoUri) {
		this.sourceRepoUri = sourceRepoUri;
	}

	public void setSourceUsername(String sourceUsername) {
		this.sourceUsername = sourceUsername;
	}

	public void setSourcePassword(char[] sourcePassword) {
		this.sourcePassword = sourcePassword;
	}

	public void setTargetRepoUri(String targetRepoUri) {
		this.targetRepoUri = targetRepoUri;
	}

	public void setTargetUsername(String targetUsername) {
		this.targetUsername = targetUsername;
	}

	public void setTargetPassword(char[] targetPassword) {
		this.targetPassword = targetPassword;
	}

	public void setSourceRepository(Repository sourceRepository) {
		this.sourceRepository = sourceRepository;
	}

	public void setSourceCredentials(Credentials sourceCredentials) {
		this.sourceCredentials = sourceCredentials;
	}

	public void setTargetRepository(Repository targetRepository) {
		this.targetRepository = targetRepository;
	}

	public void setTargetCredentials(Credentials targetCredentials) {
		this.targetCredentials = targetCredentials;
	}
}