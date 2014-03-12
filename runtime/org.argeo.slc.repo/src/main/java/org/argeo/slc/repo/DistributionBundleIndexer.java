package org.argeo.slc.repo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.nodetype.NodeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.aether.AetherUtils;
import org.argeo.slc.aether.ArtifactIdComparator;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Create or update JCR meta-data for modularDistribution bundles: <list><li>
 * .jar: dependency artifacts with csv index</li> <li>.pom: group index artifact
 * (binaries, sources) with tag dependency management</li> </list>
 */
public class DistributionBundleIndexer implements NodeIndexer {
	private final static Log log = LogFactory
			.getLog(DistributionBundleIndexer.class);

	private final static String INDEX_FILE_NAME = "modularDistribution.csv";
	private final static List<String> BINARIES_ARTIFACTS_NAME;
	static {
		List<String> tmpList = new ArrayList<String>();
		tmpList.add(RepoConstants.BINARIES_ARTIFACT_ID);
		//tmpList.add(RepoConstants.SOURCES_ARTIFACT_ID);
		//tmpList.add(RepoConstants.SDK_ARTIFACT_ID);
		BINARIES_ARTIFACTS_NAME = Collections.unmodifiableList(tmpList);
	}

	private Manifest manifest;
	// private String symbolicName;
	// private String version;

	// private List<Artifact> artifacts;
	private String separator = ",";

	private Comparator<Artifact> artifactComparator = new ArtifactIdComparator();

	// private Set<Artifact> artifacts = new
	// TreeSet<Artifact>(artifactComparator);

	public Boolean support(String path) {
		if (FilenameUtils.getExtension(path).equals("jar"))
			return true;

		if (FilenameUtils.getExtension(path).equals("pom")
				&& BINARIES_ARTIFACTS_NAME.contains(FilenameUtils.getName(path)
						.split("-")[0]))
			return true;
		return false;
	}

	public void index(Node fileNode) {
		// JarInputStream jarIn = null;
		Binary fileBinary = null;
		try {

			String fileNodePath = fileNode.getPath();
			if (!support(fileNodePath))
				return;

			if (!fileNode.isNodeType(NodeType.NT_FILE))
				return;

			// Session jcrSession = fileNode.getSession();
			Node contentNode = fileNode.getNode(Node.JCR_CONTENT);
			fileBinary = contentNode.getProperty(Property.JCR_DATA).getBinary();

			Set<Artifact> artifacts = new TreeSet<Artifact>(artifactComparator);

			if (FilenameUtils.getExtension(fileNode.getPath()).equals("jar"))
				listModulesFromCsvIndex(artifacts, fileNode, fileBinary);
			else if (FilenameUtils.getExtension(fileNode.getPath()).equals(
					"pom"))
				listModulesFromPomIndex(artifacts, fileNode, fileBinary);

			if (artifacts.isEmpty())
				return; // no modules found
			else {
				Node modules;
				if (fileNode.isNodeType(SlcTypes.SLC_MODULAR_DISTRIBUTION)) {
					modules = fileNode.getNode(SlcNames.SLC_MODULES);
				} else {
					fileNode.addMixin(SlcTypes.SLC_MODULAR_DISTRIBUTION);
					modules = JcrUtils.mkdirs(fileNode, SlcNames.SLC_MODULES,
							NodeType.NT_UNSTRUCTURED);
				}

				for (Artifact artifact : artifacts) {
					// TODO clean this once an overwrite policy has been
					// decided.
					if (!modules.hasNode(artifact.getArtifactId())) {
						Node moduleCoord = modules.addNode(
								artifact.getArtifactId(),
								SlcTypes.SLC_MODULE_COORDINATES);
						moduleCoord.setProperty(SlcNames.SLC_NAME,
								artifact.getArtifactId());
						moduleCoord.setProperty(SlcNames.SLC_VERSION,
								artifact.getVersion());
						String groupId = artifact.getGroupId();
						if (groupId != null && !"".equals(groupId.trim()))
							moduleCoord.setProperty(SlcNames.SLC_CATEGORY,
									artifact.getGroupId());
					}
				}

			}

			// find base URL
			// won't work if distribution artifact is not listed
			// for (int i = 0; i < artifacts.size(); i++) {
			// OsgiArtifact osgiArtifact = artifacts.get(i);
			// if (osgiArtifact.getSymbolicName().equals(symbolicName)
			// && osgiArtifact.getVersion().equals(version)) {
			// String relativeUrl = osgiArtifact.getRelativeUrl();
			// if (url.endsWith(relativeUrl)) {
			// baseUrl = url.substring(0, url.length()
			// - osgiArtifact.getRelativeUrl().length());
			// break;
			// }
			// }
			// }
		} catch (Exception e) {
			throw new SlcException("Cannot list dependencies from " + fileNode,
					e);
		}
	}

