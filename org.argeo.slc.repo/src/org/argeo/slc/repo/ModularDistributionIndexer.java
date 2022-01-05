package org.argeo.slc.repo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.CategoryNameVersion;
import org.argeo.slc.DefaultCategoryNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.repo.maven.AetherUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.osgi.framework.Constants;

/**
 * Create or update JCR meta-data for an SLC Modular Distribution
 * 
 * Currently, following types are managed: <list>
 * <li>* .jar: dependency artifacts with csv index</li>
 * <li>@Deprecated : .pom: artifact (binaries) that indexes a group, the .pom
 * file contains a tag "dependencyManagement" that list all modules</li> </list>
 */
public class ModularDistributionIndexer implements NodeIndexer, SlcNames {
	private final static CmsLog log = CmsLog.getLog(ModularDistributionIndexer.class);

	// Constants for csv indexing
	private final static String INDEX_FILE_NAME = "modularDistribution.csv";
	private String separator = ",";

	private Manifest manifest;

	public Boolean support(String path) {
		if (FilenameUtils.getExtension(path).equals("jar"))
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

			if (fileNode.isNodeType(SlcTypes.SLC_MODULAR_DISTRIBUTION) || currDist == null
					|| !currDist.nameVersions().hasNext())
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
			throw new SlcException("Cannot list dependencies from " + fileNode, e);
		} finally {
			JcrUtils.closeQuietly(fileBinary);
		}
	}

	private void indexDistribution(ArgeoOsgiDistribution osgiDist, Node distNode) throws RepositoryException {
		distNode.addMixin(SlcTypes.SLC_MODULAR_DISTRIBUTION);
		distNode.addMixin(SlcTypes.SLC_CATEGORIZED_NAME_VERSION);
		distNode.setProperty(SlcNames.SLC_CATEGORY, osgiDist.getCategory());
		distNode.setProperty(SlcNames.SLC_NAME, osgiDist.getName());
		distNode.setProperty(SlcNames.SLC_VERSION, osgiDist.getVersion());
		if (distNode.hasNode(SLC_MODULES))
			distNode.getNode(SLC_MODULES).remove();
		Node modules = distNode.addNode(SLC_MODULES, NodeType.NT_UNSTRUCTURED);

		for (Iterator<? extends NameVersion> it = osgiDist.nameVersions(); it.hasNext();)
			addModule(modules, it.next());
	}

	// Helpers
	private Node addModule(Node modules, NameVersion nameVersion) throws RepositoryException {
		CategoryNameVersion cnv = (CategoryNameVersion) nameVersion;
		Node moduleCoord = null;
		moduleCoord = modules.addNode(cnv.getName(), SlcTypes.SLC_MODULE_COORDINATES);
		moduleCoord.setProperty(SlcNames.SLC_CATEGORY, cnv.getCategory());
		moduleCoord.setProperty(SlcNames.SLC_NAME, cnv.getName());
		moduleCoord.setProperty(SlcNames.SLC_VERSION, cnv.getVersion());
		return moduleCoord;
	}

	private MyModularDistribution listModulesFromCsvIndex(Node fileNode, Binary fileBinary) {
		JarInputStream jarIn = null;
		BufferedReader reader = null;
		try {
			jarIn = new JarInputStream(fileBinary.getStream());

			List<CategoryNameVersion> modules = new ArrayList<CategoryNameVersion>();

			// meta data
			manifest = jarIn.getManifest();
			if (manifest == null) {
				log.error(fileNode + " has no MANIFEST");
				return null;
			}
			String category = manifest.getMainAttributes().getValue(RepoConstants.SLC_CATEGORY_ID);
			String name = manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
			String version = manifest.getMainAttributes().getValue(Constants.BUNDLE_VERSION);

			Artifact distribution = new DefaultArtifact(category, name, "jar", version);
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
					log.error("Invalid signature file digest " + "for Manifest main attributes: " + entryName
							+ " while looking for an index in bundle " + name);
				}
			}
			if (indexEntry == null)
				return null; // Not a modular definition

			if (category == null) {
				log.warn("Modular definition found but no " + RepoConstants.SLC_CATEGORY_ID + " in " + fileNode);
			}

			// Process the index
			reader = new BufferedReader(new InputStreamReader(jarIn));
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, separator);
				st.nextToken(); // moduleName
				st.nextToken(); // moduleVersion
				String relativeUrl = st.nextToken();
				Artifact currModule = AetherUtils.convertPathToArtifact(relativeUrl, null);
				modules.add(new DefaultCategoryNameVersion(currModule.getGroupId(), currModule.getArtifactId(),
						currModule.getVersion()));
			}
			return new MyModularDistribution(distribution, modules);
		} catch (Exception e) {
			throw new SlcException("Cannot list artifacts", e);
		} finally {
			IOUtils.closeQuietly(jarIn);
			IOUtils.closeQuietly(reader);
		}
	}

	/**
	 * A consistent and versioned OSGi distribution, which can be built and tested.
	 */
	private class MyModularDistribution extends ArtifactDistribution implements ArgeoOsgiDistribution {

		private List<CategoryNameVersion> modules;

		public MyModularDistribution(Artifact artifact, List<CategoryNameVersion> modules) {
			super(artifact);
			this.modules = modules;
		}

		public Iterator<CategoryNameVersion> nameVersions() {
			return modules.iterator();
		}

		// Modular distribution interface methods. Not yet used.
		public Distribution getModuleDistribution(String moduleName, String moduleVersion) {
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