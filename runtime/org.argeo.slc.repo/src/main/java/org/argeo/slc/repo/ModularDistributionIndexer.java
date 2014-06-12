package org.argeo.slc.repo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.CategorizedNameVersion;
import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.aether.AetherUtils;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.osgi.framework.Constants;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Create or update JCR meta-data for an SLC Modular Distribution
 * 
 * Currently, following types are managed: <list><li>* .jar: dependency
 * artifacts with csv index</li> <li>.pom: artifact (binaries) that indexes a
 * group, the .pom file contains a tag "dependencyManagement" that list all
 * modules</li> </list>
 */
public class ModularDistributionIndexer implements NodeIndexer, SlcNames {
	private final static Log log = LogFactory
			.getLog(ModularDistributionIndexer.class);

	// Constants for csv indexing
	private final static String INDEX_FILE_NAME = "modularDistribution.csv";
	private String separator = ",";

	// Artifact indexing
	private final static List<String> BINARIES_ARTIFACTS_NAME;
	static {
		List<String> tmpList = new ArrayList<String>();
		tmpList.add(RepoConstants.BINARIES_ARTIFACT_ID);
		// tmpList.add(RepoConstants.SOURCES_ARTIFACT_ID);
		// tmpList.add(RepoConstants.SDK_ARTIFACT_ID);
		BINARIES_ARTIFACTS_NAME = Collections.unmodifiableList(tmpList);
	}

	private Manifest manifest;

