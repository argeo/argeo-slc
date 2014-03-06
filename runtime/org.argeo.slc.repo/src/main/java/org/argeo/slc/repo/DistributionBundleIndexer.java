package org.argeo.slc.repo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.aether.AetherUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.osgi.framework.Constants;
import org.sonatype.aether.artifact.Artifact;

/**
 * Index distribution bundles that is mainly dep artifacts that have generate a
 * modular distribution csv index during maven build
 */
public class DistributionBundleIndexer implements NodeIndexer {
	private final static Log log = LogFactory
			.getLog(DistributionBundleIndexer.class);

	private final static String INDEX_FILE_NAME = "modularDistribution.csv";

	// private final String url;

	private Manifest manifest;
	private String symbolicName;
	private String version;

	/** can be null */
	// private String baseUrl;
	/** can be null */
	// private String relativeUrl;

	private List<Artifact> artifacts;

	private String separator = ",";

	// public DistributionBundleIndexer(String url) {
	// this.url = url;
	// }
	//
	// public DistributionBundleIndexer(String baseUrl, String relativeUrl) {
	// if (baseUrl == null || !baseUrl.endsWith("/"))
	// throw new SlcException("Base url " + baseUrl + " badly formatted");
	// if (relativeUrl.startsWith("http") || relativeUrl.startsWith("file:"))
	// throw new SlcException("Relative URL " + relativeUrl
	// + " badly formatted");
	// this.url = baseUrl + relativeUrl;
	// this.baseUrl = baseUrl;
	// this.relativeUrl = relativeUrl;
	// }

	public Boolean support(String path) {
		return FilenameUtils.getExtension(path).equals("jar");
	}

	public void index(Node fileNode) {
		JarInputStream jarIn = null;
		Binary fileBinary = null;
		try {
			if (!support(fileNode.getPath()))
				return;

			if (!fileNode.isNodeType(NodeType.NT_FILE))
				return;

			Session jcrSession = fileNode.getSession();
			Node contentNode = fileNode.getNode(Node.JCR_CONTENT);
			fileBinary = contentNode.getProperty(Property.JCR_DATA).getBinary();
			jarIn = new JarInputStream(fileBinary.getStream());

			// meta data
			manifest = jarIn.getManifest();
			if (manifest == null) {
				log.error(fileNode + " has no MANIFEST");
				return;
			}
			symbolicName = manifest.getMainAttributes().getValue(
					Constants.BUNDLE_SYMBOLICNAME);
			version = manifest.getMainAttributes().getValue(
					Constants.BUNDLE_VERSION);

			JarEntry indexEntry;
			while ((indexEntry = jarIn.getNextJarEntry()) != null) {
				String entryName = indexEntry.getName();
				if (entryName.equals(INDEX_FILE_NAME)) {
					break;
				}
				jarIn.closeEntry();
			}

			// list artifacts
			if (indexEntry == null)
				return; // Not a modular definition artifact

			// throw new SlcException("No index " + INDEX_FILE_NAME + " in "
			// + fileNode.getPath());
			//

			artifacts = listArtifacts(jarIn);

			if (artifacts == null || artifacts.isEmpty())
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
						moduleCoord.setProperty(SlcNames.SLC_MODULE_NAME,
								artifact.getArtifactId());
						moduleCoord.setProperty(SlcNames.SLC_MODULE_VERSION,
								artifact.getVersion());
						String groupId = artifact.getGroupId();
						if (groupId != null && !"".equals(groupId.trim()))
							moduleCoord.setProperty(
									SlcNames.SLC_MODULE_CATEGORY,
									artifact.getGroupId());
					}
				}

			}

			jarIn.closeEntry();

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
		} finally {
			if (jarIn != null)
				try {
					jarIn.close();
				} catch (IOException e) {
					// silent
				}
		}
	}

	protected List<Artifact> listArtifacts(InputStream in) {
		List<Artifact> artifacts = new ArrayList<Artifact>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {

				StringTokenizer st = new StringTokenizer(line, separator);
				String moduleName = st.nextToken();
				String moduleVersion = st.nextToken();
				String relativeUrl = st.nextToken();

				//
				// String Category = getCategoryFromRelativeUrl(relativeUrl,
				// moduleName);

				artifacts.add(AetherUtils.convertPathToArtifact(relativeUrl,
						null));

				if (log.isTraceEnabled())
					log.debug("Processed dependency: " + line);
			}
		} catch (Exception e) {
			throw new SlcException("Cannot list artifacts", e);
		}
		return artifacts;
	}

	/** Relative path to the directories where the files will be stored */
	private String getCategoryFromRelativeUrl(String relativeUrl,
			String moduleName) {
		int index = relativeUrl.indexOf("moduleName");
		if (index < 1)
			throw new SlcException("Unvalid relative URL: " + relativeUrl
					+ " for module " + moduleName);
		// Remove trailing /
		String result = relativeUrl.substring(0, index - 1);
		return result.replace('/', '.');
	}

	/**
	 * List full URLs of the bundles, based on base URL, usable directly for
	 * download.
	 */
	// public List/* <String> */listUrls() {
	// if (baseUrl == null)
	// throw new SlcException("Base URL is not set");
	//
	// if (artifacts == null)
	// throw new SlcException("Artifact list not initialized");
	//
	// List/* <String> */urls = new ArrayList();
	// for (int i = 0; i < artifacts.size(); i++) {
	// OsgiArtifact osgiArtifact = (OsgiArtifact) artifacts.get(i);
	// urls.add(baseUrl + osgiArtifact.getRelativeUrl());
	// }
	// return urls;
	// }
	//
	// public void setBaseUrl(String baseUrl) {
	// this.baseUrl = baseUrl;
	// }

	/** Separator used to parse the tabular file, default is "," */
	public void setSeparator(String modulesUrlSeparator) {
		this.separator = modulesUrlSeparator;
	}

	// public String getRelativeUrl() {
	// return relativeUrl;
	// }

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
