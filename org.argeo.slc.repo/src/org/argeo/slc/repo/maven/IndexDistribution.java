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
package org.argeo.slc.repo.maven;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.RepoConstants;
import org.sonatype.aether.artifact.Artifact;

/** Create a distribution node from a set of artifacts */
public class IndexDistribution implements Runnable {
	private final static Log log = LogFactory.getLog(IndexDistribution.class);
	private Repository repository;
	private String workspace;

	private String artifactBasePath = RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH;
	private String distributionsBasePath = RepoConstants.DISTRIBUTIONS_BASE_PATH;
	private String distributionName;

	public void run() {
		// TODO populate
		Set<Artifact> artifacts = new HashSet<Artifact>();

		// sync
		Session session = null;
		try {
			session = repository.login(workspace);
			syncDistribution(session, artifacts);
		} catch (Exception e) {
			throw new SlcException("Cannot import distribution", e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	protected void syncDistribution(Session jcrSession, Set<Artifact> artifacts) {
		Long begin = System.currentTimeMillis();
		try {
			JcrUtils.mkdirs(jcrSession, distributionsBasePath + '/'
					+ distributionName);
			artifacts: for (Artifact artifact : artifacts) {
				File file = artifact.getFile();
				if (file == null) {
					file = MavenConventionsUtils.artifactToFile(artifact);
					if (!file.exists()) {
						log.warn("Generated file " + file + " for " + artifact
								+ " does not exist");
						continue artifacts;
					}
				}

				try {
					String parentPath = artifactBasePath
							+ (artifactBasePath.endsWith("/") ? "" : "/")
							+ artifactParentPath(artifact);
					Node parentNode = jcrSession.getNode(parentPath);
					Node fileNode = parentNode.getNode(file.getName());

					if (fileNode.hasProperty(SlcNames.SLC_SYMBOLIC_NAME)) {
						String distPath = bundleDistributionPath(fileNode);
						if (!jcrSession.itemExists(distPath)
								&& fileNode
										.isNodeType(SlcTypes.SLC_BUNDLE_ARTIFACT))
							jcrSession.getWorkspace().clone(
									jcrSession.getWorkspace().getName(),
									fileNode.getPath(), distPath, false);
						if (log.isDebugEnabled())
							log.debug("Indexed " + fileNode);
					}
				} catch (Exception e) {
					log.error("Could not index " + artifact, e);
					jcrSession.refresh(false);
					throw e;
				}
			}

			Long duration = (System.currentTimeMillis() - begin) / 1000;
			if (log.isDebugEnabled())
				log.debug("Indexed distribution in " + duration + "s");
		} catch (Exception e) {
			throw new SlcException("Cannot synchronize distribution", e);
		}
	}

	private String artifactParentPath(Artifact artifact) {
		return artifact.getGroupId().replace('.', '/') + '/'
				+ artifact.getArtifactId() + '/' + artifact.getVersion();
	}

	private String bundleDistributionPath(Node fileNode) {
		try {
			return distributionsBasePath
					+ '/'
					+ distributionName
					+ '/'
					+ fileNode.getProperty(SlcNames.SLC_SYMBOLIC_NAME)
							.getString()
					+ '_'
					+ fileNode.getProperty(SlcNames.SLC_BUNDLE_VERSION)
							.getString();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot create distribution path for "
					+ fileNode, e);
		}
	}

	public void setDistributionName(String distributionName) {
		this.distributionName = distributionName;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

}
