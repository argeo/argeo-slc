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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.proxy.AbstractUrlProxy;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.rpmfactory.RpmProxyService;
import org.argeo.slc.rpmfactory.RpmRepository;

/** Synchronises the node repository with remote Maven repositories */
public class RpmProxyServiceImpl extends AbstractUrlProxy implements
		RpmProxyService, ArgeoNames, SlcNames {
	private final static Log log = LogFactory.getLog(RpmProxyServiceImpl.class);

	private Set<RpmRepository> defaultRepositories = new HashSet<RpmRepository>();

	/**
	 * Retrieve and add this file to the repository
	 */
	@Override
	protected Node retrieve(Session session, String path) {
		StringBuilder relativePathBuilder = new StringBuilder();
		String repoId = extractRepoId(path, relativePathBuilder);
		String relativePath = relativePathBuilder.toString();

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
			// if (session.hasPendingChanges())
			// throw new SlcException("Session has pending changed");
			String baseUrl = sourceRepo.getUrl();
			String remoteUrl = baseUrl + relativePath;
			Node node = proxyUrl(session, remoteUrl, path);
			if (node != null) {
				node.addMixin(SlcTypes.SLC_KNOWN_ORIGIN);
				Node origin;
				if (!node.hasNode(SLC_ORIGIN))
					origin = node.addNode(SLC_ORIGIN, SlcTypes.SLC_PROXIED);
				else
					origin = node.getNode(SLC_ORIGIN);
				// origin.setProperty(SLC_PROXY, sourceRepo.getId());
				JcrUtils.urlToAddressProperties(origin, remoteUrl);

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
		if (nodePath.contains("/repodata/"))
			return true;
		return super.shouldUpdate(clientSession, nodePath);
	}

	public void setDefaultRepositories(Set<RpmRepository> defaultRepositories) {
		this.defaultRepositories = defaultRepositories;
	}
}
