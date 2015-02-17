package org.argeo.slc.client.ui.dist.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.argeo.eclipse.ui.TreeParent;
import org.argeo.jcr.ArgeoJcrUtils;
import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.ArgeoTypes;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.UserJcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.model.RepoElem;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.util.security.Keyring;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Enables browsing in local and remote SLC software repositories. Keyring and
 * repository factory must be injected
 */
public class DistTreeContentProvider implements ITreeContentProvider {
	private Session nodeSession;
	List<RepoElem> repositories = new ArrayList<RepoElem>();

	/* DEPENDENCY INJECTION */
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;

	public Object[] getElements(Object input) {
		Repository nodeRepository = (Repository) input;
		try {
			if (nodeSession != null)
				dispose();
			nodeSession = nodeRepository.login();

			String reposPath = UserJcrUtils.getUserHome(nodeSession).getPath()
					+ RepoConstants.REPOSITORIES_BASE_PATH;

			if (!nodeSession.itemExists(reposPath))
				initializeModel(nodeSession);

			NodeIterator repos = nodeSession.getNode(reposPath).getNodes();
			while (repos.hasNext()) {
				Node repoNode = repos.nextNode();
				if (repoNode.isNodeType(ArgeoTypes.ARGEO_REMOTE_REPOSITORY)) {
					String label = repoNode.isNodeType(NodeType.MIX_TITLE) ? repoNode
							.getProperty(Property.JCR_TITLE).getString()
							: repoNode.getName();
					repositories.add(new RepoElem(repositoryFactory, keyring,
							repoNode, label));
				}
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot get base elements", e);
		}
		return repositories.toArray();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	// @Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TreeParent)
			return ((TreeParent) parentElement).getChildren();
		else
			return null;
	}

	// @Override
	public Object getParent(Object element) {
		if (element instanceof TreeParent)
			return ((TreeParent) element).getParent();
		return null;
	}

	// @Override
	public boolean hasChildren(Object element) {
		if (element instanceof TreeParent)
			return ((TreeParent) element).hasChildren();
		else
			return false;
	}

	public void dispose() {
		for (RepoElem repoElem : repositories)
			repoElem.dispose();
		repositories = new ArrayList<RepoElem>();
		JcrUtils.logoutQuietly(nodeSession);
	}

	private void initializeModel(Session nodeSession) {
		try {

			Node homeNode = UserJcrUtils.getUserHome(nodeSession);
			if (homeNode == null) // anonymous
				throw new SlcException("User must be authenticated.");

			// make sure base directory is available
			Node repos = JcrUtils.mkdirs(nodeSession, homeNode.getPath()
					+ RepoConstants.REPOSITORIES_BASE_PATH);
			nodeSession.save();

			// register default local java repository
			String alias = RepoConstants.DEFAULT_JAVA_REPOSITORY_ALIAS;
			Repository javaRepository = ArgeoJcrUtils.getRepositoryByAlias(
					repositoryFactory, alias);
			if (javaRepository != null) {
				if (!repos.hasNode(alias)) {
					Node repoNode = repos.addNode(alias,
							ArgeoTypes.ARGEO_REMOTE_REPOSITORY);
					repoNode.setProperty(ArgeoNames.ARGEO_URI, "vm:///" + alias);
					repoNode.addMixin(NodeType.MIX_TITLE);
					repoNode.setProperty(Property.JCR_TITLE,
							RepoConstants.DEFAULT_JAVA_REPOSITORY_LABEL);
					nodeSession.save();
				}
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot initialize model", e);
		}
	}

	/* DEPENDENCY INJECTION */
	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}
}