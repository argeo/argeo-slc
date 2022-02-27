package org.argeo.slc.repo;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.jcr.Binary;
import javax.jcr.Credentials;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.commons.io.IOUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.cms.jcr.CmsJcrUtils;
import org.argeo.jcr.JcrMonitor;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.xml.sax.SAXException;

/**
 * Synchronise workspaces from a remote software repository to the local
 * repository (Synchronisation in the other direction does not work).
 * 
 * Workspaces are retrieved by name given a map that links the source with a
 * target name. If a target workspace does not exist, it is created. Otherwise
 * we copy the content of the source workspace into the target one.
 */
public class RepoSync implements Runnable {
	private final static CmsLog log = CmsLog.getLog(RepoSync.class);

	// Centralizes definition of workspaces that must be ignored by the sync.
	private final static List<String> IGNORED_WKSP_LIST = Arrays.asList("security", "localrepo");

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

	private JcrMonitor monitor;
	private Map<String, String> workspaceMap;

	// TODO fix monitor
	private Boolean filesOnly = false;

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
	public RepoSync(Repository sourceRepository, Credentials sourceCredentials, Repository targetRepository,
			Credentials targetCredentials) {
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
				sourceRepository = CmsJcrUtils.getRepositoryByUri(repositoryFactory, sourceRepoUri);
			if (sourceCredentials == null && sourceUsername != null)
				sourceCredentials = new SimpleCredentials(sourceUsername, sourcePassword);
			// FIXME make it more generic
			sourceDefaultSession = sourceRepository.login(sourceCredentials, RepoConstants.DEFAULT_DEFAULT_WORKSPACE);

			if (targetRepository == null)
				targetRepository = CmsJcrUtils.getRepositoryByUri(repositoryFactory, targetRepoUri);
			if (targetCredentials == null && targetUsername != null)
				targetCredentials = new SimpleCredentials(targetUsername, targetPassword);
			targetDefaultSession = targetRepository.login(targetCredentials);

			Map<String, Exception> errors = new HashMap<String, Exception>();
			for (String sourceWorkspaceName : sourceDefaultSession.getWorkspace().getAccessibleWorkspaceNames()) {
				if (monitor != null && monitor.isCanceled())
					break;

				if (workspaceMap != null && !workspaceMap.containsKey(sourceWorkspaceName))
					continue;
				if (IGNORED_WKSP_LIST.contains(sourceWorkspaceName))
					continue;

				Session sourceSession = null;
				Session targetSession = null;
				String targetWorkspaceName = workspaceMap.get(sourceWorkspaceName);
				try {
					try {
						targetSession = targetRepository.login(targetCredentials, targetWorkspaceName);
					} catch (NoSuchWorkspaceException e) {
						targetDefaultSession.getWorkspace().createWorkspace(targetWorkspaceName);
						targetSession = targetRepository.login(targetCredentials, targetWorkspaceName);
					}
					sourceSession = sourceRepository.login(sourceCredentials, sourceWorkspaceName);
					syncWorkspace(sourceSession, targetSession);
				} catch (Exception e) {
					errors.put("Could not sync workspace " + sourceWorkspaceName, e);
					if (log.isErrorEnabled())
						e.printStackTrace();

				} finally {
					JcrUtils.logoutQuietly(sourceSession);
					JcrUtils.logoutQuietly(targetSession);
				}
			}

			if (monitor != null && monitor.isCanceled())
				log.info("Sync has been canceled by user");

			long duration = (System.currentTimeMillis() - begin) / 1000;// s
			log.info("Sync " + sourceRepoUri + " to " + targetRepoUri + " in " + (duration / 60)

					+ "min " + (duration % 60) + "s");

			if (errors.size() > 0) {
				throw new SlcException("Sync failed " + errors);
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot sync " + sourceRepoUri + " to " + targetRepoUri, e);
		} finally {
			JcrUtils.logoutQuietly(sourceDefaultSession);
			JcrUtils.logoutQuietly(targetDefaultSession);
		}
	}

	private long getNodesNumber(Session session) {
		if (IGNORED_WKSP_LIST.contains(session.getWorkspace().getName()))
			return 0l;
		try {
			Query countQuery = session.getWorkspace().getQueryManager().createQuery(
					"select file from [" + (true ? NodeType.NT_FILE : NodeType.NT_BASE) + "] as file", Query.JCR_SQL2);

			QueryResult result = countQuery.execute();
			Long expectedCount = result.getNodes().getSize();
			return expectedCount;
		} catch (RepositoryException e) {
			throw new SlcException("Unexpected error while computing " + "the size of the fetch for workspace "
					+ session.getWorkspace().getName(), e);
		}
	}

