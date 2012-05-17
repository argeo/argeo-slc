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
package org.argeo.slc.repo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * Indexes jar file, currently supports standard J2SE and OSGi metadata (both
 * from MANIFEST)
 */
public class JarFileIndexer implements NodeIndexer {
	private final static Log log = LogFactory.getLog(JarFileIndexer.class);

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
			if (!fileNode.isNodeType(NodeType.NT_FILE))
				return;

			Session jcrSession = fileNode.getSession();
			Node contentNode = fileNode.getNode(Node.JCR_CONTENT);
			fileBinary = contentNode.getProperty(Property.JCR_DATA).getBinary();
			// jar file
			// if (!FilenameUtils.isExtension(fileNode.getName(), "jar")) {
			// return;
			// }

			jarIn = new JarInputStream(fileBinary.getStream());
			Manifest manifest = jarIn.getManifest();
			if (manifest == null) {
				log.error(fileNode + " has no MANIFEST");
				return;
			}
			bo = new ByteArrayOutputStream();
			manifest.write(bo);
			bi = new ByteArrayInputStream(bo.toByteArray());
			manifestBinary = jcrSession.getValueFactory().createBinary(bi);

			// standard jar file
			fileNode.addMixin(SlcTypes.SLC_JAR_FILE);
			fileNode.setProperty(SlcNames.SLC_MANIFEST, manifestBinary);
			Attributes attrs = manifest.getMainAttributes();
			if (log.isTraceEnabled())
				for (Object key : attrs.keySet())
					log.trace(key + ": " + attrs.getValue(key.toString()));

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
				JcrUtils.updateLastModified(fileNode);
				if (log.isTraceEnabled())
					log.trace("Indexed OSGi bundle " + fileNode);
			} else {
				JcrUtils.updateLastModified(fileNode);
				if (log.isTraceEnabled())
					log.trace("Indexed JAR file " + fileNode);
			}
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

	protected void addOsgiMetadata(Node fileNode, Attributes attrs)
			throws RepositoryException {
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
			fileNode.setProperty(SlcNames.SLC_
					+ Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT, attrs
					.getValue(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT)
					.split(","));

		// bundle classpath
		if (attrs.containsKey(new Name(Constants.BUNDLE_CLASSPATH)))
			fileNode.setProperty(SlcNames.SLC_ + Constants.BUNDLE_CLASSPATH,
					attrs.getValue(Constants.BUNDLE_CLASSPATH).split(","));

		// version
		Version version = new Version(attrs.getValue(Constants.BUNDLE_VERSION));
		fileNode.setProperty(SlcNames.SLC_BUNDLE_VERSION, version.toString());
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
				if (tokens[i].startsWith(Constants.BUNDLE_VERSION_ATTRIBUTE)) {
					node.setProperty(SlcNames.SLC_BUNDLE_VERSION,
							attributeValue(tokens[i]));
				}
			}
		}

		// imported packages
		cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.IMPORT_PACKAGE);
		if (attrs.containsKey(new Name(Constants.IMPORT_PACKAGE))) {
			String importPackages = attrs.getValue(Constants.IMPORT_PACKAGE);
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
		cleanSubNodes(fileNode, SlcNames.SLC_ + Constants.DYNAMICIMPORT_PACKAGE);
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
			String exportPackages = attrs.getValue(Constants.EXPORT_PACKAGE);
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
						Node versionNode = node.addNode(SlcNames.SLC_VERSION,
								SlcTypes.SLC_OSGI_VERSION);
						mapOsgiVersion(new Version(versionStr), versionNode);
					} else if (tokens[i].startsWith(Constants.USES_DIRECTIVE)) {
						String usedPackages = directiveValue(tokens[i]);
						// log.debug("uses='" + usedPackages + "'");
						for (String usedPackage : usedPackages.split(",")) {
							// log.debug("usedPackage='" +
							// usedPackage +
							// "'");
							Node usesNode = node.addNode(SlcNames.SLC_USES,
									SlcTypes.SLC_JAVA_PACKAGE);
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
	private List<String> parsePackages(String str) {
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

}