	protected void listModulesFromCsvIndex(Set<Artifact> artifacts,
			Node fileNode, Binary fileBinary) {
		// List<Artifact> artifacts = new ArrayList<Artifact>();
		JarInputStream jarIn = null;
		BufferedReader reader = null;

		try {
			jarIn = new JarInputStream(fileBinary.getStream());

			// meta data
			// symbolicName = manifest.getMainAttributes().getValue(
			// Constants.BUNDLE_SYMBOLICNAME);
			// version = manifest.getMainAttributes().getValue(
			// Constants.BUNDLE_VERSION);

			manifest = jarIn.getManifest();
			if (manifest == null) {
				log.error(fileNode + " has no MANIFEST");
				return;
			}

			JarEntry indexEntry;
			while ((indexEntry = jarIn.getNextJarEntry()) != null) {
				String entryName = indexEntry.getName();
				if (entryName.equals(INDEX_FILE_NAME)) {
					break;
				}
				jarIn.closeEntry();
			}

			if (indexEntry == null)
				return; // Not a modular definition artifact

			reader = new BufferedReader(new InputStreamReader(jarIn));
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, separator);
				st.nextToken(); // moduleName
				st.nextToken(); // moduleVersion
				String relativeUrl = st.nextToken();

				artifacts.add(AetherUtils.convertPathToArtifact(relativeUrl,
						null));
				if (log.isTraceEnabled())
					log.debug("Processed dependency: " + line);
			}
		} catch (Exception e) {
			throw new SlcException("Cannot list artifacts", e);
		} finally {
			IOUtils.closeQuietly(jarIn);
			IOUtils.closeQuietly(reader);
		}
	}

	protected void listModulesFromPomIndex(Set<Artifact> artifacts,
			Node fileNode, Binary fileBinary) {
		InputStream input = null;
		try {
			input = fileBinary.getStream();

			DocumentBuilder documentBuilder = DocumentBuilderFactory
					.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(input);

			// properties
			Properties props = new Properties();
			// props.setProperty("project.version",
			// pomArtifact.getBaseVersion());
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

			// full coordinates are under <dependencyManagement><dependencies>
			NodeList dependencies = ((Element) doc.getElementsByTagName(
					"dependencyManagement").item(0))
					.getElementsByTagName("dependency");
			for (int i = 0; i < dependencies.getLength(); i++) {
				Element dependency = (Element) dependencies.item(i);
				String groupId = dependency.getElementsByTagName("groupId")
						.item(0).getTextContent().trim();
				String artifactId = dependency
						.getElementsByTagName("artifactId").item(0)
						.getTextContent().trim();
				String version = dependency.getElementsByTagName("version")
						.item(0).getTextContent().trim();
				// if (version.startsWith("${")) {
				// String versionKey = version.substring(0,
				// version.length() - 1).substring(2);
				// if (!props.containsKey(versionKey))
				// throw new SlcException("Cannot interpret version "
				// + version);
				// version = props.getProperty(versionKey);
				// }
				// NodeList scopes = dependency.getElementsByTagName("scope");
				// if (scopes.getLength() > 0
				// && scopes.item(0).getTextContent().equals("import")) {
				// // recurse
				// gatherPomDependencies(aetherTemplate, artifacts,
				// new DefaultArtifact(groupId, artifactId, "pom",
				// version));
				// } else {
				// TODO: deal with scope?
				// TODO: deal with type
				String type = "jar";
				Artifact artifact = new DefaultArtifact(groupId, artifactId,
						type, version);
				artifacts.add(artifact);
				// }
			}
		} catch (Exception e) {
			throw new SlcException("Cannot process pom " + fileNode, e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	/** Separator used to parse the tabular file, default is "," */
	public void setSeparator(String modulesUrlSeparator) {
		this.separator = modulesUrlSeparator;
	}

	/** One of the listed artifact */
	protected static class OsgiArtifact {
		private final String category;
		private final String symbolicName;
		private final String version;
		private final String relativeUrl;

		public OsgiArtifact(String category, String symbolicName,
				String version, String relativeUrl) {
			super();
			this.category = category;
			this.symbolicName = symbolicName;
			this.version = version;
			this.relativeUrl = relativeUrl;
		}

		public String getCategory() {
			return category;
		}

		public String getSymbolicName() {
			return symbolicName;
		}

		public String getVersion() {
			return version;
		}

		public String getRelativeUrl() {
			return relativeUrl;
		}

	}
}
