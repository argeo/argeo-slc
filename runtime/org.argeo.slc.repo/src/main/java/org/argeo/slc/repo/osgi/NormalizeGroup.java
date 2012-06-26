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
package org.argeo.slc.repo.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.aether.ArtifactIdComparator;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.ArtifactIndexer;
import org.argeo.slc.repo.JarFileIndexer;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.slc.repo.maven.MavenConventionsUtils;
import org.osgi.framework.Constants;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Make sure that all JCR metadata and Maven metadata are consistent for this
 * group of OSGi bundles.
 */
public class NormalizeGroup implements Runnable, SlcNames {
	public final static String BINARIES_ARTIFACT_ID = "binaries";
	public final static String SOURCES_ARTIFACT_ID = "sources";
	public final static String SDK_ARTIFACT_ID = "sdk";

	private final static Log log = LogFactory.getLog(NormalizeGroup.class);

	private Repository repository;
	private String workspace;
	private String groupId;
	private String artifactBasePath = "/";
	private String version = null;
	private String parentPomCoordinates;

	private List<String> excludedSuffixes = new ArrayList<String>();

	private ArtifactIndexer artifactIndexer = new ArtifactIndexer();
	private JarFileIndexer jarFileIndexer = new JarFileIndexer();

	private List<String> systemPackages = OsgiProfile.PROFILE_JAVA_SE_1_6
			.getSystemPackages();

	// indexes
	private Map<String, String> packagesToSymbolicNames = new HashMap<String, String>();
	private Map<String, Node> symbolicNamesToNodes = new HashMap<String, Node>();

	private Set<Artifact> binaries = new TreeSet<Artifact>(
			new ArtifactIdComparator());
	private Set<Artifact> sources = new TreeSet<Artifact>(
			new ArtifactIdComparator());

