/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.aether.AetherTemplate;
import org.argeo.slc.repo.ArtifactIndexer;
import org.argeo.slc.repo.JarFileIndexer;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.repo.RepoUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Import all the dependencies listed in a POM and their dependency graphs to a
 * workspace.
 */
public class ImportMavenDependencies implements Runnable {
	private final static Log log = LogFactory
			.getLog(ImportMavenDependencies.class);

	private AetherTemplate aetherTemplate;
	private String rootCoordinates = "org.argeo.dep:versions-all:pom:1.2.0";
	private String distCoordinates = "org.argeo.tp:dist:pom:1.3.0";
	private Set<String> excludedArtifacts = new HashSet<String>();

	private Repository repository;
	private String workspace;

	private String artifactBasePath = RepoConstants.ARTIFACTS_BASE_PATH;

	private ArtifactIndexer artifactIndexer = new ArtifactIndexer();
	private JarFileIndexer jarFileIndexer = new JarFileIndexer();
	private Comparator<Artifact> artifactComparator = new Comparator<Artifact>() {
		public int compare(Artifact o1, Artifact o2) {
			return o1.getArtifactId().compareTo(o2.getArtifactId());
		}
	};

	public void run() {
		// resolve
		Set<Artifact> artifacts = resolveDistribution();

		// sync
		sync(artifacts);
	}

