package org.argeo.slc.client.ui.dist.model;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.ArgeoJcrUtils;
import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.util.security.Keyring;

/**
 * Abstract a repository. Might be persisted by a node in the current user home
 * Node or just an URI and a label if user is anonymous
 */
public class RepoElem extends DistParentElem {
	private final static Log log = LogFactory.getLog(RepoElem.class);

	private Repository repository;
	private Credentials credentials;
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;

	// Defines current repo
	private Node repoNode = null;
	private String label;
	private String uri;

	/**
	 * Creates a RepoElement for an authenticated user. repofactory and keyring
	 * are used to enable lazy init
	 * 
	 */
	public RepoElem(Node repoNode, RepositoryFactory repoFactory,
			Keyring keyring) {
		this.repoNode = repoNode;
		this.repositoryFactory = repoFactory;
		this.keyring = keyring;
		try {
			// initialize this repo informations
			setInHome(RepoConstants.DEFAULT_JAVA_REPOSITORY_ALIAS
					.equals(repoNode.getName()));
			if (!inHome())
				setReadOnly(!repoNode.hasNode(ArgeoNames.ARGEO_PASSWORD));
			uri = JcrUtils.get(repoNode, ArgeoNames.ARGEO_URI);
			label = repoNode.isNodeType(NodeType.MIX_TITLE) ? repoNode
					.getProperty(Property.JCR_TITLE).getString() : repoNode
					.getName();
		} catch (RepositoryException e) {
			throw new SlcException("Unable to " + "initialize repo element", e);
		}
	}

	/**
	 * Creates a RepoElement for anonymous user. repofactory is used to enable
	 * lazy init
	 * 
	 */
	public RepoElem(RepositoryFactory repoFactory, String uri, String label) {
		this.repositoryFactory = repoFactory;
		this.uri = uri;
		this.label = label;
	}

	/** Lazily connects to repository */
	protected void connect() {
		if (repository != null)
			return;
		if (repoNode == null)
			// Anonymous
			repository = ArgeoJcrUtils.getRepositoryByUri(repositoryFactory,
					uri);
		else {
			repository = RepoUtils.getRepository(repositoryFactory, keyring,
					repoNode);
			credentials = RepoUtils.getRepositoryCredentials(keyring, repoNode);
		}
	}

	public String getLabel() {
		return label;
	}

	public String getUri() {
		return uri;
	}

	public String toString() {
		return repoNode != null ? repoNode.toString() : label;
	}

	public Object[] getChildren() {
		try {
			connect();
		} catch (Exception e) {
			log.error("Cannot connect to " + uri + " return no children.", e);
			return new Object[0];
		}

		Session session = null;
		try {
			session = repository.login(credentials);
			String[] workspaceNames = session.getWorkspace()
					.getAccessibleWorkspaceNames();
			Map<String, GroupElem> children = new HashMap<String, GroupElem>();

			buildWksp: for (String workspaceName : workspaceNames) {
				// Add a supplementary check to hide workspace that are not
				// public to anonymous user

				if (repoNode == null) {
					Session tmpSession = null;
					try {
						tmpSession = repository.login(workspaceName);
						Boolean res = true;
						try {
							tmpSession.checkPermission("/", "read");
						} catch (AccessControlException e) {
							res = false;
						}
						if (!res)
							continue buildWksp;
					} catch (RepositoryException e) {
						throw new SlcException(
								"Cannot list workspaces for anonymous user", e);
					} finally {
						JcrUtils.logoutQuietly(tmpSession);
					}
				}

				// filter technical workspaces
				// FIXME: rely on a more robust rule than just wksp name
				if (workspaceName.lastIndexOf('-') > 0) {
					String prefix = workspaceName.substring(0,
							workspaceName.lastIndexOf('-'));
					if (!children.containsKey(prefix)) {
						children.put(prefix, new GroupElem(RepoElem.this,
								prefix));
					}
				}
			}
			return children.values().toArray();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot list workspaces for " + repoNode, e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	public Repository getRepository() {
		connect();
		return repository;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public String getDescription() {
		String desc = label;
		if (repoNode != null)
			desc = label + " (" + uri + ")";
		return desc;
	}

	/** Might return null in case of an anonymous user */
	public Node getRepoNode() {
		return repoNode;
	}
}