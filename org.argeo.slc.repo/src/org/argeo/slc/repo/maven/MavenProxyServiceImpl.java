package org.argeo.slc.repo.maven;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.Privilege;

import org.argeo.api.cms.CmsLog;
import org.argeo.cms.ArgeoNames;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.proxy.AbstractUrlProxy;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.repo.MavenProxyService;
import org.argeo.slc.repo.RepoConstants;
import org.eclipse.aether.repository.RemoteRepository;

/** Synchronises the node repository with remote Maven repositories */
public class MavenProxyServiceImpl extends AbstractUrlProxy implements MavenProxyService, ArgeoNames, SlcNames {
	private final static CmsLog log = CmsLog.getLog(MavenProxyServiceImpl.class);

	private List<RemoteRepository> defaultRepositories = new ArrayList<RemoteRepository>();

	/** Initialises the artifacts area. */
	@Override
	protected void beforeInitSessionSave(Session session) throws RepositoryException {
		JcrUtils.addPrivilege(session, "/", SlcConstants.USER_ANONYMOUS, Privilege.JCR_READ);
		try {
			JcrUtils.addPrivilege(session, "/", SlcConstants.ROLE_SLC, Privilege.JCR_ALL);
		} catch (AccessControlException e) {
			if (log.isTraceEnabled())
				log.trace("Cannot give jcr:all privileges to " + SlcConstants.ROLE_SLC);
		}

		JcrUtils.mkdirsSafe(session, RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH);
		Node proxiedRepositories = JcrUtils.mkdirsSafe(session, RepoConstants.PROXIED_REPOSITORIES);
		for (RemoteRepository repository : defaultRepositories) {
			if (!proxiedRepositories.hasNode(repository.getId())) {
				Node proxiedRepository = proxiedRepositories.addNode(repository.getId());
				proxiedRepository.addMixin(NodeType.MIX_REFERENCEABLE);
				JcrUtils.urlToAddressProperties(proxiedRepository, repository.getUrl());
				// proxiedRepository.setProperty(SLC_URL, repository.getUrl());
				proxiedRepository.setProperty(SLC_TYPE, repository.getContentType());
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
				String baseUrl = JcrUtils.urlFromAddressProperties(proxiedRepository);
				node = proxyUrl(session, baseUrl, path);
				if (node != null) {
					node.addMixin(SlcTypes.SLC_KNOWN_ORIGIN);
					Node origin = node.addNode(SLC_ORIGIN, SlcTypes.SLC_PROXIED);
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

	protected synchronized List<Node> getBaseUrls(Session session) throws RepositoryException {
		List<Node> baseUrls = new ArrayList<Node>();
		for (NodeIterator nit = session.getNode(RepoConstants.PROXIED_REPOSITORIES).getNodes(); nit.hasNext();) {
			Node proxiedRepository = nit.nextNode();
			baseUrls.add(proxiedRepository);
		}
		return baseUrls;
	}

	public void setDefaultRepositories(List<RemoteRepository> defaultRepositories) {
		this.defaultRepositories = defaultRepositories;
	}
}
