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
package org.argeo.slc.client.ui.dist.commands;

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
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.ArtifactIndexer;
import org.argeo.slc.repo.JarFileIndexer;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.osgi.framework.Constants;

public class NormalizeDistribution extends AbstractHandler implements SlcNames {
	private final static Log log = LogFactory
			.getLog(NormalizeDistribution.class);

	private Repository repository;
	private String workspace;
	private String groupId;

	private ArtifactIndexer artifactIndexer = new ArtifactIndexer();
	private JarFileIndexer jarFileIndexer = new JarFileIndexer();

	// indexes
	private Map<String, String> packagesToSymbolicNames = new HashMap<String, String>();
	private Map<String, Node> symbolicNamesToNodes = new HashMap<String, Node>();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Session session = null;
		try {
			session = repository.login(workspace);
			NodeIterator bundlesIt = listBundleArtifacts(session);

			while (bundlesIt.hasNext()) {
				Node bundleNode = bundlesIt.nextNode();
				preProcessBundleArtifact(bundleNode);
				bundleNode.getSession().save();
				if (log.isDebugEnabled())
					log.debug("Pre-processed " + bundleNode.getName());
			}

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
		} catch (Exception e) {
			ErrorFeedback.show("Cannot normalize distribution " + workspace, e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}

		return null;
	}

	protected void preProcessBundleArtifact(Node bundleNode)
			throws RepositoryException {
		artifactIndexer.index(bundleNode);
		jarFileIndexer.index(bundleNode);

		String symbolicName = JcrUtils.get(bundleNode, SLC_SYMBOLIC_NAME);

		if (symbolicName.endsWith(".source")) {
			// TODO make a shared node with classifier 'sources'
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
		String pomSha = JcrUtils.checksumFile(bundleNode, "SHA-1");
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
				if (!JcrUtils.check(importPackage, SLC_OPTIONAL))
					log.warn("No bundle found for pkg " + pkg);
			}
		}

		if (n.hasNode(SLC_ + Constants.FRAGMENT_HOST)) {
			String fragmentHost = JcrUtils.get(
					n.getNode(SLC_ + Constants.FRAGMENT_HOST),
					SLC_SYMBOLIC_NAME);
			dependenciesSymbolicNames.add(fragmentHost);
		}

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
		// TODO import SDK
		p.append("</dependencies>\n");
		p.append("</dependencyManagement>\n");

		p.append("</project>\n");
		return p.toString();
	}

	static NodeIterator listBundleArtifacts(Session session)
			throws RepositoryException {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		QueryObjectModelFactory factory = queryManager.getQOMFactory();

		final String bundleArtifactsSelector = "bundleArtifacts";
		Selector source = factory.selector(SlcTypes.SLC_BUNDLE_ARTIFACT,
				bundleArtifactsSelector);

		Ordering order = factory.ascending(factory.propertyValue(
				bundleArtifactsSelector, SlcNames.SLC_SYMBOLIC_NAME));
		Ordering[] orderings = { order };

		QueryObjectModel query = factory.createQuery(source, null, orderings,
				null);

		QueryResult result = query.execute();
		return result.getNodes();
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

}