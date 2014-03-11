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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoMonitor;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.aether.ArtifactIdComparator;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.ArtifactIndexer;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.slc.repo.osgi.OsgiProfile;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Make sure that all JCR metadata and Maven metadata are consistent for this
 * group of OSGi bundles.
 */
public class GenerateBinaries implements Runnable, SlcNames {
	private final static Log log = LogFactory.getLog(GenerateBinaries.class);

	// Connection info
	private Repository repository;
	private Credentials credentials;
	private String workspace;

	// Business info
	private String groupId;
	private String parentPomCoordinates;
	private Boolean overridePoms = false;
	private String version = null;

	// Constants
	private String artifactBasePath = RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH;
	private List<String> excludedSuffixes = new ArrayList<String>();
	/** TODO make it more generic */
	private List<String> systemPackages = OsgiProfile.PROFILE_JAVA_SE_1_6
			.getSystemPackages();

	private ArtifactIndexer artifactIndexer = new ArtifactIndexer();

	// Local indexes
	private Map<String, String> packagesToSymbolicNames = new HashMap<String, String>();
	private Map<String, Node> symbolicNamesToNodes = new HashMap<String, Node>();
	private Set<Artifact> binaries = new TreeSet<Artifact>(
			new ArtifactIdComparator());
	private Set<Artifact> sources = new TreeSet<Artifact>(
			new ArtifactIdComparator());
	private Node allArtifactsHighestVersion;

