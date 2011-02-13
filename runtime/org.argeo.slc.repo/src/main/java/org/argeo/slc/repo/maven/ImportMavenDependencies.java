package org.argeo.slc.repo.maven;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.aether.AetherTemplate;
import org.argeo.slc.aether.AetherUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ImportMavenDependencies implements Runnable {
	private final static Log log = LogFactory
			.getLog(ImportMavenDependencies.class);

	private AetherTemplate aetherTemplate;
	private String rootCoordinates;
	private Set<String> excludedArtifacts = new HashSet<String>();

	public void run() {
		try {
			Artifact pomArtifact = new DefaultArtifact(rootCoordinates);

			// {
			// DependencyNode node = aetherTemplate
			// .resolveDependencies(pomArtifact);
			//
			// PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
			// node.accept(nlg);
			//
			// for (Artifact artifact : nlg.getArtifacts(true)) {
			// log.debug(artifact);
			// }
			// AetherUtils.logDependencyNode(0, node);
			// }

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
		} catch (Exception e) {
			throw new SlcException("Cannot resolve", e);
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

}
