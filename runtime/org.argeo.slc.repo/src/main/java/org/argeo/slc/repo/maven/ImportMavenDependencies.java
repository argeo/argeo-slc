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
import java.io.FileInputStream;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.aether.AetherTemplate;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.ArtifactIndexer;
import org.argeo.slc.repo.JarFileIndexer;
import org.argeo.slc.repo.RepoConstants;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ImportMavenDependencies implements Runnable {
	private final static Log log = LogFactory
			.getLog(ImportMavenDependencies.class);

	private AetherTemplate aetherTemplate;
	private String rootCoordinates;
	private Set<String> excludedArtifacts = new HashSet<String>();

	private Repository repository;
	private String workspace;

	private String artifactBasePath = RepoConstants.ARTIFACTS_BASE_PATH;
	private String distributionsBasePath = RepoConstants.DISTRIBUTIONS_BASE_PATH;
	private String distributionName;

	private ArtifactIndexer artifactIndexer = new ArtifactIndexer();
	private JarFileIndexer jarFileIndexer = new JarFileIndexer();

	public void run() {
		// resolve
		Set<Artifact> artifacts = resolveDistribution();

		// sync
		Session session = null;
		try {
			session = JcrUtils.loginOrCreateWorkspace(repository, workspace);
			// clear
			NodeIterator nit = session.getNode(artifactBasePath).getNodes();
			while (nit.hasNext()) {
				Node node = nit.nextNode();
				if (node.isNodeType(NodeType.NT_FOLDER))
					node.remove();
			}
			session.save();
			syncDistribution(session, artifacts);
		} catch (Exception e) {
			throw new SlcException("Cannot import distribution", e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	public Set<Artifact> resolveDistribution() {
		try {
			Artifact pomArtifact = new DefaultArtifact(rootCoordinates);
			Comparator<Artifact> artifactComparator = new Comparator<Artifact>() {
				public int compare(Artifact o1, Artifact o2) {
					return o1.getArtifactId().compareTo(o2.getArtifactId());
				}
			};

			Set<Artifact> registeredArtifacts = new TreeSet<Artifact>(
					artifactComparator);
			parsePom(aetherTemplate, registeredArtifacts, pomArtifact);
			if (log.isDebugEnabled())
				log.debug("Gathered " + registeredArtifacts.size()
						+ " artifacts");

			// Resolve and add non-optional dependencies
			Set<Artifact> artifacts = new TreeSet<Artifact>(artifactComparator);
			for (Artifact artifact : registeredArtifacts) {
				try {
					addArtifact(artifacts, artifact);
					DependencyNode node = aetherTemplate
							.resolveDependencies(artifact);
					addDependencies(artifacts, node);
				} catch (Exception e) {
					log.error("Could not resolve dependencies of " + artifact
							+ ": " + e.getCause().getMessage());
				}

			}

			if (log.isDebugEnabled())
				log.debug("Resolved " + artifacts.size() + " artifacts");
			Properties distributionDescriptor = new Properties();
			for (Artifact artifact : artifacts) {
				log.debug(artifact.getArtifactId() + " ["
						+ artifact.getVersion() + "]\t(" + artifact + ")");
				distributionDescriptor.setProperty(artifact.getArtifactId()
						+ ":" + artifact.getVersion(), artifact.toString());
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			distributionDescriptor.store(out, "");
			log.debug(new String(out.toByteArray()));
			out.close();

			return artifacts;
		} catch (Exception e) {
			throw new SlcException("Cannot resolve distribution", e);
		}
	}

	protected void syncDistribution(Session jcrSession, Set<Artifact> artifacts) {
		Long begin = System.currentTimeMillis();
		try {
			JcrUtils.mkdirs(jcrSession, artifactBasePath);
			JcrUtils.mkdirs(jcrSession, distributionsBasePath + '/'
					+ distributionName);
			artifacts: for (Artifact artifact : artifacts) {
				File file = artifact.getFile();
				if (file == null) {
					// log.warn("File not found for " + artifact);

					file = artifactToFile(artifact);

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
					Node parentNode;
					if (!jcrSession.itemExists(parentPath)) {
						parentNode = JcrUtils.mkdirs(jcrSession, parentPath,
								NodeType.NT_FOLDER);
					} else {
						parentNode = jcrSession.getNode(parentPath);
					}

					Node fileNode;
					if (!parentNode.hasNode(file.getName())) {
						fileNode = createFileNode(parentNode, file);
					} else {
						fileNode = parentNode.getNode(file.getName());
					}

					if (artifactIndexer.support(fileNode.getPath()))
						artifactIndexer.index(fileNode);
					if (jarFileIndexer.support(fileNode.getPath()))
						jarFileIndexer.index(fileNode);
					jcrSession.save();

					if (fileNode.hasProperty(SlcNames.SLC_SYMBOLIC_NAME)) {
						String distPath = bundleDistributionPath(fileNode);
						if (!jcrSession.itemExists(distPath)
								&& fileNode
										.isNodeType(SlcTypes.SLC_BUNDLE_ARTIFACT))
							jcrSession.getWorkspace().clone(
									jcrSession.getWorkspace().getName(),
									fileNode.getPath(), distPath, false);
						if (log.isDebugEnabled())
							log.debug("Synchronized " + fileNode);
					}
				} catch (Exception e) {
					log.error("Could not synchronize " + artifact, e);
					jcrSession.refresh(false);
					throw e;
				}
			}

			Long duration = (System.currentTimeMillis() - begin) / 1000;
			if (log.isDebugEnabled())
				log.debug("Synchronized distribution in " + duration + "s");
		} catch (Exception e) {
			throw new SlcException("Cannot synchronize distribution", e);
		}
	}

	protected String artifactParentPath(Artifact artifact) {
		return artifact.getGroupId().replace('.', '/') + '/'
				+ artifact.getArtifactId() + '/' + artifact.getVersion();
	}

	protected String bundleDistributionPath(Node fileNode) {
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

	protected File artifactToFile(Artifact artifact) {
		return new File(System.getProperty("user.home")
				+ File.separator
				+ ".m2"
				+ File.separator
				+ "repository"
				+ File.separator
				+ artifact.getGroupId().replace('.', File.separatorChar)
				+ File.separator
				+ artifact.getArtifactId()
				+ File.separator
				+ artifact.getVersion()
				+ File.separator
				+ artifact.getArtifactId()
				+ '-'
				+ artifact.getVersion()
				+ (artifact.getClassifier().equals("") ? ""
						: '-' + artifact.getClassifier()) + '.'
				+ artifact.getExtension());
	}

	private Node createFileNode(Node parentNode, File file) {
		Binary binary = null;
		try {
			Node fileNode = parentNode
					.addNode(file.getName(), NodeType.NT_FILE);
			Node contentNode = fileNode.addNode(Node.JCR_CONTENT,
					NodeType.NT_RESOURCE);
			binary = contentNode.getSession().getValueFactory()
					.createBinary(new FileInputStream(file));
			contentNode.setProperty(Property.JCR_DATA, binary);
			return fileNode;
		} catch (Exception e) {
			throw new SlcException("Cannot create file node based on " + file
					+ " under " + parentNode, e);
		} finally {
			if (binary != null)
				binary.dispose();
		}
	}

	/** Recursively adds non optional dependencies */
	private void addDependencies(Set<Artifact> artifacts, DependencyNode node) {
		for (DependencyNode child : node.getChildren()) {
			if (!child.getDependency().isOptional()) {
				addArtifact(artifacts, child.getDependency().getArtifact());
				addDependencies(artifacts, child);
			}
		}
	}

	private void addArtifact(Set<Artifact> artifacts, Artifact artifact) {
		if (!excludedArtifacts.contains(artifact.getGroupId() + ":"
				+ artifact.getArtifactId()))
			artifacts.add(artifact);
	}

	/**
	 * Directly parses Maven POM XML format in order to find all artifacts
	 * references under the dependency and dependencyManagement tags. This is
	 * meant to migrate existing pom registering a lot of artifacts, not to
	 * replace Maven resolving.
	 */
	protected void parsePom(AetherTemplate aetherTemplate,
			Set<Artifact> artifacts, Artifact pomArtifact) {
		if (log.isDebugEnabled())
			log.debug("Gather dependencies for " + pomArtifact);

		try {
			File file = aetherTemplate.getResolvedFile(pomArtifact);
			DocumentBuilder documentBuilder = DocumentBuilderFactory
					.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(file);

			// properties
			Properties props = new Properties();
			props.setProperty("project.version", pomArtifact.getBaseVersion());
			NodeList properties = doc.getElementsByTagName("properties");
			if (properties.getLength() > 0) {
				NodeList propertiesElems = properties.item(0).getChildNodes();
				for (int i = 0; i < propertiesElems.getLength(); i++) {
					if (propertiesElems.item(i) instanceof Element) {
						Element property = (Element) propertiesElems.item(i);
						props.put(property.getNodeName(),
								property.getTextContent());
					}
				}
			}

			// dependencies (direct and dependencyManagement)
			NodeList dependencies = doc.getElementsByTagName("dependency");
			for (int i = 0; i < dependencies.getLength(); i++) {
				Element dependency = (Element) dependencies.item(i);
				String groupId = dependency.getElementsByTagName("groupId")
						.item(0).getTextContent().trim();
				String artifactId = dependency
						.getElementsByTagName("artifactId").item(0)
						.getTextContent().trim();
				String version = dependency.getElementsByTagName("version")
						.item(0).getTextContent().trim();
				if (version.startsWith("${")) {
					String versionKey = version.substring(0,
							version.length() - 1).substring(2);
					if (!props.containsKey(versionKey))
						throw new SlcException("Cannot interpret version "
								+ version);
					version = props.getProperty(versionKey);
				}
				NodeList scopes = dependency.getElementsByTagName("scope");
				if (scopes.getLength() > 0
						&& scopes.item(0).getTextContent().equals("import")) {
					// recurse
					parsePom(aetherTemplate, artifacts, new DefaultArtifact(
							groupId, artifactId, "pom", version));
				} else {
					// TODO: deal with scope?
					// TODO: deal with type
					String type = "jar";
					Artifact artifact = new DefaultArtifact(groupId,
							artifactId, type, version);
					artifacts.add(artifact);
				}
			}
		} catch (Exception e) {
			throw new SlcException("Cannot process " + pomArtifact, e);
		}
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
