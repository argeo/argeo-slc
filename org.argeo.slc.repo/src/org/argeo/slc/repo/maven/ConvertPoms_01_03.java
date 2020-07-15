package org.argeo.slc.repo.maven;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Recursively migrate all the POMs to Argeo Distribution v1.3 */
public class ConvertPoms_01_03 implements Runnable {
	final String SPRING_SOURCE_PREFIX = "com.springsource";

	private HashMap<String, String> artifactMapping = new HashMap<String, String>();

	private File rootDir;

	public ConvertPoms_01_03(String rootDirPath) {
		this(new File(rootDirPath));
	}

	public ConvertPoms_01_03(File rootDir) {
		this.rootDir = rootDir;

		artifactMapping.put("org.argeo.dep.jacob", "com.jacob");
		artifactMapping.put("org.argeo.dep.jacob.win32.x86",
				"com.jacob.win32.x86");
		artifactMapping.put("org.argeo.dep.osgi.activemq",
				"org.apache.activemq");
		artifactMapping.put("org.argeo.dep.osgi.activemq.optional",
				"org.apache.activemq.optional");
		artifactMapping.put("org.argeo.dep.osgi.activemq.xmpp",
				"org.apache.activemq.xmpp");
		artifactMapping.put("org.argeo.dep.osgi.aether", "org.eclipse.aether");
		artifactMapping.put("org.argeo.dep.osgi.boilerpipe",
				"de.l3s.boilerpipe");
		artifactMapping.put("org.argeo.dep.osgi.commons.cli",
				"org.apache.commons.cli");
		artifactMapping.put("org.argeo.dep.osgi.commons.exec",
				"org.apache.commons.exec");
		artifactMapping.put("org.argeo.dep.osgi.directory.shared.asn.codec",
				"org.apache.directory.shared.asn.codec");
		artifactMapping.put("org.argeo.dep.osgi.drewnoakes.metadata_extractor",
				"com.drewnoakes.metadata_extractor");
		artifactMapping.put("org.argeo.dep.osgi.geoapi", "org.opengis");
		artifactMapping.put("org.argeo.dep.osgi.geotools", "org.geotools");
		artifactMapping.put("org.argeo.dep.osgi.google.collections",
				"com.google.collections");
		artifactMapping.put("org.argeo.dep.osgi.hibernatespatial",
				"org.hibernatespatial");
		artifactMapping.put("org.argeo.dep.osgi.jackrabbit",
				"org.apache.jackrabbit");
		artifactMapping.put("org.argeo.dep.osgi.jai.imageio",
				"com.sun.media.jai.imageio");
		artifactMapping.put("org.argeo.dep.osgi.java3d", "javax.vecmath");
		artifactMapping.put("org.argeo.dep.osgi.jcr", "javax.jcr");
		artifactMapping.put("org.argeo.dep.osgi.jsr275", "javax.measure");
		artifactMapping.put("org.argeo.dep.osgi.jts", "com.vividsolutions.jts");
		artifactMapping.put("org.argeo.dep.osgi.mina.filter.ssl",
				"org.apache.mina.filter.ssl");
		artifactMapping.put("org.argeo.dep.osgi.modeshape", "org.modeshape");
		artifactMapping.put("org.argeo.dep.osgi.netcdf",
				"edu.ucar.unidata.netcdf");
		artifactMapping.put("org.argeo.dep.osgi.pdfbox", "org.apache.pdfbox");
		artifactMapping.put("org.argeo.dep.osgi.poi", "org.apache.poi");
		artifactMapping.put("org.argeo.dep.osgi.postgis.jdbc",
				"org.postgis.jdbc");
		artifactMapping.put("org.argeo.dep.osgi.springframework.ldap",
				"org.springframework.ldap");
		artifactMapping.put("org.argeo.dep.osgi.tagsoup",
				"org.ccil.cowan.tagsoup");
		artifactMapping.put("org.argeo.dep.osgi.tika", "org.apache.tika");
	}

	public void run() {
		traverse(rootDir);
	}