	void sync(Set<Artifact> artifacts) {
		Session session = null;
		try {
			session = JcrUtils.loginOrCreateWorkspace(repository, workspace);
			// clear
			NodeIterator nit = session.getNode(artifactBasePath).getNodes();
			while (nit.hasNext()) {
				Node node = nit.nextNode();
				if (node.isNodeType(NodeType.NT_FOLDER)
						|| node.isNodeType(NodeType.NT_UNSTRUCTURED))
					node.remove();
			}
			session.save();
			
			// sync
			syncDistribution(session, artifacts);
		} catch (Exception e) {
			throw new SlcException("Cannot import distribution", e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	/**
	 * Generate a POM with all the artifacts declared in root coordinates as
	 * dependencies AND in dependency management.
	 */
	void createDistPom() {
		try {
			Artifact pomArtifact = new DefaultArtifact(rootCoordinates);

			Set<Artifact> registeredArtifacts = new TreeSet<Artifact>(
					artifactComparator);
			MavenConventionsUtils.gatherPomDependencies(aetherTemplate,
					registeredArtifacts, pomArtifact);
			Artifact sdkArtifact = new DefaultArtifact(distCoordinates);
			String sdkPom = MavenConventionsUtils.artifactsAsDependencyPom(
					sdkArtifact, registeredArtifacts);
			if (log.isDebugEnabled())
				log.debug("Gathered " + registeredArtifacts.size()
						+ " artifacts:\n" + sdkPom);
		} catch (Exception e) {
			throw new SlcException("Cannot resolve distribution", e);
		}
	}

	/** Returns all transitive dependencies of dist POM */
	private Set<Artifact> resolveDistribution() {
		try {
			Artifact distArtifact = new DefaultArtifact(distCoordinates);
			Set<Artifact> artifacts = new TreeSet<Artifact>(artifactComparator);

			DependencyNode node = aetherTemplate
					.resolveDependencies(distArtifact);
			addDependencies(artifacts, node, null);

			if (log.isDebugEnabled()) {
				log.debug("Resolved " + artifacts.size() + " artifacts");

				// Properties distributionDescriptor =
				// generateDistributionDescriptor(artifacts);
				// ByteArrayOutputStream out = new ByteArrayOutputStream();
				// distributionDescriptor.store(out, "");
				// log.debug(new String(out.toByteArray()));
				// out.close();
			}

			/*
			 * for (Artifact artifact : registeredArtifacts) { try { Boolean
			 * wasAdded = addArtifact(artifacts, artifact); if (wasAdded) {
			 * DependencyNode node = aetherTemplate
			 * .resolveDependencies(artifact); addDependencies(artifacts, node,
			 * null); } } catch (Exception e) {
			 * log.error("Could not resolve dependencies of " + artifact + ": "
			 * + e.getCause().getMessage()); }
			 * 
			 * }
			 * 
			 * if (log.isDebugEnabled()) log.debug("Resolved " +
			 * artifacts.size() + " artifacts");
			 * 
			 * // distribution descriptor // Properties distributionDescriptor =
			 * // generateDistributionDescriptor(artifacts); //
			 * ByteArrayOutputStream out = new ByteArrayOutputStream(); //
			 * distributionDescriptor.store(out, ""); // log.debug(new
			 * String(out.toByteArray())); // out.close();
			 */
			return artifacts;
		} catch (Exception e) {
			throw new SlcException("Cannot resolve distribution", e);
		}
	}

	protected Properties generateDistributionDescriptor(Set<Artifact> artifacts) {
		Properties distributionDescriptor = new Properties();
		for (Artifact artifact : artifacts) {
			log.debug(artifact.getArtifactId() + " [" + artifact.getVersion()
					+ "]\t(" + artifact + ")");
			distributionDescriptor.setProperty(artifact.getArtifactId() + ":"
					+ artifact.getVersion(), artifact.toString());
		}
		return distributionDescriptor;
	}

	/** Write artifacts to the target workspace, skipping excluded ones */
	protected void syncDistribution(Session jcrSession, Set<Artifact> artifacts) {
		Set<Artifact> artifactsWithoutSources = new TreeSet<Artifact>(
				artifactComparator);
		Long begin = System.currentTimeMillis();
		try {
			JcrUtils.mkfolders(jcrSession, artifactBasePath);
			artifacts: for (Artifact artifact : artifacts) {
				// skip excluded
				if (excludedArtifacts.contains(artifact.getGroupId() + ":"
						+ artifact.getArtifactId())) {
					if (log.isDebugEnabled())
						log.debug("Exclude " + artifact);
					continue artifacts;
				}

				File jarFile = MavenConventionsUtils.artifactToFile(artifact);
				if (!jarFile.exists()) {
					log.warn("Generated file " + jarFile + " for " + artifact
							+ " does not exist");
					continue artifacts;
				}
				artifact.setFile(jarFile);

				try {
					String parentPath = MavenConventionsUtils
							.artifactParentPath(artifactBasePath, artifact);
					Node parentNode;
					if (!jcrSession.itemExists(parentPath))
						parentNode = JcrUtils.mkfolders(jcrSession, parentPath);
					else
						parentNode = jcrSession.getNode(parentPath);

					Node fileNode;
					if (!parentNode.hasNode(jarFile.getName())) {
						fileNode = createFileNode(parentNode, jarFile);
					} else {
						fileNode = parentNode.getNode(jarFile.getName());
					}

					if (artifactIndexer.support(fileNode.getPath()))
						artifactIndexer.index(fileNode);
					if (jarFileIndexer.support(fileNode.getPath()))
						jarFileIndexer.index(fileNode);
					jcrSession.save();

					addPdeSource(jcrSession, artifact, jarFile,
							artifactsWithoutSources);
					jcrSession.save();

					if (log.isDebugEnabled())
						log.debug("Synchronized " + fileNode);
				} catch (Exception e) {
					log.error("Could not synchronize " + artifact, e);
					jcrSession.refresh(false);
					throw e;
				}
			}

			Long duration = (System.currentTimeMillis() - begin) / 1000;
			if (log.isDebugEnabled()) {
				log.debug("Synchronized distribution in " + duration + "s");
				log.debug("The following artifacts have no sources:");
				for (Artifact artifact : artifactsWithoutSources) {
					log.debug(artifact);
				}
			}
		} catch (Exception e) {
			throw new SlcException("Cannot synchronize distribution", e);
		}
	}

	/** Try to add PDE sources */
	private void addPdeSource(Session session, Artifact artifact,
			File artifactFile, Set<Artifact> artifactsWithoutSources) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			File origSourceFile = null;
			Artifact origSourceArtifact = new DefaultArtifact(
					artifact.getGroupId(), artifact.getArtifactId(), "sources",
					artifact.getExtension(), artifact.getVersion());
			Artifact newSourceArtifact = new DefaultArtifact(
					artifact.getGroupId(),
					artifact.getArtifactId() + ".source",
					artifact.getExtension(), artifact.getVersion());
			try {
				origSourceFile = aetherTemplate
						.getResolvedFile(origSourceArtifact);
			} catch (Exception e) {
				// also try artifact following the conventions
				origSourceArtifact = newSourceArtifact;
				origSourceFile = aetherTemplate
						.getResolvedFile(origSourceArtifact);
			}

			String newSourceParentPath = MavenConventionsUtils
					.artifactParentPath(artifactBasePath, newSourceArtifact);
			Node newSourceParentNode = JcrUtils.mkfolders(session,
					newSourceParentPath);
			NameVersion bundleNameVersion = RepoUtils
					.readNameVersion(artifactFile);
			RepoUtils.packagesAsPdeSource(origSourceFile, bundleNameVersion,
					out);
			String newSourceFileName = MavenConventionsUtils
					.artifactFileName(newSourceArtifact);
			JcrUtils.copyBytesAsFile(newSourceParentNode, newSourceFileName,
					out.toByteArray());
		} catch (Exception e) {
			log.error("Cannot add PDE source for " + artifact + ": " + e);
			artifactsWithoutSources.add(artifact);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	private Node createFileNode(Node parentNode, File file) {
		return JcrUtils.copyFile(parentNode, file);
	}

	/** Recursively adds non optional dependencies */
	private void addDependencies(Set<Artifact> artifacts, DependencyNode node,
			String ancestors) {
		// if (artifacts.contains(node.getDependency().getArtifact()))
		// return;
		String currentArtifactId = node.getDependency().getArtifact()
				.getArtifactId();
		if (log.isDebugEnabled()) {
			log.debug("# Add dependency for " + currentArtifactId);
			if (ancestors != null)
				log.debug(ancestors);
		}
		for (DependencyNode child : node.getChildren()) {
			if (!child.getDependency().isOptional()) {
				if (willAdd(child.getDependency().getArtifact())) {
					addArtifact(artifacts, child.getDependency().getArtifact());
					addDependencies(artifacts, child, currentArtifactId + "\n"
							+ (ancestors != null ? ancestors : ""));
				}
			}
		}
	}

	/** @return whether it was added */
	private Boolean addArtifact(Set<Artifact> artifacts, Artifact artifact) {
		Boolean willAdd = willAdd(artifact);
		if (willAdd)
			artifacts.add(artifact);
		else
			log.info("Skip " + artifact);
		return willAdd;
	}

	private Boolean willAdd(Artifact artifact) {
		Boolean willAdd = true;
		if (excludedArtifacts.contains(artifact.getGroupId() + ":"
				+ artifact.getArtifactId()))
			willAdd = false;
		else if (excludedArtifacts.contains(artifact.getGroupId() + ":*"))
			willAdd = false;
		return willAdd;
	}

	public void setAetherTemplate(AetherTemplate aetherTemplate) {
		this.aetherTemplate = aetherTemplate;
	}

	public void setExcludedArtifacts(Set<String> excludedArtifactIds) {
		this.excludedArtifacts = excludedArtifactIds;
	}

	public void setRootCoordinates(String rootCoordinates) {
		this.rootCoordinates = rootCoordinates;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public void setDistCoordinates(String distCoordinates) {
		this.distCoordinates = distCoordinates;
	}

	public void setArtifactBasePath(String artifactBasePath) {
		this.artifactBasePath = artifactBasePath;
	}

}