	// private Comparator<Artifact> artifactComparator = new
	// ArtifactIdComparator();

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
		Binary fileBinary = null;
		try {

			String fileNodePath = fileNode.getPath();
			if (!support(fileNodePath))
				return;

			if (!fileNode.isNodeType(NodeType.NT_FILE))
				return;

			Node contentNode = fileNode.getNode(Node.JCR_CONTENT);
			fileBinary = contentNode.getProperty(Property.JCR_DATA).getBinary();

			MyModularDistribution currDist = null;
			if (FilenameUtils.getExtension(fileNode.getPath()).equals("jar"))
				currDist = listModulesFromCsvIndex(fileNode, fileBinary);
			else if (FilenameUtils.getExtension(fileNode.getPath()).equals(
					"pom"))
				currDist = listModulesFromPomIndex(fileNode, fileBinary);

			if (fileNode.isNodeType(SlcTypes.SLC_MODULAR_DISTRIBUTION)
					|| currDist == null || !currDist.nameVersions().hasNext())
				return; // already indexed or no modules found
			else {
				fileNode.addMixin(SlcTypes.SLC_MODULAR_DISTRIBUTION);
				fileNode.addMixin(SlcTypes.SLC_CATEGORIZED_NAME_VERSION);
				if (currDist.getCategory() != null)
					fileNode.setProperty(SLC_CATEGORY, currDist.getCategory());
				fileNode.setProperty(SLC_NAME, currDist.getName());
				fileNode.setProperty(SLC_VERSION, currDist.getVersion());
				indexDistribution(currDist, fileNode);
			}

			if (log.isTraceEnabled())
				log.trace("Indexed " + fileNode + " as modular distribution");
		} catch (Exception e) {
			throw new SlcException("Cannot list dependencies from " + fileNode,
					e);
		}
	}

	private void indexDistribution(ArgeoOsgiDistribution osgiDist, Node distNode)
			throws RepositoryException {
		distNode.addMixin(SlcTypes.SLC_MODULAR_DISTRIBUTION);
		distNode.addMixin(SlcTypes.SLC_CATEGORIZED_NAME_VERSION);
		distNode.setProperty(SlcNames.SLC_CATEGORY, osgiDist.getCategory());
		distNode.setProperty(SlcNames.SLC_NAME, osgiDist.getName());
		distNode.setProperty(SlcNames.SLC_VERSION, osgiDist.getVersion());
		Node modules = JcrUtils.mkdirs(distNode, SlcNames.SLC_MODULES,
				NodeType.NT_UNSTRUCTURED);

		for (Iterator<? extends NameVersion> it = osgiDist.nameVersions(); it
				.hasNext();)
			addModule(modules, it.next());
	}

	// Helpers
	private Node addModule(Node modules, NameVersion nameVersion)
			throws RepositoryException {
		CategorizedNameVersion cnv = (CategorizedNameVersion) nameVersion;
		Node moduleCoord = null;
		moduleCoord = modules.addNode(cnv.getName(),
				SlcTypes.SLC_MODULE_COORDINATES);
		moduleCoord.setProperty(SlcNames.SLC_CATEGORY, cnv.getCategory());
		moduleCoord.setProperty(SlcNames.SLC_NAME, cnv.getName());
		moduleCoord.setProperty(SlcNames.SLC_VERSION, cnv.getVersion());
		return moduleCoord;
	}

	private MyModularDistribution listModulesFromCsvIndex(Node fileNode,
			Binary fileBinary) {
		JarInputStream jarIn = null;
		BufferedReader reader = null;
		try {
			jarIn = new JarInputStream(fileBinary.getStream());

			List<CategorizedNameVersion> modules = new ArrayList<CategorizedNameVersion>();

			// meta data
			manifest = jarIn.getManifest();
			if (manifest == null) {
				log.error(fileNode + " has no MANIFEST");
				return null;
			}
			String category = manifest.getMainAttributes().getValue(
					RepoConstants.SLC_GROUP_ID);
			String name = manifest.getMainAttributes().getValue(
					Constants.BUNDLE_SYMBOLICNAME);
			String version = manifest.getMainAttributes().getValue(
					Constants.BUNDLE_VERSION);

			Artifact distribution = new DefaultArtifact(category, name, "pom",
					version);
			// Retrieve the index file
			JarEntry indexEntry;
			while ((indexEntry = jarIn.getNextJarEntry()) != null) {
				String entryName = indexEntry.getName();
				if (entryName.equals(INDEX_FILE_NAME)) {
					break;
				}
				try {
					jarIn.closeEntry();
				} catch (SecurityException se) {
					log.error("Invalid signature file digest "
							+ "for Manifest main attributes: " + entryName
							+ " while looking for an index in bundle " + name);
				}
			}
			if (indexEntry == null)
				return null; // Not a modular definition artifact

			// Process the index
			reader = new BufferedReader(new InputStreamReader(jarIn));
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, separator);
				st.nextToken(); // moduleName
				st.nextToken(); // moduleVersion
				String relativeUrl = st.nextToken();
				Artifact currModule = AetherUtils.convertPathToArtifact(
						relativeUrl, null);
				modules.add(new MyCategorizedNameVersion(currModule
						.getGroupId(), currModule.getArtifactId(), currModule
						.getVersion()));
			}
			return new MyModularDistribution(distribution, modules);
		} catch (Exception e) {
			throw new SlcException("Cannot list artifacts", e);
		} finally {
			IOUtils.closeQuietly(jarIn);
			IOUtils.closeQuietly(reader);
		}
	}

	private MyModularDistribution listModulesFromPomIndex(Node fileNode,
			Binary fileBinary) {
		InputStream input = null;
		List<CategorizedNameVersion> modules = new ArrayList<CategorizedNameVersion>();
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
				modules.add(new MyCategorizedNameVersion(groupId, artifactId,
						version));
			}

			String groupId = doc.getElementsByTagName("groupId").item(0)
					.getTextContent().trim();
			String artifactId = doc.getElementsByTagName("artifactId").item(0)
					.getTextContent().trim();
			String version = doc.getElementsByTagName("version").item(0)
					.getTextContent().trim();

			Artifact currDist = new DefaultArtifact(groupId, artifactId, "pom",
					version);

			return new MyModularDistribution(currDist, modules);
		} catch (Exception e) {
			throw new SlcException("Cannot process pom " + fileNode, e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	/** The created modular distribution */
	private static class MyCategorizedNameVersion extends DefaultNameVersion
			implements CategorizedNameVersion {
		private final String category;

		public MyCategorizedNameVersion(String category, String name,
				String version) {
			super(name, version);
			this.category = category;
		}

		public String getCategory() {
			return category;
		}
	}

	/**
	 * A consistent and versioned OSGi distribution, which can be built and
	 * tested.
	 */
	private class MyModularDistribution extends ArtifactDistribution implements
			ArgeoOsgiDistribution {

		private List<CategorizedNameVersion> modules;

		public MyModularDistribution(Artifact artifact,
				List<CategorizedNameVersion> modules) {
			super(artifact);
			this.modules = modules;
		}

		public Iterator<CategorizedNameVersion> nameVersions() {
			return modules.iterator();
		}

		// Modular distribution interface methods. Not yet used.
		public Distribution getModuleDistribution(String moduleName,
				String moduleVersion) {
			return null;
		}

		public Object getModulesDescriptor(String descriptorType) {
			return null;
		}
	}

	/** Separator used to parse the tabular file, default is "," */
	public void setSeparator(String modulesUrlSeparator) {
		this.separator = modulesUrlSeparator;
	}

}