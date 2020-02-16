package org.argeo.slc.repo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.CategoryNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.osgi.framework.Constants;

/**
 * Creates a jar bundle from an ArgeoOsgiDistribution. This jar is then
 * persisted and indexed in a java repository using the OSGI Factory.
 * 
 * It does the following <list>
 * <li>Creates a Manifest</li>
 * <li>Creates files indexes (csv, feature.xml ...)</li>
 * <li>Populate the corresponding jar</li>
 * <li>Save it in the repository</li>
 * <li>Index the node and creates corresponding sha1 and md5 files</li> </list>
 * 
 */
public class ModularDistributionFactory implements Runnable {

	private OsgiFactory osgiFactory;
	private Session javaSession;
	private ArgeoOsgiDistribution osgiDistribution;
	private String modularDistributionSeparator = ",";
	private String artifactBasePath = RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH;
	private String artifactType = "jar";

	// Constants
	private final static String CSV_FILE_NAME = "modularDistribution.csv";
	private final DateFormat snapshotTimestamp = new SimpleDateFormat("YYYYMMddhhmm");

	// private final static String FEATURE_FILE_NAME = "feature.xml";
	// private static int BUFFER_SIZE = 10240;

	/** Convenience constructor with minimal configuration */
	public ModularDistributionFactory(OsgiFactory osgiFactory, ArgeoOsgiDistribution osgiDistribution) {
		this.osgiFactory = osgiFactory;
		this.osgiDistribution = osgiDistribution;
	}

	@Override
	public void run() {
		byte[] distFile = null;
		try {
			javaSession = osgiFactory.openJavaSession();

			if (artifactType == "jar")
				distFile = generateJarFile();
			else if (artifactType == "pom")
				distFile = generatePomFile();
			else
				throw new SlcException("Unimplemented distribution artifact type: " + artifactType + " for "
						+ osgiDistribution.toString());

			// Save in java repository
			Artifact osgiArtifact = new DefaultArtifact(osgiDistribution.getCategory(), osgiDistribution.getName(),
					artifactType, osgiDistribution.getVersion());

			Node distNode = RepoUtils.copyBytesAsArtifact(javaSession.getNode(artifactBasePath), osgiArtifact,
					distFile);

			// index
			osgiFactory.indexNode(distNode);

			// We use a specific session. Save before closing
			javaSession.save();
		} catch (RepositoryException e) {
			throw new SlcException(
					"JCR error while persisting modular distribution in JCR " + osgiDistribution.toString(), e);
		} finally {
			JcrUtils.logoutQuietly(javaSession);
		}
	}

	private byte[] generateJarFile() {
		ByteArrayOutputStream byteOut = null;
		JarOutputStream jarOut = null;
		try {
			byteOut = new ByteArrayOutputStream();
			jarOut = new JarOutputStream(byteOut, createManifest());
			// Create various indexes
			addToJar(createCsvDescriptor(), CSV_FILE_NAME, jarOut);
			jarOut.close();
			return byteOut.toByteArray();
		} catch (IOException e) {
			throw new SlcException("IO error while generating modular distribution " + osgiDistribution.toString(), e);
		} finally {
			IOUtils.closeQuietly(byteOut);
			IOUtils.closeQuietly(jarOut);
		}
	}

	// private void indexDistribution(Node distNode) throws RepositoryException
	// {
	// distNode.addMixin(SlcTypes.SLC_MODULAR_DISTRIBUTION);
	// distNode.addMixin(SlcTypes.SLC_CATEGORIZED_NAME_VERSION);
	// distNode.setProperty(SlcNames.SLC_CATEGORY,
	// osgiDistribution.getCategory());
	// distNode.setProperty(SlcNames.SLC_NAME, osgiDistribution.getName());
	// distNode.setProperty(SlcNames.SLC_VERSION,
	// osgiDistribution.getVersion());
	//
	// if (distNode.hasNode(SlcNames.SLC_MODULES))
	// distNode.getNode(SlcNames.SLC_MODULES).remove();
	// Node modules = distNode.addNode(SlcNames.SLC_MODULES,
	// NodeType.NT_UNSTRUCTURED);
	//
	// for (Iterator<? extends NameVersion> it = osgiDistribution
	// .nameVersions(); it.hasNext();)
	// addModule(modules, it.next());
	// }

