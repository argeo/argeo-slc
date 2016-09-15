package org.argeo.slc.client.ui.dist.model;

import java.security.AccessControlException;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.node.ArgeoNames;
import org.argeo.node.NodeUtils;
import org.argeo.node.security.Keyring;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.repo.RepoUtils;

/**
 * Abstract a repository. It might be persisted by a node in the current user
 * home Node or just an URI and a label if user is anonymous
 */
public class RepoElem extends DistParentElem {
	// private final static Log log = LogFactory.getLog(RepoElem.class);

	private RepositoryFactory repositoryFactory;
	private Keyring keyring;
	private Credentials credentials;
	private Session defaultSession = null;

	// Defines current repo
	private Node repoNode = null;
	private String label;
	private String uri;

	private Repository repository;

	/**
	 * Creates a RepoElement for anonymous user. The {@code RepositoryFactory}
	 * is used to enable lazy initialisation
	 */
	public RepoElem(RepositoryFactory repoFactory, String uri, String label) {
		super(label);
		this.repositoryFactory = repoFactory;
		this.uri = uri;
		this.label = label;
	}

	/**
	 * Creates a RepoElement for an authenticated user. The
	 * {@code RepositoryFactory} and {@code Keyring} are used to enable lazy
	 * initialisation
	 * 
	 */
	public RepoElem(RepositoryFactory repoFactory, Keyring keyring, Node repoNode, String alias) {
		super(alias);
		this.label = alias;
		// label = repoNode.isNodeType(NodeType.MIX_TITLE) ? repoNode
		// .getProperty(Property.JCR_TITLE).getString() : repoNode
		// .getName();
		this.repoNode = repoNode;
		this.repositoryFactory = repoFactory;
		this.keyring = keyring;
		try {
			// Initialize this repo information
			setInHome(RepoConstants.DEFAULT_JAVA_REPOSITORY_ALIAS.equals(repoNode.getName()));
			if (inHome())
				// Directly log and retrieve children for local repository
				login();
			else
				setReadOnly(!repoNode.hasNode(ArgeoNames.ARGEO_PASSWORD));
			uri = JcrUtils.get(repoNode, ArgeoNames.ARGEO_URI);
		} catch (RepositoryException e) {
			throw new SlcException("Unable to " + "initialize repo element", e);
		}
	}

	/** Effective login. Does nothing if the session is already there. */
	public void login() {
		if (isConnected())
			return;

		if (repository == null)
			if (repoNode == null)
				// Anonymous
				repository = NodeUtils.getRepositoryByUri(repositoryFactory, uri);
			else {
				repository = RepoUtils.getRepository(repositoryFactory, keyring, repoNode);
				credentials = RepoUtils.getRepositoryCredentials(keyring, repoNode);
			}

		try {
			// FIXME make it more generic
			String defaultWorkspace = "main";
			defaultSession = repository.login(credentials, defaultWorkspace);
			refreshChildren();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot login repository " + label + " with credential " + credentials, e);
		}
	}

	protected void refreshChildren() {
		try {
			// TODO also remove deleted children (only adds for the time being
			String[] workspaceNames = defaultSession.getWorkspace().getAccessibleWorkspaceNames();
			buildWksp: for (String workspaceName : workspaceNames) {
				if (!isWorkspaceVisible(workspaceName))
					continue buildWksp;

				String prefix = getPrefix(workspaceName);
				if (getChildByName(prefix) == null) {
					WkspGroupElem wkspGpElem = new WkspGroupElem(RepoElem.this, prefix);
					addChild(wkspGpElem);
				}
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot list workspaces for " + repoNode, e);
		}
	}

	@Override
	public synchronized void dispose() {
		JcrUtils.logoutQuietly(defaultSession);
		super.dispose();
	}

	private String getPrefix(String workspaceName) {
		// Here is the tricks - we rely on a "hard coded" convention
		// Workspace name should be like: name-major.minor
		if (workspaceName.lastIndexOf(VERSION_SEP) > 0)
			return workspaceName.substring(0, workspaceName.lastIndexOf(VERSION_SEP));
		else
			return workspaceName;
	}

	/* Exposes this to the children workspace group */
	protected boolean isWorkspaceVisible(String wkspName) {
		Boolean result = true;
		if (ARGEO_SYSTEM_WKSP.contains(wkspName))
			return false;
		// Add a supplementary check to hide workspace that are not
		// public to anonymous user
		if (repoNode == null) {
			Session tmpSession = null;
			try {
				tmpSession = repository.login(wkspName);
				try {
					tmpSession.checkPermission("/", "read");
				} catch (AccessControlException e) {
					result = false;
				}
			} catch (RepositoryException e) {
				throw new SlcException("Cannot list workspaces for anonymous user", e);
			} finally {
				JcrUtils.logoutQuietly(tmpSession);
			}
		}
		return result;
	}

	/**
	 * Actual call to the
	 * {@link Repository#login(javax.jcr.Credentials, String)} method. To be
	 * overridden.
	 * 
	 * Creates a new session with correct credentials using the information
	 * contained in the corresponding repo node. It provides all UI children
	 * elements an unique entry point to retrieve a new Session. Caller must
	 * close the session when it is not in use anymore.
	 * 
	 */
	protected Session repositoryLogin(String workspaceName) {
		try {
			if (workspaceName == null)
				workspaceName = "main";// FIXME make it more generic
			return repository.login(credentials, workspaceName);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot login repository " + label + " with credential " + credentials, e);
		}
	}

	public Boolean isConnected() {
		if (defaultSession != null && defaultSession.isLive())
			return true;
		else
			return false;
	}

	/** Exposes URI to the current repository */
	public String getUri() {
		return uri;
	}

	public String getRepoNodePath() {
		if (repoNode == null)
			return null;
		else
			try {
				return repoNode.getPath();
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get node path for repository " + label, e);
			}
	}

	/**
	 * Exposes the local repoNode that completely define a connection to a
	 * repository (including a set of credentials). Might return null in case of
	 * an anonymous user
	 */
	protected Node getRepoNode() {
		return repoNode;
	}

	protected Repository getRepository() {
		return repository;
	}

	protected Credentials getCredentials() {
		return credentials;
	}

	// META INFO
	public String getDescription() {
		String desc = label;
		if (repoNode != null)
			desc = label + " (" + uri + ")";
		return desc;
	}

	public String getLabel() {
		return label;
	}

	public String toString() {
		return repoNode != null ? repoNode.toString() : label;
	}
}