	public void run() {
		Session session = null;
		try {
			session = repository.login(workspace);
			Node groupNode = session.getNode(MavenConventionsUtils.groupPath(
					artifactBasePath, groupId));
			internalPreProcessing(groupNode, null);
			processGroupNode(groupNode, null);
		} catch (Exception e) {
			throw new SlcException("Cannot normalize group " + groupId + " in "
					+ workspace, e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	public static void processGroupNode(Node groupNode, String version,
			Boolean overridePoms, ArgeoMonitor monitor)
			throws RepositoryException {
		// TODO set artifactsBase based on group node
		GenerateBinaries gb = new GenerateBinaries();
		String groupId = groupNode.getProperty(SlcNames.SLC_GROUP_BASE_ID)
				.getString();
		gb.setGroupId(groupId);
		gb.setVersion(version);
		gb.setOverridePoms(overridePoms);
		// TODO use already done pre-processing
		gb.internalPreProcessing(groupNode, monitor);
		gb.processGroupNode(groupNode, monitor);
	}

	/** Only builds local indexes. Does not change anything in the local Session */
	public static GenerateBinaries preProcessGroupNode(Node groupNode,
			ArgeoMonitor monitor) throws RepositoryException {
		// TODO set artifactsBase based on group node
		GenerateBinaries gb = new GenerateBinaries();
		String groupId = groupNode.getProperty(SlcNames.SLC_GROUP_BASE_ID)
				.getString();
		gb.setGroupId(groupId);
		// gb.setVersion(version);
		// gb.setOverridePoms(overridePoms);
		gb.internalPreProcessing(groupNode, monitor);
		return gb;
	}

	// exposes indexes. to display results of the pre-processing phase.
	public Set<Artifact> getBinaries() {
		return binaries;
	}

	public Artifact getHighestArtifactVersion() throws RepositoryException {
		return allArtifactsHighestVersion == null ? null : RepoUtils
				.asArtifact(allArtifactsHighestVersion);
	}

	protected void internalPreProcessing(Node groupNode, ArgeoMonitor monitor)
			throws RepositoryException {
		if (monitor != null)
			monitor.subTask("Pre processing group " + groupId);

		// Process all direct children nodes,
		// gathering latest versions of all artifact base
		allArtifactsHighestVersion = null;
		// Session session = groupNode.getSession();
		aBases: for (NodeIterator aBases = groupNode.getNodes(); aBases
				.hasNext();) {
			Node aBase = aBases.nextNode();
			if (aBase.isNodeType(SlcTypes.SLC_ARTIFACT_BASE)) {
				Node highestAVersion = getArtifactLatestVersion(aBase);
				if (highestAVersion == null)
					continue aBases;
				else {
					// retrieve relevant child node
					for (NodeIterator files = highestAVersion.getNodes(); files
							.hasNext();) {
						Node file = files.nextNode();
						if (file.isNodeType(SlcTypes.SLC_BUNDLE_ARTIFACT)) {
							preProcessBundleArtifact(file);
							if (log.isDebugEnabled())
								log.debug("Pre-processed " + file.getName());
						}
					}
				}
			}
		}
		if (log.isDebugEnabled()) {
			int bundleCount = symbolicNamesToNodes.size();
			log.debug("" + bundleCount + " bundles have been indexed for "
					+ groupId);
		}
	}

	/** Does the real job : writes JCR META-DATA and generates binaries */
	protected void processGroupNode(Node groupNode, ArgeoMonitor monitor)
			throws RepositoryException {
		if (monitor != null)
			monitor.subTask("Processing group " + groupId);

		Session session = groupNode.getSession();

		// if version not set or empty, use the highest version
		// useful when indexing a product maven repository where
		// all artifacts have the same version for a given release
		// => the version can then be left empty
		if (version == null || version.trim().equals(""))
			if (allArtifactsHighestVersion != null)
				version = allArtifactsHighestVersion.getProperty(
						SLC_ARTIFACT_VERSION).getString();
			else
				throw new SlcException("Group version " + version
						+ " is empty.");

		int bundleCount = symbolicNamesToNodes.size();

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
		Artifact indexArtifact = writeIndex(session,
				RepoConstants.BINARIES_ARTIFACT_ID, binaries);
		indexes.add(indexArtifact);
		indexArtifact = writeIndex(session, RepoConstants.SOURCES_ARTIFACT_ID,
				sources);
		indexes.add(indexArtifact);
		// sdk
		writeIndex(session, RepoConstants.SDK_ARTIFACT_ID, indexes);
		if (monitor != null)
			monitor.worked(1);
	}

	// Helpers
	private Node getArtifactLatestVersion(Node artifactBase) {
		try {
			Node highestAVersion = null;
			for (NodeIterator aVersions = artifactBase.getNodes(); aVersions
					.hasNext();) {
				Node aVersion = aVersions.nextNode();
				if (aVersion.isNodeType(SlcTypes.SLC_ARTIFACT_VERSION_BASE)) {
					if (highestAVersion == null) {
						highestAVersion = aVersion;
						if (allArtifactsHighestVersion == null)
							allArtifactsHighestVersion = aVersion;
						// Correctly handle following arrival order:
						// Name1 - V1, name2 - V3
						else {
							Version cachedHighestVersion = extractOsgiVersion(allArtifactsHighestVersion);
							Version currVersion = extractOsgiVersion(aVersion);
							if (currVersion.compareTo(cachedHighestVersion) > 0)
								allArtifactsHighestVersion = aVersion;
						}
					} else {
						Version currVersion = extractOsgiVersion(aVersion);
						Version currentHighestVersion = extractOsgiVersion(highestAVersion);
						if (currVersion.compareTo(currentHighestVersion) > 0) {
							highestAVersion = aVersion;
						}
						if (currVersion
								.compareTo(extractOsgiVersion(allArtifactsHighestVersion)) > 0) {
							allArtifactsHighestVersion = aVersion;
						}
					}

				}
			}
			return highestAVersion;
		} catch (RepositoryException re) {
			throw new SlcException("Unable to get latest version for node "
					+ artifactBase, re);
		}
	}

	private Version extractOsgiVersion(Node artifactVersion)
			throws RepositoryException {
		String rawVersion = artifactVersion.getProperty(SLC_ARTIFACT_VERSION)
				.getString();
		String cleanVersion = rawVersion.replace("-SNAPSHOT", ".SNAPSHOT");
		return new Version(cleanVersion);
	}

	protected void preProcessBundleArtifact(Node bundleNode)
			throws RepositoryException {

		String symbolicName = JcrUtils.get(bundleNode, SLC_SYMBOLIC_NAME);
		if (symbolicName.endsWith(".source")) {
			// TODO make a shared node with classifier 'sources'?
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
			throw new SlcException("Pending changes in the session, "
					+ "this should not be true here.");
		// bundleNode.getSession().save();
	}

	protected void processBundleArtifact(Node bundleNode)
			throws RepositoryException {
		Node artifactFolder = bundleNode.getParent();
		String baseName = FilenameUtils.getBaseName(bundleNode.getName());

		// pom
		String pomName = baseName + ".pom";
		if (artifactFolder.hasNode(pomName) && !overridePoms)
			return;// skip

		String pom = generatePomForBundle(bundleNode);
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

	// Writers
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

		// TODO factorize
		String pomSha = JcrUtils.checksumFile(node, "SHA-1");
		JcrUtils.copyBytesAsFile(node.getParent(), node.getName() + ".sha1",
				pomSha.getBytes());
		String pomMd5 = JcrUtils.checksumFile(node, "MD5");
		JcrUtils.copyBytesAsFile(node.getParent(), node.getName() + ".md5",
				pomMd5.getBytes());
		session.save();
		return artifact;
	}

	private String generatePomForBundle(Node n) throws RepositoryException {
		String ownSymbolicName = JcrUtils.get(n, SLC_SYMBOLIC_NAME);

		StringBuffer p = new StringBuffer();

		// XML header
		p.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		p.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n");
		p.append("<modelVersion>4.0.0</modelVersion>");

		// Artifact
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
				.append(ownSymbolicName.endsWith(".source") ? RepoConstants.SOURCES_ARTIFACT_ID
						: RepoConstants.BINARIES_ARTIFACT_ID)
				.append("</artifactId>\n");
		p.append("\t<version>").append(version).append("</version>\n");
		p.append("\t<type>pom</type>\n");
		p.append("\t<scope>import</scope>\n");
		p.append("</dependency>\n");
		p.append("</dependencies>\n");
		p.append("</dependencyManagement>\n");

		p.append("</project>\n");
		return p.toString();
	}

	/* SETTERS */
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

	public void setOverridePoms(Boolean overridePoms) {
		this.overridePoms = overridePoms;
	}

	public void setArtifactIndexer(ArtifactIndexer artifactIndexer) {
		this.artifactIndexer = artifactIndexer;
	}
}