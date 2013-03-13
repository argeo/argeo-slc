package org.argeo.slc.client.ui.dist.model;

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

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.util.security.Keyring;

public class RepoElem extends DistParentElem {
	private Node repoNode;
	private Repository repository;
	private Credentials credentials;

	private RepositoryFactory repositoryFactory;
	private Keyring keyring;

	@Deprecated
	public RepoElem(Node repoNode, boolean inHome, boolean isReadOnly) {
		super(inHome, isReadOnly);
		this.repoNode = repoNode;
	}

	/** Inject repofactory and keyring to enable lazy init */
	public RepoElem(Node repoNode, RepositoryFactory repoFactory,
			Keyring keyring, boolean inHome, boolean isReadOnly) {
		super(inHome, isReadOnly);
		this.repoNode = repoNode;
		this.repositoryFactory = repoFactory;
		this.keyring = keyring;
	}

	/** Inject repofactory and keyring to enable lazy init */
	public RepoElem(Node repoNode, RepositoryFactory repoFactory,
			Keyring keyring) {
		this.repoNode = repoNode;
		this.repositoryFactory = repoFactory;
		this.keyring = keyring;
	}

	@Deprecated
	public RepoElem(Node repoNode) {
		this.repoNode = repoNode;
	}

	/** Lazily connects to repository */
	protected void connect() {
		if (repository != null)
			return;
		repository = RepoUtils.getRepository(repositoryFactory, keyring,
				repoNode);
		credentials = RepoUtils.getRepositoryCredentials(keyring, repoNode);
	}

	public String getLabel() {
		try {
			if (repoNode.isNodeType(NodeType.MIX_TITLE)) {
				return repoNode.getProperty(Property.JCR_TITLE).getString();
			} else {
				return repoNode.getName();
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot read label of " + repoNode, e);
		}
	}

	public String toString() {
		return repoNode.toString();
	}

	public Object[] getChildren() {
		connect();
		Session session = null;
		try {
			session = repository.login(credentials);
			String[] workspaceNames = session.getWorkspace()
					.getAccessibleWorkspaceNames();
			// List<DistributionElem> distributionElems = new
			// ArrayList<DistributionElem>();
			Map<String, GroupElem> children = new HashMap<String, GroupElem>();
			for (String workspaceName : workspaceNames) {
				// filter technical workspaces
				// FIXME: rely on a more robust rule than just wksp name
				if (workspaceName.lastIndexOf('-') > 0) {
					String prefix = workspaceName.substring(0,
							workspaceName.lastIndexOf('-'));
					if (!repoNode.hasNode(workspaceName))
						repoNode.addNode(workspaceName);
					repoNode.getSession().save();
					if (!children.containsKey(prefix)) {
						children.put(prefix, new GroupElem(RepoElem.this,
								prefix));
					}
					// FIXME remove deleted workspaces
				}
			}
			return children.values().toArray();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot list workspaces for " + repoNode, e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	public String getRepoPath() {
		try {
			return repoNode.getPath();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot get path for " + repoNode, e);
		}
	}

	public Repository getRepository() {
		connect();
		return repository;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public Node getRepoNode() {
		return repoNode;
	}

}
