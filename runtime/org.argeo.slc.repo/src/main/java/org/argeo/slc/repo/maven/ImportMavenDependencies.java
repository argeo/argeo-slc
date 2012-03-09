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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.aether.AetherTemplate;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.ArtifactIndexer;
import org.argeo.slc.repo.RepoConstants;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
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

	private Session jcrSession;
	private String artifactBasePath = RepoConstants.ARTIFACTS_BASE_PATH;
	private String distributionsBasePath = RepoConstants.DISTRIBUTIONS_BASE_PATH;
	private String distributionName;

	private ArtifactIndexer artifactIndexer = new ArtifactIndexer();

	public void run() {
		log.debug(jcrSession.getUserID());
		Set<Artifact> artifacts = resolveDistribution();
		syncDistribution(artifacts);
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

	protected void syncDistribution(Set<Artifact> artifacts) {
		Long begin = System.currentTimeMillis();
		try {
			JcrUtils.mkdirs(jcrSession, artifactBasePath);
			JcrUtils.mkdirs(jcrSession, distributionsBasePath + '/'
					+ distributionName);
			artifacts: for (Artifact artifact : artifacts) {
				File file = artifact.getFile();
				if (file == null) {
					log.warn("File not found for " + artifact);

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
					if (fileNode.isNodeType(SlcTypes.SLC_JAR_FILE)) {
						processOsgiBundle(fileNode);
					}
					jcrSession.save();

					if (!jcrSession
							.itemExists(bundleDistributionPath(fileNode))
							&& fileNode
									.isNodeType(SlcTypes.SLC_BUNDLE_ARTIFACT))
						jcrSession.getWorkspace().clone(
								jcrSession.getWorkspace().getName(),
								fileNode.getPath(),
								bundleDistributionPath(fileNode), false);

					if (log.isDebugEnabled())
						log.debug("Synchronized " + fileNode);
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

	protected void processOsgiBundle(Node fileNode) {
		Binary manifestBinary = null;
		InputStream manifestIn = null;
		try {
			manifestBinary = fileNode.getProperty(SlcNames.SLC_MANIFEST)
					.getBinary();
			manifestIn = manifestBinary.getStream();
			Manifest manifest = new Manifest(manifestIn);
			Attributes attrs = manifest.getMainAttributes();

			if (log.isTraceEnabled())
				for (Object key : attrs.keySet())
					log.trace(key + ": " + attrs.getValue(key.toString()));

			if (!attrs.containsKey(new Name(Constants.BUNDLE_SYMBOLICNAME))) {
				log.warn(fileNode + " is not an OSGi bundle");
				return;// not an osgi bundle
			}

			fileNode.addMixin(SlcTypes.SLC_BUNDLE_ARTIFACT);

			// symbolic name
			String symbolicName = attrs.getValue(Constants.BUNDLE_SYMBOLICNAME);
			// make sure there is no directive
			symbolicName = symbolicName.split(";")[0];
			fileNode.setProperty(SlcNames.SLC_SYMBOLIC_NAME, symbolicName);

			// direct mapping
			addAttr(Constants.BUNDLE_SYMBOLICNAME, fileNode, attrs);
			addAttr(Constants.BUNDLE_NAME, fileNode, attrs);
			addAttr(Constants.BUNDLE_DESCRIPTION, fileNode, attrs);
			addAttr(Constants.BUNDLE_MANIFESTVERSION, fileNode, attrs);
			addAttr(Constants.BUNDLE_CATEGORY, fileNode, attrs);
			addAttr(Constants.BUNDLE_ACTIVATIONPOLICY, fileNode, attrs);
			addAttr(Constants.BUNDLE_COPYRIGHT, fileNode, attrs);
			addAttr(Constants.BUNDLE_VENDOR, fileNode, attrs);
			addAttr("Bundle-License", fileNode, attrs);
			addAttr(Constants.BUNDLE_DOCURL, fileNode, attrs);
			addAttr(Constants.BUNDLE_CONTACTADDRESS, fileNode, attrs);
			addAttr(Constants.BUNDLE_ACTIVATOR, fileNode, attrs);
			addAttr(Constants.BUNDLE_UPDATELOCATION, fileNode, attrs);
			addAttr(Constants.BUNDLE_LOCALIZATION, fileNode, attrs);

			// required execution environment
			if (attrs.containsKey(new Name(
					Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT)))
				fileNode.setProperty(
						SlcNames.SLC_
								+ Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT,
						attrs.getValue(
								Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT)
								.split(","));

			// bundle classpath
			if (attrs.containsKey(new Name(Constants.BUNDLE_CLASSPATH)))
				fileNode.setProperty(
						SlcNames.SLC_ + Constants.BUNDLE_CLASSPATH, attrs
								.getValue(Constants.BUNDLE_CLASSPATH)
								.split(","));

			// version
			Version version = new Version(
					attrs.getValue(Constants.BUNDLE_VERSION));
			fileNode.setProperty(SlcNames.SLC_BUNDLE_VERSION,
					version.toString());
			cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.BUNDLE_VERSION);
			Node bundleVersionNode = fileNode.addNode(SlcNames.SLC_
					+ Constants.BUNDLE_VERSION, SlcTypes.SLC_OSGI_VERSION);
			mapOsgiVersion(version, bundleVersionNode);

			// fragment
			cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.FRAGMENT_HOST);
			if (attrs.containsKey(new Name(Constants.FRAGMENT_HOST))) {
				String fragmentHost = attrs.getValue(Constants.FRAGMENT_HOST);
				String[] tokens = fragmentHost.split(";");
				Node node = fileNode.addNode(SlcNames.SLC_
						+ Constants.FRAGMENT_HOST, SlcTypes.SLC_FRAGMENT_HOST);
				node.setProperty(SlcNames.SLC_SYMBOLIC_NAME, tokens[0]);
				for (int i = 1; i < tokens.length; i++) {
					if (tokens[i]
							.startsWith(Constants.BUNDLE_VERSION_ATTRIBUTE)) {
						node.setProperty(SlcNames.SLC_BUNDLE_VERSION,
								attributeValue(tokens[i]));
					}
				}
			}

			// imported packages
			cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.IMPORT_PACKAGE);
			if (attrs.containsKey(new Name(Constants.IMPORT_PACKAGE))) {
				String importPackages = attrs
						.getValue(Constants.IMPORT_PACKAGE);
				List<String> packages = parsePackages(importPackages);
				for (String pkg : packages) {
					String[] tokens = pkg.split(";");
					Node node = fileNode.addNode(SlcNames.SLC_
							+ Constants.IMPORT_PACKAGE,
							SlcTypes.SLC_IMPORTED_PACKAGE);
					node.setProperty(SlcNames.SLC_NAME, tokens[0]);
					for (int i = 1; i < tokens.length; i++) {
						if (tokens[i].startsWith(Constants.VERSION_ATTRIBUTE)) {
							node.setProperty(SlcNames.SLC_VERSION,
									attributeValue(tokens[i]));
						} else if (tokens[i]
								.startsWith(Constants.RESOLUTION_DIRECTIVE)) {
							node.setProperty(
									SlcNames.SLC_OPTIONAL,
									directiveValue(tokens[i]).equals(
											Constants.RESOLUTION_OPTIONAL));
						}
					}
				}
			}

			// dynamic import package
			cleanSubNodes(fileNode, SlcNames.SLC_
					+ Constants.DYNAMICIMPORT_PACKAGE);
			if (attrs.containsKey(new Name(Constants.DYNAMICIMPORT_PACKAGE))) {
				String importPackages = attrs
						.getValue(Constants.DYNAMICIMPORT_PACKAGE);
				List<String> packages = parsePackages(importPackages);
				for (String pkg : packages) {
					String[] tokens = pkg.split(";");
					Node node = fileNode.addNode(SlcNames.SLC_
							+ Constants.DYNAMICIMPORT_PACKAGE,
							SlcTypes.SLC_DYNAMIC_IMPORTED_PACKAGE);
					node.setProperty(SlcNames.SLC_NAME, tokens[0]);
					for (int i = 1; i < tokens.length; i++) {
						if (tokens[i].startsWith(Constants.VERSION_ATTRIBUTE)) {
							node.setProperty(SlcNames.SLC_VERSION,
									attributeValue(tokens[i]));
						}
					}
				}
			}

			// exported packages
			cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.EXPORT_PACKAGE);
			if (attrs.containsKey(new Name(Constants.EXPORT_PACKAGE))) {
				String exportPackages = attrs
						.getValue(Constants.EXPORT_PACKAGE);
				List<String> packages = parsePackages(exportPackages);
				for (String pkg : packages) {
					String[] tokens = pkg.split(";");
					Node node = fileNode.addNode(SlcNames.SLC_
							+ Constants.EXPORT_PACKAGE,
							SlcTypes.SLC_EXPORTED_PACKAGE);
					node.setProperty(SlcNames.SLC_NAME, tokens[0]);
					// TODO: are these cleans really necessary?
					cleanSubNodes(node, SlcNames.SLC_USES);
					cleanSubNodes(node, SlcNames.SLC_VERSION);
					for (int i = 1; i < tokens.length; i++) {
						if (tokens[i].startsWith(Constants.VERSION_ATTRIBUTE)) {
							String versionStr = attributeValue(tokens[i]);
							Node versionNode = node.addNode(
									SlcNames.SLC_VERSION,
									SlcTypes.SLC_OSGI_VERSION);
							mapOsgiVersion(new Version(versionStr), versionNode);
						} else if (tokens[i]
								.startsWith(Constants.USES_DIRECTIVE)) {
							String usedPackages = directiveValue(tokens[i]);
							// log.debug("uses='" + usedPackages + "'");
							for (String usedPackage : usedPackages.split(",")) {
								// log.debug("usedPackage='" + usedPackage +
								// "'");
								Node usesNode = node.addNode(SlcNames.SLC_USES,
										SlcTypes.SLC_JAVA_PACKAGE);
								usesNode.setProperty(SlcNames.SLC_NAME,
										usedPackage);
							}
						}
					}
				}
			}

			// required bundle
			cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.REQUIRE_BUNDLE);
			if (attrs.containsKey(new Name(Constants.REQUIRE_BUNDLE))) {
				String requireBundle = attrs.getValue(Constants.REQUIRE_BUNDLE);
				String[] bundles = requireBundle.split(",");
				for (String bundle : bundles) {
					String[] tokens = bundle.split(";");
					Node node = fileNode.addNode(SlcNames.SLC_
							+ Constants.REQUIRE_BUNDLE,
							SlcTypes.SLC_REQUIRED_BUNDLE);
					node.setProperty(SlcNames.SLC_SYMBOLIC_NAME, tokens[0]);
					for (int i = 1; i < tokens.length; i++) {
						if (tokens[i]
								.startsWith(Constants.BUNDLE_VERSION_ATTRIBUTE)) {
							node.setProperty(SlcNames.SLC_BUNDLE_VERSION,
									attributeValue(tokens[i]));
						} else if (tokens[i]
								.startsWith(Constants.RESOLUTION_DIRECTIVE)) {
							node.setProperty(
									SlcNames.SLC_OPTIONAL,
									directiveValue(tokens[i]).equals(
											Constants.RESOLUTION_OPTIONAL));
						}
					}
				}
			}
		} catch (Exception e) {
			throw new SlcException("Cannot process OSGi bundle " + fileNode, e);
		} finally {
			if (manifestBinary != null)
				manifestBinary.dispose();
			IOUtils.closeQuietly(manifestIn);
		}
	}

	/** Parse package list with nested directive with ',' */
	private List<String> parsePackages(String str) {
		List<String> res = new ArrayList<String>();
		StringBuffer curr = new StringBuffer("");
		boolean in = false;
		for (char c : str.toCharArray()) {
			if (c == ',') {
				if (!in) {
					res.add(curr.toString());
					curr = new StringBuffer("");
				}
			} else if (c == '\"') {
				in = !in;
				curr.append(c);
			} else {
				curr.append(c);
			}
		}
		res.add(curr.toString());
		log.debug(res);
		return res;
	}

	private void addAttr(String key, Node node, Attributes attrs)
			throws RepositoryException {
		addAttr(new Name(key), node, attrs);
	}

	private void addAttr(Name key, Node node, Attributes attrs)
			throws RepositoryException {
		if (attrs.containsKey(key)) {
			String value = attrs.getValue(key);
			node.setProperty(SlcNames.SLC_ + key, value);
		}
	}

	private void cleanSubNodes(Node node, String name)
			throws RepositoryException {
		if (node.hasNode(name)) {
			NodeIterator nit = node.getNodes(name);
			while (nit.hasNext())
				nit.nextNode().remove();
		}
	}

	protected void mapOsgiVersion(Version version, Node versionNode)
			throws RepositoryException {
		versionNode.setProperty(SlcNames.SLC_AS_STRING, version.toString());
		versionNode.setProperty(SlcNames.SLC_MAJOR, version.getMajor());
		versionNode.setProperty(SlcNames.SLC_MINOR, version.getMinor());
		versionNode.setProperty(SlcNames.SLC_MICRO, version.getMicro());
		if (!version.getQualifier().equals(""))
			versionNode.setProperty(SlcNames.SLC_QUALIFIER,
					version.getQualifier());
	}

	private String attributeValue(String str) {
		return extractValue(str, "=");
	}

	private String directiveValue(String str) {
		return extractValue(str, ":=");
	}

	private String extractValue(String str, String eq) {
		String[] tokens = str.split(eq);
		// String key = tokens[0];
		String value = tokens[1].trim();
		// TODO: optimize?
		if (value.startsWith("\""))
			value = value.substring(1);
		if (value.endsWith("\""))
			value = value.substring(0, value.length() - 1);
		return value;
	}

	protected Node createFileNode(Node parentNode, File file) {
		Binary binary = null;
		try {
			Node fileNode = parentNode
					.addNode(file.getName(), NodeType.NT_FILE);
			Node contentNode = fileNode.addNode(Node.JCR_CONTENT,
					NodeType.NT_RESOURCE);
			binary = jcrSession.getValueFactory().createBinary(
					new FileInputStream(file));
			contentNode.setProperty(Property.JCR_DATA, binary);
			// jar file
			if (FilenameUtils.isExtension(file.getName(), "jar")) {
				JarInputStream jarIn = null;
				ByteArrayOutputStream bo = null;
				ByteArrayInputStream bi = null;
				Binary manifestBinary = null;
				try {
					jarIn = new JarInputStream(binary.getStream());
					Manifest manifest = jarIn.getManifest();
					bo = new ByteArrayOutputStream();
					manifest.write(bo);
					bi = new ByteArrayInputStream(bo.toByteArray());
					manifestBinary = jcrSession.getValueFactory().createBinary(
							bi);
					fileNode.addMixin(SlcTypes.SLC_JAR_FILE);
					fileNode.setProperty(SlcNames.SLC_MANIFEST, manifestBinary);
					Attributes attrs = manifest.getMainAttributes();
					addAttr(Attributes.Name.MANIFEST_VERSION, fileNode, attrs);
					addAttr(Attributes.Name.SIGNATURE_VERSION, fileNode, attrs);
					addAttr(Attributes.Name.CLASS_PATH, fileNode, attrs);
					addAttr(Attributes.Name.MAIN_CLASS, fileNode, attrs);
					addAttr(Attributes.Name.EXTENSION_NAME, fileNode, attrs);
					addAttr(Attributes.Name.IMPLEMENTATION_VERSION, fileNode,
							attrs);
					addAttr(Attributes.Name.IMPLEMENTATION_VENDOR, fileNode,
							attrs);
					addAttr(Attributes.Name.IMPLEMENTATION_VENDOR_ID, fileNode,
							attrs);
					addAttr(Attributes.Name.SPECIFICATION_TITLE, fileNode,
							attrs);
					addAttr(Attributes.Name.SPECIFICATION_VERSION, fileNode,
							attrs);
					addAttr(Attributes.Name.SPECIFICATION_VENDOR, fileNode,
							attrs);
					addAttr(Attributes.Name.SEALED, fileNode, attrs);
				} finally {
					if (manifestBinary != null)
						manifestBinary.dispose();
					IOUtils.closeQuietly(bi);
					IOUtils.closeQuietly(bo);
					IOUtils.closeQuietly(jarIn);
				}
			}
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

	public void setJcrSession(Session jcrSession) {
		this.jcrSession = jcrSession;
	}

	public void setDistributionName(String distributionName) {
		this.distributionName = distributionName;
	}

}
