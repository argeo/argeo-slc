package org.argeo.slc.repo.maven.proxy;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.proxy.AbstractUrlProxy;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.repo.RepoConstants;
import org.sonatype.aether.repository.RemoteRepository;

/** Synchronizes the node repository with remote Maven repositories */
public class MavenProxyServiceImpl extends AbstractUrlProxy implements
		MavenProxyService, ArgeoNames, SlcNames {
	private final static Log log = LogFactory
			.getLog(MavenProxyServiceImpl.class);

	private List<RemoteRepository> defaultRepositories = new ArrayList<RemoteRepository>();

	@Override
	protected void beforeInitSessionSave() throws RepositoryException {
		JcrUtils.mkdirs(getJcrAdminSession(), RepoConstants.ARTIFACTS_BASE_PATH);
		Node proxiedRepositories = JcrUtils.mkdirs(getJcrAdminSession(),
				RepoConstants.PROXIED_REPOSITORIES);
		for (RemoteRepository repository : defaultRepositories) {
			if (!proxiedRepositories.hasNode(repository.getId())) {
				Node proxiedRepository = proxiedRepositories.addNode(repository
						.getId());
				proxiedRepository.setProperty(SLC_URL, repository.getUrl());
				proxiedRepository.setProperty(SLC_TYPE,
						repository.getContentType());
			}
		}
	}

	/**
	 * Retrieve and add this file to the repository
	 */
	protected String retrieve(String path) {
		try {
			Node node = null;
			baseUrls: for (String baseUrl : getBaseUrls()) {
				node = proxyUrl(baseUrl, path);
				if (node != null)
					break baseUrls;
			}

			if (node != null)
				return node.getIdentifier();
			else {
				log.warn("Could not proxy " + path);
				return null;
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot proxy " + path, e);
		}
	}

	protected synchronized List<String> getBaseUrls()
			throws RepositoryException {
		List<String> baseUrls = new ArrayList<String>();
		for (NodeIterator nit = getJcrAdminSession().getNode(
				RepoConstants.PROXIED_REPOSITORIES).getNodes(); nit.hasNext();) {
			Node proxiedRepository = nit.nextNode();
			String repoUrl = proxiedRepository.getProperty(SLC_URL).getString();
			baseUrls.add(repoUrl);
		}
		return baseUrls;
	}

	/** The JCR path where this file could be found */
	public String getNodePath(String path) {
		return RepoConstants.ARTIFACTS_BASE_PATH + path;
	}

	public void setDefaultRepositories(
			List<RemoteRepository> defaultRepositories) {
		this.defaultRepositories = defaultRepositories;
	}

}
