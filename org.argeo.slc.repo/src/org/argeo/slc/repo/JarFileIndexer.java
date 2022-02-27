package org.argeo.slc.repo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * Indexes jar file, currently supports standard J2SE and OSGi metadata (both
 * from MANIFEST)
 */
public class JarFileIndexer implements NodeIndexer, SlcNames {
	private final static CmsLog log = CmsLog.getLog(JarFileIndexer.class);
	private Boolean force = false;

	public Boolean support(String path) {
		return FilenameUtils.getExtension(path).equals("jar");
	}

	public void index(Node fileNode) {
		Binary fileBinary = null;
		JarInputStream jarIn = null;
		ByteArrayOutputStream bo = null;
		ByteArrayInputStream bi = null;
		Binary manifestBinary = null;
		try {
			if (!support(fileNode.getPath()))
				return;

			// Already indexed
			if (!force && fileNode.isNodeType(SlcTypes.SLC_JAR_FILE))
				return;

			if (!fileNode.isNodeType(NodeType.NT_FILE))
				return;

			Session jcrSession = fileNode.getSession();
			Node contentNode = fileNode.getNode(Node.JCR_CONTENT);
			fileBinary = contentNode.getProperty(Property.JCR_DATA).getBinary();

			jarIn = new JarInputStream(fileBinary.getStream());
			Manifest manifest = jarIn.getManifest();
			if (manifest == null) {
				log.error(fileNode + " has no MANIFEST");
				return;
			}

			bo = new ByteArrayOutputStream();
			manifest.write(bo);
			byte[] newManifest = bo.toByteArray();
			if (fileNode.hasProperty(SLC_MANIFEST)) {
				byte[] storedManifest = JcrUtils.getBinaryAsBytes(fileNode.getProperty(SLC_MANIFEST));
				if (Arrays.equals(newManifest, storedManifest)) {
					if (log.isTraceEnabled())
						log.trace("Manifest not changed, doing nothing " + fileNode);
					return;
				}
			}

			bi = new ByteArrayInputStream(newManifest);
			manifestBinary = jcrSession.getValueFactory().createBinary(bi);

			// standard jar file
			fileNode.addMixin(SlcTypes.SLC_JAR_FILE);

			fileNode.setProperty(SlcNames.SLC_MANIFEST, manifestBinary);
			Attributes attrs = manifest.getMainAttributes();

			getI18nValues(fileBinary, attrs);

			// standard J2SE MANIFEST attributes
			addAttr(Attributes.Name.MANIFEST_VERSION, fileNode, attrs);
			addAttr(Attributes.Name.SIGNATURE_VERSION, fileNode, attrs);
			addAttr(Attributes.Name.CLASS_PATH, fileNode, attrs);
			addAttr(Attributes.Name.MAIN_CLASS, fileNode, attrs);
			addAttr(Attributes.Name.EXTENSION_NAME, fileNode, attrs);
			addAttr(Attributes.Name.IMPLEMENTATION_VERSION, fileNode, attrs);
			addAttr(Attributes.Name.IMPLEMENTATION_VENDOR, fileNode, attrs);
			addAttr(Attributes.Name.IMPLEMENTATION_VENDOR_ID, fileNode, attrs);
			addAttr(Attributes.Name.SPECIFICATION_TITLE, fileNode, attrs);
			addAttr(Attributes.Name.SPECIFICATION_VERSION, fileNode, attrs);
			addAttr(Attributes.Name.SPECIFICATION_VENDOR, fileNode, attrs);
			addAttr(Attributes.Name.SEALED, fileNode, attrs);

			// OSGi
			if (attrs.containsKey(new Name(Constants.BUNDLE_SYMBOLICNAME))) {
				addOsgiMetadata(fileNode, attrs);
				if (log.isTraceEnabled())
					log.trace("Indexed OSGi bundle " + fileNode);
			} else {
				if (log.isTraceEnabled())
					log.trace("Indexed JAR file " + fileNode);
			}

			JcrUtils.updateLastModified(fileNode);

		} catch (Exception e) {
			throw new SlcException("Cannot index jar " + fileNode, e);
		} finally {
			IOUtils.closeQuietly(bi);
			IOUtils.closeQuietly(bo);
			IOUtils.closeQuietly(jarIn);
			JcrUtils.closeQuietly(manifestBinary);
			JcrUtils.closeQuietly(fileBinary);
		}

	}