	private Manifest createManifest() {
		Manifest manifest = new Manifest();

		// TODO make this configurable
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
//		addManifestAttribute(manifest, Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT, "JavaSE-1.8");
		addManifestAttribute(manifest, Constants.BUNDLE_VENDOR, "Argeo");
		addManifestAttribute(manifest, Constants.BUNDLE_MANIFESTVERSION, "2");
//		addManifestAttribute(manifest, "Bundle-License", "http://www.apache.org/licenses/LICENSE-2.0.txt");

		// TODO define a user friendly name
		addManifestAttribute(manifest, Constants.BUNDLE_NAME, osgiDistribution.getName());

		// Categorized name version
		addManifestAttribute(manifest, RepoConstants.SLC_CATEGORY_ID, osgiDistribution.getCategory());
		addManifestAttribute(manifest, Constants.BUNDLE_SYMBOLICNAME, osgiDistribution.getName());
		String version = osgiDistribution.getVersion();
		if (version.endsWith("-SNAPSHOT")) {
			version = version.substring(0, version.length() - "-SNAPSHOT".length());
			version = version + ".SNAPSHOT-r" + snapshotTimestamp.format(new Date());
		}
		addManifestAttribute(manifest, Constants.BUNDLE_VERSION, version);

		return manifest;
	}

	private void addManifestAttribute(Manifest manifest, String name, String value) {
		manifest.getMainAttributes().put(new Attributes.Name(name), value);
	}