	protected void traverse(File dir) {
		for (File file : dir.listFiles()) {
			String fileName = file.getName();
			if (file.isDirectory() && !skipDirName(fileName)) {
				traverse(file);
			} else if (fileName.equals("pom.xml")) {
				processPom(file);
			}
		}
	}

	protected Boolean skipDirName(String fileName) {
		return fileName.equals(".svn") || fileName.equals("target")
				|| fileName.equals("META-INF") || fileName.equals("src");
	}

	protected void processPom(File pomFile) {
		try {
			Boolean wasChanged = false;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(pomFile);
			doc.getDocumentElement().normalize();

			Element dependenciesElement = null;
			NodeList rootChildren = doc.getDocumentElement().getChildNodes();
			for (int temp = 0; temp < rootChildren.getLength(); temp++) {
				Node n = rootChildren.item(temp);
				if (n.getNodeName().equals("dependencies"))
					dependenciesElement = (Element) n;
			}

			if (dependenciesElement != null) {
				stdOut("\n## " + pomFile);
				NodeList dependencyElements = dependenciesElement
						.getElementsByTagName("dependency");

				for (int temp = 0; temp < dependencyElements.getLength(); temp++) {
					Element eElement = (Element) dependencyElements.item(temp);
					String groupId = getTagValue(eElement, "groupId");
					String artifactId = getTagValue(eElement, "artifactId");
					// stdOut(groupId + ":" + artifactId);

					String newGroupId = null;
					String newArtifactId = null;
					if (groupId.startsWith("org.argeo.dep")) {
						newGroupId = "org.argeo.tp";
					} else if (!(groupId.startsWith("org.argeo")
							|| groupId.startsWith("com.capco")
							|| groupId.startsWith("com.agfa") || groupId
							.startsWith("org.ibboost"))) {
						newGroupId = "org.argeo.tp";
					}

					if (artifactMapping.containsKey(artifactId)) {
						newArtifactId = artifactMapping.get(artifactId);
					} else if (artifactId.startsWith(SPRING_SOURCE_PREFIX)
							&& !artifactId.equals(SPRING_SOURCE_PREFIX
									+ ".json")) {
						newArtifactId = artifactId
								.substring(SPRING_SOURCE_PREFIX.length() + 1);
					}

					// modify
					if (newGroupId != null || newArtifactId != null) {
						if (newGroupId == null)
							newGroupId = groupId;
						if (newArtifactId == null)
							newArtifactId = artifactId;
						stdOut(groupId + ":" + artifactId + " => " + newGroupId
								+ ":" + newArtifactId);
						setTagValue(eElement, "groupId", newGroupId);
						setTagValue(eElement, "artifactId", newArtifactId);
						wasChanged = true;
					}
				}
			}

			if (wasChanged) {
				// pomFile.renameTo(new File(pomFile.getParentFile(),
				// "pom-old.xml"));
				// save in place
				Source source = new DOMSource(doc);
				Result result = new StreamResult(pomFile);
				Transformer xformer = TransformerFactory.newInstance()
						.newTransformer();
				xformer.transform(source, result);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot process " + pomFile, e);
		}

	}

	private String getTagValue(Element eElement, String sTag) {
		NodeList nList = eElement.getElementsByTagName(sTag);
		if (nList.getLength() > 0) {
			NodeList nlList = nList.item(0).getChildNodes();
			Node nValue = (Node) nlList.item(0);
			return nValue.getNodeValue();
		} else
			return null;
	}

	private void setTagValue(Element eElement, String sTag, String value) {
		NodeList nList = eElement.getElementsByTagName(sTag);
		if (nList.getLength() > 0) {
			NodeList nlList = nList.item(0).getChildNodes();
			Node nValue = (Node) nlList.item(0);
			nValue.setNodeValue(value);
		}
	}

	public static void stdOut(Object obj) {
		System.out.println(obj);
	}

	public static void main(String argv[]) {
		new ConvertPoms_01_03(argv[0]).run();
	}

}
