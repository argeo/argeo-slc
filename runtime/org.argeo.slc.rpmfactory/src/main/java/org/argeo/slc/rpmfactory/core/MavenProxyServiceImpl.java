/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.rpmfactory.core;

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
import org.argeo.slc.rpmfactory.MavenProxyService;
import org.argeo.slc.rpmfactory.RpmRepository;

/** Synchronizes the node repository with remote Maven repositories */
public class MavenProxyServiceImpl extends AbstractUrlProxy implements
		MavenProxyService, ArgeoNames, SlcNames {
	private final static Log log = LogFactory
			.getLog(MavenProxyServiceImpl.class);

	private List<RpmRepository> defaultRepositories = new ArrayList<RpmRepository>();

	private String artifactsBasePath = RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH;

	/** Inititalizes the artifacts area. */
	@Override
	protected void beforeInitSessionSave(Session session)
			throws RepositoryException {
		JcrUtils.mkdirsSafe(session, RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH);
		Node proxiedRepositories = JcrUtils.mkdirsSafe(session,
				RepoConstants.PROXIED_REPOSITORIES);
		for (RpmRepository repository : defaultRepositories) {
			if (!proxiedRepositories.hasNode(repository.getId())) {
				Node proxiedRepository = proxiedRepositories.addNode(repository
						.getId());
				proxiedRepository.addMixin(NodeType.MIX_REFERENCEABLE);
				JcrUtils.urlToAddressProperties(proxiedRepository,
						repository.getUrl());
				// proxiedRepository.setProperty(SLC_URL, repository.getUrl());
				// proxiedRepository.setProperty(SLC_TYPE,
				// repository.getContentType());
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
		if (artifactsBasePath.equals(RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH))
			return path;
		else
			return artifactsBasePath + path;
	}

	public void setDefaultRepositories(List<RpmRepository> defaultRepositories) {
		this.defaultRepositories = defaultRepositories;
	}

	public void setArtifactsBasePath(String artifactsBasePath) {
		this.artifactsBasePath = artifactsBasePath;
	}

}
