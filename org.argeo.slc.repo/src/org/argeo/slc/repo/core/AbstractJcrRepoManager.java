package org.argeo.slc.repo.core;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.NodeIndexer;

/** Generic operations on a JCR-based repo. */
abstract class AbstractJcrRepoManager {
	private final static CmsLog log = CmsLog.getLog(AbstractJcrRepoManager.class);
	private String securityWorkspace = "security";

	private Repository jcrRepository;
	private Session adminSession;
	private List<NodeIndexer> nodeIndexers;

	// registries
	private Map<String, Session> workspaceSessions = new TreeMap<String, Session>();
	private Map<String, WorkspaceIndexer> workspaceIndexers = new TreeMap<String, WorkspaceIndexer>();

	public void init() {
		try {
			adminSession = jcrRepository.login();
			String[] workspaceNames = adminSession.getWorkspace().getAccessibleWorkspaceNames();
			for (String workspaceName : workspaceNames) {
				if (workspaceName.equals(securityWorkspace))
					continue;
				if (workspaceName.equals(adminSession.getWorkspace().getName()))
					continue;
				workspaceInit(workspaceName);
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot initialize repo manager", e);
		}
	}

	public void destroy() {
		for (String key : workspaceIndexers.keySet()) {
			workspaceIndexers.get(key).close();
		}

		for (String key : workspaceSessions.keySet()) {
			JcrUtils.logoutQuietly(workspaceSessions.get(key));
		}
		JcrUtils.logoutQuietly(adminSession);
	}

	public void createWorkspace(String workspaceName) {
		try {
			try {
				jcrRepository.login(workspaceName);
				throw new SlcException("Workspace " + workspaceName + " exists already.");
			} catch (NoSuchWorkspaceException e) {
				// try to create workspace
				adminSession.getWorkspace().createWorkspace(workspaceName);
				workspaceInit(workspaceName);
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot create workspace " + workspaceName, e);
		}
	}

	protected void workspaceInit(String workspaceName) {
		Session workspaceAdminSession = null;
		try {
			workspaceAdminSession = jcrRepository.login(workspaceName);
			workspaceSessions.put(workspaceName, adminSession);
			JcrUtils.addPrivilege(workspaceAdminSession, "/", SlcConstants.ROLE_SLC, "jcr:all");
			WorkspaceIndexer workspaceIndexer = new WorkspaceIndexer(workspaceAdminSession, nodeIndexers);
			workspaceIndexers.put(workspaceName, workspaceIndexer);
		} catch (RepositoryException e) {
			log.error("Cannot initialize workspace " + workspaceName, e);
		} finally {
			JcrUtils.logoutQuietly(workspaceAdminSession);
		}
	}

	public void setJcrRepository(Repository jcrRepository) {
		this.jcrRepository = jcrRepository;
	}

	public void setNodeIndexers(List<NodeIndexer> nodeIndexers) {
		this.nodeIndexers = nodeIndexers;
	}

	public void setSecurityWorkspace(String securityWorkspace) {
		this.securityWorkspace = securityWorkspace;
	}
}