	protected void syncWorkspace(Session sourceSession, Session targetSession) {
		if (monitor != null) {
			monitor.beginTask("Computing fetch size...", -1);
			Long totalAmount = getNodesNumber(sourceSession);
			monitor.beginTask("Fetch", totalAmount.intValue());
		}

		try {
			String msg = "Synchronizing workspace: " + sourceSession.getWorkspace().getName();
			if (monitor != null)
				monitor.setTaskName(msg);
			if (log.isDebugEnabled())
				log.debug(msg);

			for (NodeIterator it = sourceSession.getRootNode().getNodes(); it.hasNext();) {
				Node node = it.nextNode();
				if (node.getName().contains(":"))
					continue;
				if (node.getName().equals("download"))
					continue;
				if (!node.isNodeType(NodeType.NT_HIERARCHY_NODE))
					continue;
				syncNode(node, targetSession);
			}
			// if (filesOnly) {
			// JcrUtils.copyFiles(sourceSession.getRootNode(), targetSession.getRootNode(),
			// true, monitor);
			// } else {
			// for (NodeIterator it = sourceSession.getRootNode().getNodes(); it.hasNext();)
			// {
			// Node node = it.nextNode();
			// if (node.getName().equals("jcr:system"))
			// continue;
			// syncNode(node, targetSession);
			// }
			// }
			if (log.isDebugEnabled())
				log.debug("Synced " + sourceSession.getWorkspace().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new SlcException("Cannot sync " + sourceSession.getWorkspace().getName() + " to "
					+ targetSession.getWorkspace().getName(), e);
		}
	}

	/** factorizes monitor management */
	private void updateMonitor(String msg) {
		updateMonitor(msg, false);
	}

	protected void syncNode(Node sourceNode, Session targetSession) throws RepositoryException, SAXException {
		if (filesOnly) {
			Node targetNode;
			if (targetSession.itemExists(sourceNode.getPath()))
				targetNode = targetSession.getNode(sourceNode.getPath());
			else
				targetNode = JcrUtils.mkdirs(targetSession, sourceNode.getPath(), NodeType.NT_FOLDER);
			JcrUtils.copyFiles(sourceNode, targetNode, true, monitor, true);
			return;
		}
		// Boolean singleLevel = singleLevel(sourceNode);
		try {
			if (monitor != null && monitor.isCanceled()) {
				updateMonitor("Fetched has been canceled, " + "process is terminating");
				return;
			}

			Node targetParentNode = targetSession.getNode(sourceNode.getParent().getPath());
			Node targetNode;
			if (monitor != null && sourceNode.isNodeType(NodeType.NT_HIERARCHY_NODE))
				monitor.subTask("Process " + sourceNode.getPath());

			final Boolean isNew;
			if (!targetSession.itemExists(sourceNode.getPath())) {
				isNew = true;
				targetNode = targetParentNode.addNode(sourceNode.getName(), sourceNode.getPrimaryNodeType().getName());
			} else {
				isNew = false;
				targetNode = targetSession.getNode(sourceNode.getPath());
				if (!targetNode.getPrimaryNodeType().getName().equals(sourceNode.getPrimaryNodeType().getName()))
					targetNode.setPrimaryType(sourceNode.getPrimaryNodeType().getName());
			}

			// export
			// sourceNode.getSession().exportSystemView(sourceNode.getPath(),
			// contentHandler, false, singleLevel);

			// if (singleLevel) {
			// if (targetSession.hasPendingChanges()) {
			// // updateMonitor(
			// // (isNew ? "Added " : "Updated ") + targetNode.getPath(),
			// // true);
			// if (doSave)
			// targetSession.save();
			// } else {
			// // updateMonitor("Checked " + targetNode.getPath(), false);
			// }
			// }

			// mixin and properties
			for (NodeType nt : sourceNode.getMixinNodeTypes()) {
				if (!targetNode.isNodeType(nt.getName()) && targetNode.canAddMixin(nt.getName()))
					targetNode.addMixin(nt.getName());
			}
			copyProperties(sourceNode, targetNode);

			// next level
			NodeIterator ni = sourceNode.getNodes();
			while (ni != null && ni.hasNext()) {
				Node sourceChild = ni.nextNode();
				syncNode(sourceChild, targetSession);
			}

			copyTimestamps(sourceNode, targetNode);

			if (sourceNode.isNodeType(NodeType.NT_HIERARCHY_NODE)) {
				if (targetSession.hasPendingChanges()) {
					if (sourceNode.isNodeType(NodeType.NT_FILE))
						updateMonitor((isNew ? "Added " : "Updated ") + targetNode.getPath(), true);
					// if (doSave)
					targetSession.save();
				} else {
					if (sourceNode.isNodeType(NodeType.NT_FILE))
						updateMonitor("Checked " + targetNode.getPath(), false);
				}
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot sync source node " + sourceNode, e);
		}
	}

	private void copyTimestamps(Node sourceNode, Node targetNode) throws RepositoryException {
		if (sourceNode.getDefinition().isProtected())
			return;
		if (targetNode.getDefinition().isProtected())
			return;
		copyTimestamp(sourceNode, targetNode, Property.JCR_CREATED);
		copyTimestamp(sourceNode, targetNode, Property.JCR_CREATED_BY);
		copyTimestamp(sourceNode, targetNode, Property.JCR_LAST_MODIFIED);
		copyTimestamp(sourceNode, targetNode, Property.JCR_LAST_MODIFIED_BY);
	}

	private void copyTimestamp(Node sourceNode, Node targetNode, String property) throws RepositoryException {
		if (sourceNode.hasProperty(property)) {
			Property p = sourceNode.getProperty(property);
			if (p.getDefinition().isProtected())
				return;
			if (targetNode.hasProperty(property)
					&& targetNode.getProperty(property).getValue().equals(sourceNode.getProperty(property).getValue()))
				return;
			targetNode.setProperty(property, sourceNode.getProperty(property).getValue());
		}
	}

	private void copyProperties(Node sourceNode, Node targetNode) throws RepositoryException {
		properties: for (PropertyIterator pi = sourceNode.getProperties(); pi.hasNext();) {
			Property p = pi.nextProperty();
			if (p.getDefinition().isProtected())
				continue properties;
			if (p.getName().equals(Property.JCR_CREATED) || p.getName().equals(Property.JCR_CREATED_BY)
					|| p.getName().equals(Property.JCR_LAST_MODIFIED)
					|| p.getName().equals(Property.JCR_LAST_MODIFIED_BY))
				continue properties;

			if (p.getType() == PropertyType.BINARY) {
				copyBinary(p, targetNode);
			} else {

				if (p.isMultiple()) {
					if (!targetNode.hasProperty(p.getName())
							|| !Arrays.equals(targetNode.getProperty(p.getName()).getValues(), p.getValues()))
						targetNode.setProperty(p.getName(), p.getValues());
				} else {
					if (!targetNode.hasProperty(p.getName())
							|| !targetNode.getProperty(p.getName()).getValue().equals(p.getValue()))
						targetNode.setProperty(p.getName(), p.getValue());
				}
			}
		}
	}

	private static void copyBinary(Property p, Node targetNode) throws RepositoryException {
		InputStream in = null;
		Binary sourceBinary = null;
		Binary targetBinary = null;
		try {
			sourceBinary = p.getBinary();
			if (targetNode.hasProperty(p.getName()))
				targetBinary = targetNode.getProperty(p.getName()).getBinary();

			// optim FIXME make it more configurable
			if (targetBinary != null)
				if (sourceBinary.getSize() == targetBinary.getSize()) {
					if (log.isTraceEnabled())
						log.trace("Skipped " + p.getPath());
					return;
				}

			in = sourceBinary.getStream();
			targetBinary = targetNode.getSession().getValueFactory().createBinary(in);
			targetNode.setProperty(p.getName(), targetBinary);
		} catch (Exception e) {
			throw new SlcException("Could not transfer " + p, e);
		} finally {
			IOUtils.closeQuietly(in);
			JcrUtils.closeQuietly(sourceBinary);
			JcrUtils.closeQuietly(targetBinary);
		}
	}

	/** factorizes monitor management */
	private void updateMonitor(String msg, Boolean doLog) {
		if (doLog && log.isDebugEnabled())
			log.debug(msg);
		if (monitor != null) {
			monitor.worked(1);
			monitor.subTask(msg);
		}
	}

	// private void syncNode_old(Node sourceNode, Node targetParentNode)
	// throws RepositoryException, SAXException {
	//
	// // enable cancelation of the current fetch process
	// // fxme insure the repository stays in a stable state
	// if (monitor != null && monitor.isCanceled()) {
	// updateMonitor("Fetched has been canceled, "
	// + "process is terminating");
	// return;
	// }
	//
	// Boolean noRecurse = singleLevel(sourceNode);
	// Calendar sourceLastModified = null;
	// if (sourceNode.isNodeType(NodeType.MIX_LAST_MODIFIED)) {
	// sourceLastModified = sourceNode.getProperty(
	// Property.JCR_LAST_MODIFIED).getDate();
	// }
	//
	// if (sourceNode.getDefinition().isProtected())
	// log.warn(sourceNode + " is protected.");
	//
	// if (!targetParentNode.hasNode(sourceNode.getName())) {
	// String msg = "Adding " + sourceNode.getPath();
	// updateMonitor(msg);
	// if (log.isDebugEnabled())
	// log.debug(msg);
	// ContentHandler contentHandler = targetParentNode
	// .getSession()
	// .getWorkspace()
	// .getImportContentHandler(targetParentNode.getPath(),
	// ImportUUIDBehavior.IMPORT_UUID_COLLISION_THROW);
	// sourceNode.getSession().exportSystemView(sourceNode.getPath(),
	// contentHandler, false, noRecurse);
	// } else {
	// Node targetNode = targetParentNode.getNode(sourceNode.getName());
	// if (sourceLastModified != null) {
	// Calendar targetLastModified = null;
	// if (targetNode.isNodeType(NodeType.MIX_LAST_MODIFIED)) {
	// targetLastModified = targetNode.getProperty(
	// Property.JCR_LAST_MODIFIED).getDate();
	// }
	//
	// if (targetLastModified == null
	// || targetLastModified.before(sourceLastModified)) {
	// String msg = "Updating " + targetNode.getPath();
	// updateMonitor(msg);
	// if (log.isDebugEnabled())
	// log.debug(msg);
	// ContentHandler contentHandler = targetParentNode
	// .getSession()
	// .getWorkspace()
	// .getImportContentHandler(
	// targetParentNode.getPath(),
	// ImportUUIDBehavior.IMPORT_UUID_COLLISION_REMOVE_EXISTING);
	// sourceNode.getSession().exportSystemView(
	// sourceNode.getPath(), contentHandler, false,
	// noRecurse);
	// } else {
	// String msg = "Skipped up to date " + targetNode.getPath();
	// updateMonitor(msg);
	// if (log.isDebugEnabled())
	// log.debug(msg);
	// return;
	// }
	// }
	// }
	//
	// if (noRecurse) {
	// // recurse
	// Node targetNode = targetParentNode.getNode(sourceNode.getName());
	// if (sourceLastModified != null) {
	// Calendar zero = new GregorianCalendar();
	// zero.setTimeInMillis(0);
	// targetNode.setProperty(Property.JCR_LAST_MODIFIED, zero);
	// targetNode.getSession().save();
	// }
	//
	// for (NodeIterator it = sourceNode.getNodes(); it.hasNext();) {
	// syncNode_old(it.nextNode(), targetNode);
	// }
	//
	// if (sourceLastModified != null) {
	// targetNode.setProperty(Property.JCR_LAST_MODIFIED,
	// sourceLastModified);
	// targetNode.getSession().save();
	// }
	// }
	// }

	protected Boolean singleLevel(Node sourceNode) throws RepositoryException {
		if (sourceNode.isNodeType(NodeType.NT_FILE))
			return false;
		return true;
	}

	/**
	 * Synchronises only one workspace, retrieved by name without changing its name.
	 */
	public void setSourceWksp(String sourceWksp) {
		if (sourceWksp != null && !sourceWksp.trim().equals("")) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(sourceWksp, sourceWksp);
			setWkspMap(map);
		}
	}

	/**
	 * Synchronises a map of workspaces that will be retrieved by name. If the
	 * target name is not defined (eg null or an empty string) for a given source
	 * workspace, we use the source name as target name.
	 */
	public void setWkspMap(Map<String, String> workspaceMap) {
		// clean the list to ease later use
		this.workspaceMap = new HashMap<String, String>();
		if (workspaceMap != null) {
			workspaceNames: for (String srcName : workspaceMap.keySet()) {
				String targetName = workspaceMap.get(srcName);

				// Sanity check
				if (srcName.trim().equals(""))
					continue workspaceNames;
				if (targetName == null || "".equals(targetName.trim()))
					targetName = srcName;
				this.workspaceMap.put(srcName, targetName);
			}
		}
		// clean the map to ease later use
		if (this.workspaceMap.size() == 0)
			this.workspaceMap = null;
	}

	public void setMonitor(JcrMonitor monitor) {
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

	public void setFilesOnly(Boolean filesOnly) {
		this.filesOnly = filesOnly;
	}

}