	private byte[] createCsvDescriptor() {
		Writer writer = null;
		try {
			// FIXME remove use of tmp file.
			File tmpFile = File.createTempFile("modularDistribution", "csv");
			tmpFile.deleteOnExit();
			writer = new FileWriter(tmpFile);
			// Populate the file
			for (Iterator<? extends NameVersion> it = osgiDistribution.nameVersions(); it.hasNext();)
				writer.write(getCsvLine(it.next()));
			writer.flush();
			return FileUtils.readFileToByteArray(tmpFile);
		} catch (Exception e) {
			throw new SlcException("unable to create csv distribution file for " + osgiDistribution.toString(), e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	@SuppressWarnings("unused")
	private byte[] createFeatureDescriptor() {
		// Directly retrieved from Argeo maven plugin
		// Does not work due to the lack of org.codehaus.plexus/plexus-archiver
		// third party dependency

		throw new SlcException("Unimplemented method");

		// // protected void writeFeatureDescriptor() throws
		// MojoExecutionException {
		// File featureDesc = File.createTempFile("feature", "xml");
		// featureDesc.deleteOnExit();
		//
		// Writer writer = null;
		// try {
		// writer = new FileWriter(featureDesc);
		// PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter(writer);
		// xmlWriter.startElement("feature");
		// xmlWriter.addAttribute("id", project.getArtifactId());
		// xmlWriter.addAttribute("label", project.getName());
		//
		// // Version
		// String projectVersion = project.getVersion();
		// int indexSnapshot = projectVersion.indexOf("-SNAPSHOT");
		// if (indexSnapshot > -1)
		// projectVersion = projectVersion.substring(0, indexSnapshot);
		// projectVersion = projectVersion + ".qualifier";
		//
		// // project.
		// xmlWriter.addAttribute("version", projectVersion);
		//
		// Organization organization = project.getOrganization();
		// if (organization != null && organization.getName() != null)
		// xmlWriter.addAttribute("provider-name", organization.getName());
		//
		// if (project.getDescription() != null || project.getUrl() != null) {
		// xmlWriter.startElement("description");
		// if (project.getUrl() != null)
		// xmlWriter.addAttribute("url", project.getUrl());
		// if (project.getDescription() != null)
		// xmlWriter.writeText(project.getDescription());
		// xmlWriter.endElement();// description
		// }
		//
		// if (feature != null && feature.getCopyright() != null
		// || (organization != null && organization.getUrl() != null)) {
		// xmlWriter.startElement("copyright");
		// if (organization != null && organization.getUrl() != null)
		// xmlWriter.addAttribute("url", organization.getUrl());
		// if (feature.getCopyright() != null)
		// xmlWriter.writeText(feature.getCopyright());
		// xmlWriter.endElement();// copyright
		// }
		//
		// if (feature != null && feature.getUpdateSite() != null) {
		// xmlWriter.startElement("url");
		// xmlWriter.startElement("update");
		// xmlWriter.addAttribute("url", feature.getUpdateSite());
		// xmlWriter.endElement();// update
		// xmlWriter.endElement();// url
		// }
		//
		// List licenses = project.getLicenses();
		// if (licenses.size() > 0) {
		// // take the first one
		// License license = (License) licenses.get(0);
		// xmlWriter.startElement("license");
		//
		// if (license.getUrl() != null)
		// xmlWriter.addAttribute("url", license.getUrl());
		// if (license.getComments() != null)
		// xmlWriter.writeText(license.getComments());
		// else if (license.getName() != null)
		// xmlWriter.writeText(license.getName());
		// xmlWriter.endElement();// license
		// }
		//
		// // deploymentRepository.pathOf(null);
		// if (jarDirectory == null) {
		// Set dependencies = mavenDependencyManager
		// .getTransitiveProjectDependencies(project, remoteRepos,
		// local);
		// // // protected void writeFeatureDescriptor() throws
		// MojoExecutionException {
		// File featureDesc = File.createTempFile("feature", "xml");
		// featureDesc.deleteOnExit();
		//
		// Writer writer = null;
		// try {
		// writer = new FileWriter(featureDesc);
		// PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter(writer);
		// xmlWriter.startElement("feature");
		// xmlWriter.addAttribute("id", project.getArtifactId());
		// xmlWriter.addAttribute("label", project.getName());
		//
		// // Version
		// String projectVersion = project.getVersion();
		// int indexSnapshot = projectVersion.indexOf("-SNAPSHOT");
		// if (indexSnapshot > -1)
		// projectVersion = projectVersion.substring(0, indexSnapshot);
		// projectVersion = projectVersion + ".qualifier";
		//
		// // project.
		// xmlWriter.addAttribute("version", projectVersion);
		//
		// Organization organization = project.getOrganization();
		// if (organization != null && organization.getName() != null)
		// xmlWriter.addAttribute("provider-name", organization.getName());
		//
		// if (project.getDescription() != null || project.getUrl() != null) {
		// xmlWriter.startElement("description");
		// if (project.getUrl() != null)
		// xmlWriter.addAttribute("url", project.getUrl());
		// if (project.getDescription() != null)
		// xmlWriter.writeText(project.getDescription());
		// xmlWriter.endElement();// description
		// }
		//
		// if (feature != null && feature.getCopyright() != null
		// || (organization != null && organization.getUrl() != null)) {
		// xmlWriter.startElement("copyright");
		// if (organization != null && organization.getUrl() != null)
		// xmlWriter.addAttribute("url", organization.getUrl());
		// if (feature.getCopyright() != null)
		// xmlWriter.writeText(feature.getCopyright());
		// xmlWriter.endElement();// copyright
		// }
		//
		// if (feature != null && feature.getUpdateSite() != null) {
		// xmlWriter.startElement("url");
		// xmlWriter.startElement("update");
		// xmlWriter.addAttribute("url", feature.getUpdateSite());
		// xmlWriter.endElement();// update
		// xmlWriter.endElement();// url
		// }
		//
		// List licenses = project.getLicenses();
		// if (licenses.size() > 0) {
		// // take the first one
		// License license = (License) licenses.get(0);
		// xmlWriter.startElement("license");
		//
		// if (license.getUrl() != null)
		// xmlWriter.addAttribute("url", license.getUrl());
		// if (license.getComments() != null)
		// xmlWriter.writeText(license.getComments());
		// else if (license.getName() != null)
		// xmlWriter.writeText(license.getName());
		// xmlWriter.endElement();// license
		// }
		//
		// // deploymentRepository.pathOf(null);
		// if (jarDirectory == null) {
		// Set dependencies = mavenDependencyManager
		// .getTransitiveProjectDependencies(project, remoteRepos,
		// local);
		// for (Iterator it = dependencies.iterator(); it.hasNext();) {
		// Artifact artifact = (Artifact) it.next();
		// writeFeaturePlugin(xmlWriter, artifact.getFile());
		// }
		// } else {
		// // TODO: filter jars
		// File[] jars = jarDirectory.listFiles();
		// if (jars == null)
		// throw new MojoExecutionException("No jar found in "
		// + jarDirectory);
		// for (int i = 0; i < jars.length; i++) {
		// writeFeaturePlugin(xmlWriter, jars[i]);
		// }
		// }
		//
		// xmlWriter.endElement();// feature
		//
		// if (getLog().isDebugEnabled())
		// getLog().debug("Wrote Eclipse feature descriptor.");
		// } catch (Exception e) {
		// throw new MojoExecutionException("Cannot write feature descriptor",
		// e);
		// } finally {
		// IOUtil.close(writer);
		// }for (Iterator it = dependencies.iterator(); it.hasNext();) {
		// Artifact artifact = (Artifact) it.next();
		// writeFeaturePlugin(xmlWriter, artifact.getFile());
		// }
		// } else {
		// // TODO: filter jars
		// File[] jars = jarDirectory.listFiles();
		// if (jars == null)
		// throw new MojoExecutionException("No jar found in "
		// + jarDirectory);
		// for (int i = 0; i < jars.length; i++) {
		// writeFeaturePlugin(xmlWriter, jars[i]);
		// }
		// }
		//
		// xmlWriter.endElement();// feature
		//
		// if (getLog().isDebugEnabled())
		// getLog().debug("Wrote Eclipse feature descriptor.");
		// } catch (Exception e) {
		// throw new MojoExecutionException("Cannot write feature descriptor",
		// e);
		// } finally {
		// IOUtil.close(writer);
		// }
	}

	/** Create an Aether like distribution artifact */
	private byte[] generatePomFile() {
		StringBuilder b = new StringBuilder();
		// XML header
		b.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		b.append(
				"<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n");
		b.append("<modelVersion>4.0.0</modelVersion>");

		// Artifact
		b.append("<groupId>").append(osgiDistribution.getCategory()).append("</groupId>\n");
		b.append("<artifactId>").append(osgiDistribution.getName()).append("</artifactId>\n");
		b.append("<version>").append(osgiDistribution.getVersion()).append("</version>\n");
		b.append("<packaging>pom</packaging>\n");
		// p.append("<name>").append("Bundle Name").append("</name>\n");
		// p.append("<description>").append("Bundle
		// Description").append("</description>\n");

		// Dependencies
		b.append("<dependencies>\n");
		for (Iterator<? extends NameVersion> it = osgiDistribution.nameVersions(); it.hasNext();) {
			NameVersion nameVersion = it.next();
			if (!(nameVersion instanceof CategoryNameVersion))
				throw new SlcException("Unsupported type " + nameVersion.getClass());
			CategoryNameVersion nv = (CategoryNameVersion) nameVersion;
			b.append(getDependencySnippet(nv, false));
		}
		b.append("</dependencies>\n");

		// Dependency management
		b.append("<dependencyManagement>\n");
		b.append("<dependencies>\n");

		for (Iterator<? extends NameVersion> it = osgiDistribution.nameVersions(); it.hasNext();)
			b.append(getDependencySnippet((CategoryNameVersion) it.next(), true));
		b.append("</dependencies>\n");
		b.append("</dependencyManagement>\n");

		b.append("</project>\n");
		return b.toString().getBytes();
	}

	private String getDependencySnippet(CategoryNameVersion cnv, boolean includeVersion) { // , String type, String
																								// scope
		StringBuilder b = new StringBuilder();
		b.append("<dependency>\n");
		b.append("\t<groupId>").append(cnv.getCategory()).append("</groupId>\n");
		b.append("\t<artifactId>").append(cnv.getName()).append("</artifactId>\n");
		if (includeVersion)
			b.append("\t<version>").append(cnv.getVersion()).append("</version>\n");
		// if (type!= null)
		// p.append("\t<type>").append(type).append("</type>\n");
		// if (type!= null)
		// p.append("\t<scope>").append(scope).append("</scope>\n");
		b.append("</dependency>\n");
		return b.toString();
	}

	// Helpers
	private void addToJar(byte[] content, String name, JarOutputStream target) throws IOException {
		ByteArrayInputStream in = null;
		try {
			target.putNextEntry(new JarEntry(name));
			in = new ByteArrayInputStream(content);
			byte[] buffer = new byte[1024];
			while (true) {
				int count = in.read(buffer);
				if (count == -1)
					break;
				target.write(buffer, 0, count);
			}
			target.closeEntry();
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private String getCsvLine(NameVersion nameVersion) throws RepositoryException {
		if (!(nameVersion instanceof CategoryNameVersion))
			throw new SlcException("Unsupported type " + nameVersion.getClass());
		CategoryNameVersion cnv = (CategoryNameVersion) nameVersion;
		StringBuilder builder = new StringBuilder();

		builder.append(cnv.getName());
		builder.append(modularDistributionSeparator);
		builder.append(nameVersion.getVersion());
		builder.append(modularDistributionSeparator);
		builder.append(cnv.getCategory().replace('.', '/'));
		// MavenConventionsUtils.groupPath("", cnv.getCategory());
		builder.append('/');
		builder.append(cnv.getName());
		builder.append('/');
		builder.append(cnv.getVersion());
		builder.append('/');
		builder.append(cnv.getName());
		builder.append('-');
		builder.append(cnv.getVersion());
		builder.append('.');
		// TODO make this dynamic
		builder.append("jar");
		builder.append("\n");

		return builder.toString();
	}

	/** Enable dependency injection */
	public void setOsgiFactory(OsgiFactory osgiFactory) {
		this.osgiFactory = osgiFactory;
	}

	public void setOsgiDistribution(ArgeoOsgiDistribution osgiDistribution) {
		this.osgiDistribution = osgiDistribution;
	}

	public void setModularDistributionSeparator(String modularDistributionSeparator) {
		this.modularDistributionSeparator = modularDistributionSeparator;
	}

	public void setArtifactBasePath(String artifactBasePath) {
		this.artifactBasePath = artifactBasePath;
	}

	public void setArtifactType(String artifactType) {
		this.artifactType = artifactType;
	}
}