	private void getI18nValues(Binary fileBinary, Attributes attrs) throws IOException {
		JarInputStream jarIn = null;
		try {
			jarIn = new JarInputStream(fileBinary.getStream());
			String bundleLocalization = null;

			String blKey = Constants.BUNDLE_LOCALIZATION; // "Bundle-Localization";
			Name blkName = new Name(blKey);

			browse: for (Object obj : attrs.keySet()) {
				String value = attrs.getValue((Attributes.Name) obj);
				if (value.startsWith("%")) {
					if (attrs.containsKey(blkName)) {
						bundleLocalization = attrs.getValue(blkName);
						break browse;
					}
				}
			}

			JarEntry jarEntry = null;
			byte[] propBytes = null;
			ByteArrayOutputStream baos = null;
			browse: if (bundleLocalization != null) {
				JarEntry entry = jarIn.getNextJarEntry();
				while (entry != null) {
					if (entry.getName().equals(bundleLocalization + ".properties")) {
						jarEntry = entry;

						// if(je.getSize() != -1){
						// propBytes = new byte[(int)je.getSize()];
						// int len = (int) je.getSize();
						// int offset = 0;
						// while (offset != len)
						// offset += jarIn.read(propBytes, offset, len -
						// offset);
						// } else {
						baos = new ByteArrayOutputStream();
						while (true) {
							int qwe = jarIn.read();
							if (qwe == -1)
								break;
							baos.write(qwe);
						}
						propBytes = baos.toByteArray();
						break browse;
					}
					entry = jarIn.getNextJarEntry();
				}
			}

			if (jarEntry != null) {
				Properties prop = new Properties();
				InputStream is = new ByteArrayInputStream(propBytes);
				prop.load(is);

				for (Object obj : attrs.keySet()) {
					String value = attrs.getValue((Attributes.Name) obj);
					if (value.startsWith("%")) {
						String newVal = prop.getProperty(value.substring(1));
						if (newVal != null)
							attrs.put(obj, newVal);
					}
				}
			}
		} catch (RepositoryException e) {
			throw new SlcException("Error while reading the jar binary content " + fileBinary, e);
		} catch (IOException ioe) {
			throw new SlcException("unable to get internationalized values", ioe);
		} finally {
			IOUtils.closeQuietly(jarIn);
		}
	}

	protected void addOsgiMetadata(Node fileNode, Attributes attrs) throws RepositoryException {

		// TODO remove this ?
		// Compulsory for the time being, because bundle artifact extends
		// artifact
		if (!fileNode.isNodeType(SlcTypes.SLC_ARTIFACT)) {
			ArtifactIndexer indexer = new ArtifactIndexer();
			indexer.index(fileNode);
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
		if (attrs.containsKey(new Name(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT)))
			fileNode.setProperty(SlcNames.SLC_ + Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT,
					attrs.getValue(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT).split(","));

		// bundle classpath
		if (attrs.containsKey(new Name(Constants.BUNDLE_CLASSPATH)))
			fileNode.setProperty(SlcNames.SLC_ + Constants.BUNDLE_CLASSPATH,
					attrs.getValue(Constants.BUNDLE_CLASSPATH).split(","));

		// version
		Version version = new Version(attrs.getValue(Constants.BUNDLE_VERSION));
		fileNode.setProperty(SlcNames.SLC_BUNDLE_VERSION, version.toString());
		cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.BUNDLE_VERSION);
		Node bundleVersionNode = fileNode.addNode(SlcNames.SLC_ + Constants.BUNDLE_VERSION, SlcTypes.SLC_OSGI_VERSION);
		mapOsgiVersion(version, bundleVersionNode);

		// fragment
		cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.FRAGMENT_HOST);
		if (attrs.containsKey(new Name(Constants.FRAGMENT_HOST))) {
			String fragmentHost = attrs.getValue(Constants.FRAGMENT_HOST);
			String[] tokens = fragmentHost.split(";");
			Node node = fileNode.addNode(SlcNames.SLC_ + Constants.FRAGMENT_HOST, SlcTypes.SLC_FRAGMENT_HOST);
			node.setProperty(SlcNames.SLC_SYMBOLIC_NAME, tokens[0]);
			for (int i = 1; i < tokens.length; i++) {
				if (tokens[i].startsWith(Constants.BUNDLE_VERSION_ATTRIBUTE)) {
					node.setProperty(SlcNames.SLC_BUNDLE_VERSION, attributeValue(tokens[i]));
				}
			}
		}

