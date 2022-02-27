package org.argeo.slc.rpmfactory.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.security.AccessControlException;

import org.argeo.api.cms.CmsLog;
import org.argeo.cms.ArgeoNames;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.proxy.AbstractUrlProxy;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.rpmfactory.RpmProxyService;
import org.argeo.slc.rpmfactory.RpmRepository;

/** Synchronises the node repository with remote Maven repositories */
public class RpmProxyServiceImpl extends AbstractUrlProxy implements
		RpmProxyService, ArgeoNames, SlcNames {
	private final static CmsLog log = CmsLog.getLog(RpmProxyServiceImpl.class);

	private Set<RpmRepository> defaultRepositories = new HashSet<RpmRepository>();

	@Override
	protected void beforeInitSessionSave(Session session)
			throws RepositoryException {
		JcrUtils.addPrivilege(session, "/", "anonymous", "jcr:read");
		try {
			JcrUtils.addPrivilege(session, "/", SlcConstants.ROLE_SLC,
					"jcr:all");
		} catch (AccessControlException e) {
			if (log.isTraceEnabled())
				log.trace("Cannot give jcr:all privileges to "+SlcConstants.ROLE_SLC);
		}

		JcrUtils.mkdirsSafe(session, RepoConstants.PROXIED_REPOSITORIES);
	}

	/**
	 * Retrieve and add this file to the repository
	 */
	@Override
	protected Node retrieve(Session session, String path) {
		StringBuilder relativePathBuilder = new StringBuilder();
		String repoId = extractRepoId(path, relativePathBuilder);
		// remove starting '/'
		String relativePath = relativePathBuilder.toString().substring(1);

		RpmRepository sourceRepo = null;
		for (Iterator<RpmRepository> reposIt = defaultRepositories.iterator(); reposIt
				.hasNext();) {
			RpmRepository rpmRepo = reposIt.next();
			if (rpmRepo.getId().equals(repoId)) {
				sourceRepo = rpmRepo;
				break;
			}
		}

		if (sourceRepo == null)
			throw new SlcException("No RPM repository found for " + path);

		try {
			String baseUrl = sourceRepo.getUrl();
			String remoteUrl = baseUrl + relativePath;
			Node node = proxyUrl(session, remoteUrl, path);
			if (node != null) {
				registerSource(sourceRepo, node, remoteUrl);
				if (log.isDebugEnabled())
					log.debug("Imported " + remoteUrl + " to " + node);
				return node;
			}
		} catch (Exception e) {
			throw new SlcException("Cannot proxy " + path, e);
		}
		JcrUtils.discardQuietly(session);
		throw new SlcException("No proxy found for " + path);
	}

	protected void registerSource(RpmRepository sourceRepo, Node node,
			String remoteUrl) throws RepositoryException {
		node.addMixin(SlcTypes.SLC_KNOWN_ORIGIN);
		Node origin;
		if (!node.hasNode(SLC_ORIGIN))
			origin = node.addNode(SLC_ORIGIN, SlcTypes.SLC_PROXIED);
		else
			origin = node.getNode(SLC_ORIGIN);

		// proxied repository
		Node proxiedRepository;
		String proxiedRepositoryPath = RepoConstants.PROXIED_REPOSITORIES + '/'
				+ sourceRepo.getId();
		Session session = node.getSession();
		if (session.itemExists(proxiedRepositoryPath)) {
			proxiedRepository = session.getNode(proxiedRepositoryPath);
		} else {
			proxiedRepository = session.getNode(
					RepoConstants.PROXIED_REPOSITORIES).addNode(
					sourceRepo.getId());
			proxiedRepository.addMixin(NodeType.MIX_REFERENCEABLE);
			JcrUtils.urlToAddressProperties(proxiedRepository,
					sourceRepo.getUrl());
			proxiedRepository.setProperty(SLC_URL, sourceRepo.getUrl());
		}

		origin.setProperty(SLC_PROXY, proxiedRepository);
		JcrUtils.urlToAddressProperties(origin, remoteUrl);
	}

	/** Returns the first token of the path */
	protected String extractRepoId(String path, StringBuilder relativePath) {
		StringBuilder workspace = new StringBuilder();
		StringBuilder buf = workspace;
		for (int i = 1; i < path.length(); i++) {
			char c = path.charAt(i);
			if (c == '/') {
				buf = relativePath;
			}
			buf.append(c);
		}
		return workspace.toString();
	}

	@Override
	protected Boolean shouldUpdate(Session clientSession, String nodePath) {
		// if (nodePath.contains("/repodata/"))
		// return true;
		return super.shouldUpdate(clientSession, nodePath);
	}

	public void setDefaultRepositories(Set<RpmRepository> defaultRepositories) {
		this.defaultRepositories = defaultRepositories;
	}
}
