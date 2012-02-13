package org.argeo.slc.repo.maven.proxy;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.proxy.AbstractUrlProxy;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.RepoConstants;
import org.sonatype.aether.repository.RemoteRepository;

/** Synchronizes the node repository with remote Maven repositories */
public class MavenProxyServiceImpl extends AbstractUrlProxy implements
		MavenProxyService, ArgeoNames, SlcNames {
	private final static Log log = LogFactory
			.getLog(MavenProxyServiceImpl.class);

	private List<RemoteRepository> defaultRepositories = new ArrayList<RemoteRepository>();

	private boolean rootNodeIsArtifactBase = RepoConstants.ARTIFACTS_BASE_PATH
			.equals("/");

	/** Inititalizes the artifacts area. */
	@Override
	protected void beforeInitSessionSave(Session session)
			throws RepositoryException {
		JcrUtils.mkdirsSafe(session, RepoConstants.ARTIFACTS_BASE_PATH);
		Node proxiedRepositories = JcrUtils.mkdirsSafe(session,
				RepoConstants.PROXIED_REPOSITORIES);
		for (RemoteRepository repository : defaultRepositories) {
			if (!proxiedRepositories.hasNode(repository.getId())) {
				Node proxiedRepository = proxiedRepositories.addNode(repository
						.getId());
				proxiedRepository.addMixin(NodeType.MIX_REFERENCEABLE);
				JcrUtils.urlToAddressProperties(proxiedRepository,
						repository.getUrl());
				// proxiedRepository.setProperty(SLC_URL, repository.getUrl());
				proxiedRepository.setProperty(SLC_TYPE,
						repository.getContentType());
			}
		}
	}

	/**
	 * Retrieve and add this file to the repository
	 */
	@Override
	protected Node retrieve(Session session, String path) {
		try {
			if (session.hasPendingChanges())
				throw new SlcException("Session has pending changed");
			Node node = null;
			for (Node proxiedRepository : getBaseUrls(session)) {
				String baseUrl = JcrUtils
						.urlFromAddressProperties(proxiedRepository);
				node = proxyUrl(session, baseUrl, path);
				if (node != null) {
					node.addMixin(SlcTypes.SLC_KNOWN_ORIGIN);
					Node origin = node
							.addNode(SLC_ORIGIN, SlcTypes.SLC_PROXIED);
					origin.setProperty(SLC_PROXY, proxiedRepository);
					JcrUtils.urlToAddressProperties(origin, baseUrl + path);
					if (log.isDebugEnabled())
						log.debug("Imported " + baseUrl + path + " to " + node);
					return node;
				}
			}
			if (log.isDebugEnabled())
				log.warn("No proxy found for " + path);
			return null;
		} catch (Exception e) {
			throw new SlcException("Cannot proxy " + path, e);
		}
	}

	protected synchronized List<Node> getBaseUrls(Session session)
			throws RepositoryException {
		List<Node> baseUrls = new ArrayList<Node>();
		for (NodeIterator nit = session.getNode(
				RepoConstants.PROXIED_REPOSITORIES).getNodes(); nit.hasNext();) {
			Node proxiedRepository = nit.nextNode();
			baseUrls.add(proxiedRepository);
		}
		return baseUrls;
	}

	/** The JCR path where this file could be found */
	public String getNodePath(String path) {
		if (rootNodeIsArtifactBase)
			return path;
		else
			return RepoConstants.ARTIFACTS_BASE_PATH + path;
	}

	public void setDefaultRepositories(
			List<RemoteRepository> defaultRepositories) {
		this.defaultRepositories = defaultRepositories;
	}

}