		// imported packages
		cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.IMPORT_PACKAGE);
		if (attrs.containsKey(new Name(Constants.IMPORT_PACKAGE))) {
			String importPackages = attrs.getValue(Constants.IMPORT_PACKAGE);
			List<String> packages = parseCommaSeparated(importPackages);
			for (String pkg : packages) {
				String[] tokens = pkg.split(";");
				Node node = fileNode.addNode(SlcNames.SLC_ + Constants.IMPORT_PACKAGE, SlcTypes.SLC_IMPORTED_PACKAGE);
				node.setProperty(SlcNames.SLC_NAME, tokens[0]);
				for (int i = 1; i < tokens.length; i++) {
					if (tokens[i].startsWith(Constants.VERSION_ATTRIBUTE)) {
						node.setProperty(SlcNames.SLC_VERSION, attributeValue(tokens[i]));
					} else if (tokens[i].startsWith(Constants.RESOLUTION_DIRECTIVE)) {
						node.setProperty(SlcNames.SLC_OPTIONAL,
								directiveValue(tokens[i]).equals(Constants.RESOLUTION_OPTIONAL));
					}
				}
			}
		}

		// dynamic import package
		cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.DYNAMICIMPORT_PACKAGE);
		if (attrs.containsKey(new Name(Constants.DYNAMICIMPORT_PACKAGE))) {
			String importPackages = attrs.getValue(Constants.DYNAMICIMPORT_PACKAGE);
			List<String> packages = parseCommaSeparated(importPackages);
			for (String pkg : packages) {
				String[] tokens = pkg.split(";");
				Node node = fileNode.addNode(SlcNames.SLC_ + Constants.DYNAMICIMPORT_PACKAGE,
						SlcTypes.SLC_DYNAMIC_IMPORTED_PACKAGE);
				node.setProperty(SlcNames.SLC_NAME, tokens[0]);
				for (int i = 1; i < tokens.length; i++) {
					if (tokens[i].startsWith(Constants.VERSION_ATTRIBUTE)) {
						node.setProperty(SlcNames.SLC_VERSION, attributeValue(tokens[i]));
					}
				}
			}
		}

		// exported packages
		cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.EXPORT_PACKAGE);
		if (attrs.containsKey(new Name(Constants.EXPORT_PACKAGE))) {
			String exportPackages = attrs.getValue(Constants.EXPORT_PACKAGE);
			List<String> packages = parseCommaSeparated(exportPackages);
			for (String pkg : packages) {
				String[] tokens = pkg.split(";");
				Node node = fileNode.addNode(SlcNames.SLC_ + Constants.EXPORT_PACKAGE, SlcTypes.SLC_EXPORTED_PACKAGE);
				node.setProperty(SlcNames.SLC_NAME, tokens[0]);
				// TODO: are these cleans really necessary?
				cleanSubNodes(node, SlcNames.SLC_USES);
				cleanSubNodes(node, SlcNames.SLC_VERSION);
				for (int i = 1; i < tokens.length; i++) {
					if (tokens[i].startsWith(Constants.VERSION_ATTRIBUTE)) {
						String versionStr = attributeValue(tokens[i]);
						Node versionNode = node.addNode(SlcNames.SLC_VERSION, SlcTypes.SLC_OSGI_VERSION);
						mapOsgiVersion(new Version(versionStr), versionNode);
					} else if (tokens[i].startsWith(Constants.USES_DIRECTIVE)) {
						String usedPackages = directiveValue(tokens[i]);
						// log.debug("uses='" + usedPackages + "'");
						for (String usedPackage : usedPackages.split(",")) {
							// log.debug("usedPackage='" +
							// usedPackage +
							// "'");
							Node usesNode = node.addNode(SlcNames.SLC_USES, SlcTypes.SLC_JAVA_PACKAGE);
							usesNode.setProperty(SlcNames.SLC_NAME, usedPackage);
						}
					}
				}
			}
		}

		// required bundle
		cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.REQUIRE_BUNDLE);
		if (attrs.containsKey(new Name(Constants.REQUIRE_BUNDLE))) {
			String requireBundle = attrs.getValue(Constants.REQUIRE_BUNDLE);
			List<String> bundles = parseCommaSeparated(requireBundle);
			for (String bundle : bundles) {
				String[] tokens = bundle.split(";");
				Node node = fileNode.addNode(SlcNames.SLC_ + Constants.REQUIRE_BUNDLE, SlcTypes.SLC_REQUIRED_BUNDLE);
				node.setProperty(SlcNames.SLC_SYMBOLIC_NAME, tokens[0]);
				for (int i = 1; i < tokens.length; i++) {
					if (tokens[i].startsWith(Constants.BUNDLE_VERSION_ATTRIBUTE)) {
						node.setProperty(SlcNames.SLC_BUNDLE_VERSION, attributeValue(tokens[i]));
					} else if (tokens[i].startsWith(Constants.RESOLUTION_DIRECTIVE)) {
						node.setProperty(SlcNames.SLC_OPTIONAL,
								directiveValue(tokens[i]).equals(Constants.RESOLUTION_OPTIONAL));
					}
				}
			}
		}

	}

	private void addAttr(String key, Node node, Attributes attrs) throws RepositoryException {
		addAttr(new Name(key), node, attrs);
	}

	private void addAttr(Name key, Node node, Attributes attrs) throws RepositoryException {
		if (attrs.containsKey(key)) {
			String value = attrs.getValue(key);
			node.setProperty(SlcNames.SLC_ + key, value);
		}
	}

	private void cleanSubNodes(Node node, String name) throws RepositoryException {
		if (node.hasNode(name)) {
			NodeIterator nit = node.getNodes(name);
			while (nit.hasNext())
				nit.nextNode().remove();
		}
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

	/** Parse package list with nested directive with ',' */
	private List<String> parseCommaSeparated(String str) {
		List<String> res = new ArrayList<String>();
		StringBuffer curr = new StringBuffer("");
		boolean in = false;
		for (char c : str.toCharArray()) {
			if (c == ',') {
				if (!in) {// new package
					res.add(curr.toString());
					curr = new StringBuffer("");
				} else {// a ',' within " "
					curr.append(c);
				}
			} else if (c == '\"') {
				in = !in;
				curr.append(c);
			} else {
				curr.append(c);
			}
		}
		res.add(curr.toString());
		// log.debug(res);
		return res;
	}

	protected void mapOsgiVersion(Version version, Node versionNode) throws RepositoryException {
		versionNode.setProperty(SlcNames.SLC_AS_STRING, version.toString());
		versionNode.setProperty(SlcNames.SLC_MAJOR, version.getMajor());
		versionNode.setProperty(SlcNames.SLC_MINOR, version.getMinor());
		versionNode.setProperty(SlcNames.SLC_MICRO, version.getMicro());
		if (!version.getQualifier().equals(""))
			versionNode.setProperty(SlcNames.SLC_QUALIFIER, version.getQualifier());
	}

	public void setForce(Boolean force) {
		this.force = force;
	}

}