	public void run() {
		Session session = null;
		try {
			session = repository.login(workspace);

			Node groupNode = session.getNode(MavenConventionsUtils.groupPath(
					artifactBasePath, groupId));
			// TODO factorize with a traverser pattern?
			for (NodeIterator artifactBases = groupNode.getNodes(); artifactBases
					.hasNext();) {
				Node artifactBase = artifactBases.nextNode();
				if (artifactBase.isNodeType(SlcTypes.SLC_ARTIFACT_BASE)) {
					for (NodeIterator artifactVersions = artifactBase
							.getNodes(); artifactVersions.hasNext();) {
						Node artifactVersion = artifactVersions.nextNode();
						if (artifactVersion
								.isNodeType(SlcTypes.SLC_ARTIFACT_VERSION_BASE))
							for (NodeIterator files = artifactVersion
									.getNodes(); files.hasNext();) {
								Node file = files.nextNode();
								if (file.isNodeType(SlcTypes.SLC_BUNDLE_ARTIFACT)) {
									preProcessBundleArtifact(file);
									file.getSession().save();
									if (log.isDebugEnabled())
										log.debug("Pre-processed "
												+ file.getName());
								}

							}
					}
				}
			}
			// NodeIterator bundlesIt = listBundleArtifacts(session);
			//
			// while (bundlesIt.hasNext()) {
			// Node bundleNode = bundlesIt.nextNode();
			// preProcessBundleArtifact(bundleNode);
			// bundleNode.getSession().save();
			// if (log.isDebugEnabled())
			// log.debug("Pre-processed " + bundleNode.getName());
			// }

			int bundleCount = symbolicNamesToNodes.size();
			if (log.isDebugEnabled())
				log.debug("Indexed " + bundleCount + " bundles");

			int count = 1;
			for (Node bundleNode : symbolicNamesToNodes.values()) {
				processBundleArtifact(bundleNode);
				bundleNode.getSession().save();
				if (log.isDebugEnabled())
					log.debug(count + "/" + bundleCount + " Processed "
							+ bundleNode.getName());
				count++;
			}

			// indexes
			Set<Artifact> indexes = new TreeSet<Artifact>(
					new ArtifactIdComparator());
			Artifact indexArtifact = writeIndex(session, BINARIES_ARTIFACT_ID,
					binaries);
			indexes.add(indexArtifact);
			indexArtifact = writeIndex(session, SOURCES_ARTIFACT_ID, sources);
			indexes.add(indexArtifact);
			// sdk
			writeIndex(session, SDK_ARTIFACT_ID, indexes);
		} catch (Exception e) {
			throw new SlcException("Cannot normalize group " + groupId + " in "
					+ workspace, e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	private Artifact writeIndex(Session session, String artifactId,
			Set<Artifact> artifacts) throws RepositoryException {
		Artifact artifact = new DefaultArtifact(groupId, artifactId, "pom",
				version);
		Artifact parentArtifact = parentPomCoordinates != null ? new DefaultArtifact(
				parentPomCoordinates) : null;
		String pom = MavenConventionsUtils.artifactsAsDependencyPom(artifact,
				artifacts, parentArtifact);
		Node node = RepoUtils.copyBytesAsArtifact(
				session.getNode(artifactBasePath), artifact, pom.getBytes());
		artifactIndexer.index(node);

		// FIXME factorize
		String pomSha = JcrUtils.checksumFile(node, "SHA-1");
		JcrUtils.copyBytesAsFile(node.getParent(), node.getName() + ".sha1",
				pomSha.getBytes());
		session.save();
		return artifact;
	}

	protected void preProcessBundleArtifact(Node bundleNode)
			throws RepositoryException {
		artifactIndexer.index(bundleNode);
		jarFileIndexer.index(bundleNode);

		String symbolicName = JcrUtils.get(bundleNode, SLC_SYMBOLIC_NAME);

		if (symbolicName.endsWith(".source")) {
			// TODO make a shared node with classifier 'sources'
			String bundleName = RepoUtils
					.extractBundleNameFromSourceName(symbolicName);
			for (String excludedSuffix : excludedSuffixes) {
				if (bundleName.endsWith(excludedSuffix))
					return;// skip adding to sources
			}
			sources.add(RepoUtils.asArtifact(bundleNode));
			return;
		}

		NodeIterator exportPackages = bundleNode.getNodes(SLC_
				+ Constants.EXPORT_PACKAGE);
		while (exportPackages.hasNext()) {
			Node exportPackage = exportPackages.nextNode();
			String pkg = JcrUtils.get(exportPackage, SLC_NAME);
			packagesToSymbolicNames.put(pkg, symbolicName);
		}

		symbolicNamesToNodes.put(symbolicName, bundleNode);
		for (String excludedSuffix : excludedSuffixes) {
			if (symbolicName.endsWith(excludedSuffix))
				return;// skip adding to binaries
		}
		binaries.add(RepoUtils.asArtifact(bundleNode));

		if (bundleNode.getSession().hasPendingChanges())
			bundleNode.getSession().save();
	}

	protected void processBundleArtifact(Node bundleNode)
			throws RepositoryException {
		Node artifactFolder = bundleNode.getParent();
		String baseName = FilenameUtils.getBaseName(bundleNode.getName());

		// pom
		String pom = generatePomForBundle(bundleNode);
		String pomName = baseName + ".pom";
		Node pomNode = JcrUtils.copyBytesAsFile(artifactFolder, pomName,
				pom.getBytes());

		// checksum
		String bundleSha = JcrUtils.checksumFile(bundleNode, "SHA-1");
		JcrUtils.copyBytesAsFile(artifactFolder,
				bundleNode.getName() + ".sha1", bundleSha.getBytes());
		String pomSha = JcrUtils.checksumFile(pomNode, "SHA-1");
		JcrUtils.copyBytesAsFile(artifactFolder, pomNode.getName() + ".sha1",
				pomSha.getBytes());
	}

	private String generatePomForBundle(Node n) throws RepositoryException {
		String ownSymbolicName = JcrUtils.get(n, SLC_SYMBOLIC_NAME);

		StringBuffer p = new StringBuffer();

		// XML header
		p.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		p.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n");
		p.append("<modelVersion>4.0.0</modelVersion>");

		// Artifact
		// p.append("<parent><groupId>org.argeo</groupId><artifactId>parent</artifactId><version>1.2.0</version></parent>\n");
		p.append("<groupId>").append(JcrUtils.get(n, SLC_GROUP_ID))
				.append("</groupId>\n");
		p.append("<artifactId>").append(JcrUtils.get(n, SLC_ARTIFACT_ID))
				.append("</artifactId>\n");
		p.append("<version>").append(JcrUtils.get(n, SLC_ARTIFACT_VERSION))
				.append("</version>\n");
		p.append("<packaging>pom</packaging>\n");
		if (n.hasProperty(SLC_ + Constants.BUNDLE_NAME))
			p.append("<name>")
					.append(JcrUtils.get(n, SLC_ + Constants.BUNDLE_NAME))
					.append("</name>\n");
		if (n.hasProperty(SLC_ + Constants.BUNDLE_DESCRIPTION))
			p.append("<description>")
					.append(JcrUtils
							.get(n, SLC_ + Constants.BUNDLE_DESCRIPTION))
					.append("</description>\n");

		// Dependencies
		Set<String> dependenciesSymbolicNames = new TreeSet<String>();
		Set<String> optionalSymbolicNames = new TreeSet<String>();
		NodeIterator importPackages = n.getNodes(SLC_
				+ Constants.IMPORT_PACKAGE);
		while (importPackages.hasNext()) {
			Node importPackage = importPackages.nextNode();
			String pkg = JcrUtils.get(importPackage, SLC_NAME);
			if (packagesToSymbolicNames.containsKey(pkg)) {
				String dependencySymbolicName = packagesToSymbolicNames
						.get(pkg);
				if (JcrUtils.check(importPackage, SLC_OPTIONAL))
					optionalSymbolicNames.add(dependencySymbolicName);
				else
					dependenciesSymbolicNames.add(dependencySymbolicName);
			} else {
				if (!JcrUtils.check(importPackage, SLC_OPTIONAL)
						&& !systemPackages.contains(pkg))
					log.warn("No bundle found for pkg " + pkg);
			}
		}

		if (n.hasNode(SLC_ + Constants.FRAGMENT_HOST)) {
			String fragmentHost = JcrUtils.get(
					n.getNode(SLC_ + Constants.FRAGMENT_HOST),
					SLC_SYMBOLIC_NAME);
			dependenciesSymbolicNames.add(fragmentHost);
		}

		// TODO require bundles

		List<Node> dependencyNodes = new ArrayList<Node>();
		for (String depSymbName : dependenciesSymbolicNames) {
			if (depSymbName.equals(ownSymbolicName))
				continue;// skip self

			if (symbolicNamesToNodes.containsKey(depSymbName))
				dependencyNodes.add(symbolicNamesToNodes.get(depSymbName));
			else
				log.warn("Could not find node for " + depSymbName);
		}
		List<Node> optionalDependencyNodes = new ArrayList<Node>();
		for (String depSymbName : optionalSymbolicNames) {
			if (symbolicNamesToNodes.containsKey(depSymbName))
				optionalDependencyNodes.add(symbolicNamesToNodes
						.get(depSymbName));
			else
				log.warn("Could not find node for " + depSymbName);
		}

		p.append("<dependencies>\n");
		for (Node dependencyNode : dependencyNodes) {
			p.append("<dependency>\n");
			p.append("\t<groupId>")
					.append(JcrUtils.get(dependencyNode, SLC_GROUP_ID))
					.append("</groupId>\n");
			p.append("\t<artifactId>")
					.append(JcrUtils.get(dependencyNode, SLC_ARTIFACT_ID))
					.append("</artifactId>\n");
			p.append("</dependency>\n");
		}

		if (optionalDependencyNodes.size() > 0)
			p.append("<!-- OPTIONAL -->\n");
		for (Node dependencyNode : optionalDependencyNodes) {
			p.append("<dependency>\n");
			p.append("\t<groupId>")
					.append(JcrUtils.get(dependencyNode, SLC_GROUP_ID))
					.append("</groupId>\n");
			p.append("\t<artifactId>")
					.append(JcrUtils.get(dependencyNode, SLC_ARTIFACT_ID))
					.append("</artifactId>\n");
			p.append("\t<optional>true</optional>\n");
			p.append("</dependency>\n");
		}
		p.append("</dependencies>\n");

		// Dependency management
		p.append("<dependencyManagement>\n");
		p.append("<dependencies>\n");
		p.append("<dependency>\n");
		p.append("\t<groupId>").append(groupId).append("</groupId>\n");
		p.append("\t<artifactId>")
				.append(ownSymbolicName.endsWith(".source") ? SOURCES_ARTIFACT_ID
						: BINARIES_ARTIFACT_ID).append("</artifactId>\n");
		p.append("\t<version>").append(version).append("</version>\n");
		p.append("\t<type>pom</type>\n");
		p.append("\t<scope>import</scope>\n");
		p.append("</dependency>\n");
		p.append("</dependencies>\n");
		p.append("</dependencyManagement>\n");

		p.append("</project>\n");
		return p.toString();
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setParentPomCoordinates(String parentPomCoordinates) {
		this.parentPomCoordinates = parentPomCoordinates;
	}

	public void setArtifactBasePath(String artifactBasePath) {
		this.artifactBasePath = artifactBasePath;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setExcludedSuffixes(List<String> excludedSuffixes) {
		this.excludedSuffixes = excludedSuffixes;
	}

}
