package org.argeo.slc.client.ui.dist.model;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryManager;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;

/** Abstract a workspace that contains a software distribution */
public class WorkspaceElem extends DistParentElem {
	private final RepoElem repoElem;
	private String workspaceName;
	private Session currSession;

	public WorkspaceElem(WkspGroupElem parent, RepoElem repoElem,
			String workspaceName) {
		super(workspaceName, repoElem.inHome(), repoElem.isReadOnly());
		this.repoElem = repoElem;
		this.workspaceName = workspaceName;
		setParent(parent);
	}

	public String getWorkspaceName() {
		return workspaceName;
	}

	public RepoElem getRepoElem() {
		return repoElem;
	}

	public Boolean isConnected() {
		if (currSession != null && currSession.isLive())
			return true;
		else
			return false;
	}

	public void login() {
		currSession = repoElem.repositoryLogin(getName());
	}

	/** Utility to create a new Session with correct credential in this context */
	public Session getNewSession() {
		return repoElem.repositoryLogin(getName());
	}

	public boolean hasChildren() {
		try {
			if (isConnected())
				return currSession.getRootNode().hasNodes();
			else
				return true;
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while checking children node existence",
					re);
		}
	}

	/** Override normal behaviour to initialize display of the workspace */
	@Override
	public synchronized Object[] getChildren() {
		if (isLoaded()) {
			return super.getChildren();
		} else {
			// initialize current object
			try {
				// Lazy connect the first time we retrieve children
				if (currSession == null)
					login();

				// Retrieve already existing distribution

				// Use QOM rather than SQL2 - it seems more robust for remoting
				// with JCR 2.2 (might also be some model refresh issue with the
				// remoting)
				QueryManager queryManager = currSession.getWorkspace()
						.getQueryManager();
				QueryObjectModelFactory factory = queryManager.getQOMFactory();
				Selector selector = factory.selector(
						SlcTypes.SLC_MODULAR_DISTRIBUTION,
						SlcTypes.SLC_MODULAR_DISTRIBUTION);
				// Curiously this works...
				// Selector selector = factory.selector(
				// SlcTypes.SLC_JAR_FILE,
				// SlcTypes.SLC_JAR_FILE);

				QueryObjectModel query = factory.createQuery(selector, null,
						null, null);

				// Query groupQuery = currSession
				// .getWorkspace()
				// .getQueryManager()
				// .createQuery(
				// "select * from ["
				// + SlcTypes.SLC_MODULAR_DISTRIBUTION
				// + "]", Query.JCR_SQL2);
				NodeIterator distributions = null;
				try {
					distributions = query.execute().getNodes();
				} catch (InvalidQueryException iqe) {
					// For legacy only does not throw an exception while
					// browsing
					// legacy repositories that does not know
					// SLC_MODULAR_DISTRIBUTION type
				}
				distribs: while (distributions != null
						&& distributions.hasNext()) {
					Node currDist = distributions.nextNode();
					Node distBase = currDist.getParent().getParent();
					if (!distBase.isNodeType(SlcTypes.SLC_ARTIFACT_BASE))
						continue distribs;
					String groupId = distBase
							.getProperty(SlcNames.SLC_GROUP_ID).getString();
					String artifactId = distBase.getProperty(
							SlcNames.SLC_ARTIFACT_ID).getString();

					String name;
					String type;
					if (ModularDistVersionBaseElem.AETHER_BINARIES_TYPE
							.equals(artifactId)) {
						name = groupId;
						type = ModularDistVersionBaseElem.AETHER_BINARIES_TYPE;
					} else {
						name = artifactId;
						type = ModularDistVersionBaseElem.AETHER_DEP_TYPE;
					}
					if (getChildByName(name) == null)
						addChild(new ModularDistVersionBaseElem(
								WorkspaceElem.this, name, distBase, type));
				}
				return super.getChildren();
			} catch (RepositoryException e) {
				throw new SlcException(
						"Cannot initialize WorkspaceNode UI object."
								+ getName(), e);
			}
		}
	}

	@Override
	public synchronized void dispose() {
		JcrUtils.logoutQuietly(currSession);
		super.dispose();
	}
}