package org.argeo.slc.maven.plugins.osgi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.License;
import org.apache.maven.model.Organization;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.argeo.slc.maven.plugin.MavenDependencyManager;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;

/**
 * Generates a feature descriptor based on the pom
 * 
 * @goal featureDescriptor
 * @phase process-resources
 */
public class FeatureDescriptorMojo extends AbstractOsgiMojo {
	/** @component */
	private MavenDependencyManager mavenDependencyManager;

	/**
	 * The Maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The directory for the pom
	 * 
	 * @parameter expression="${basedir}"
	 * @required
	 */
	private File baseDir;

	/**
	 * Information about the feature
	 * 
	 * @parameter expression="${argeo-pde.feature}"
	 */
	private Feature feature;

	public void execute() throws MojoExecutionException {
		File featureDesc = new File(baseDir, "feature.xml");
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(featureDesc);
			PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter(
					fileWriter);
			xmlWriter.startElement("feature");
			xmlWriter.addAttribute("id", project.getArtifactId());
			xmlWriter.addAttribute("label", project.getName());

			// Version
			String projectVersion = project.getVersion();
			int indexSnapshot = projectVersion.indexOf("-SNAPSHOT");
			if (indexSnapshot > -1)
				projectVersion = projectVersion.substring(0, indexSnapshot);
			projectVersion = projectVersion + ".qualifier";

			// project.
			xmlWriter.addAttribute("version", projectVersion);

			Organization organization = project.getOrganization();
			if (organization != null && organization.getName() != null)
				xmlWriter.addAttribute("provider-name", organization.getName());

			if (project.getDescription() != null || project.getUrl() != null) {
				xmlWriter.startElement("description");
				if (project.getUrl() != null)
					xmlWriter.addAttribute("url", project.getUrl());
				if (project.getDescription() != null)
					xmlWriter.writeText(project.getDescription());
				xmlWriter.endElement();// description
			}

			if (feature != null && feature.getCopyright() != null
					|| (organization != null && organization.getUrl() != null)) {
				xmlWriter.startElement("copyright");
				if (organization != null && organization.getUrl() != null)
					xmlWriter.addAttribute("url", organization.getUrl());
				if (feature.getCopyright() != null)
					xmlWriter.writeText(feature.getCopyright());
				xmlWriter.endElement();// copyright
			}

			if (feature != null && feature.getUpdateSite() != null) {
				xmlWriter.startElement("url");
				xmlWriter.startElement("update");
				xmlWriter.addAttribute("url", feature.getUpdateSite());
				xmlWriter.endElement();// update
				xmlWriter.endElement();// url
			}

			List licenses = project.getLicenses();
			if (licenses.size() > 0) {
				// take the first one
				License license = (License) licenses.get(0);
				xmlWriter.startElement("license");

				if (license.getUrl() != null)
					xmlWriter.addAttribute("url", license.getUrl());
				if (license.getComments() != null)
					xmlWriter.writeText(license.getComments());
				else if (license.getName() != null)
					xmlWriter.writeText(license.getName());
				xmlWriter.endElement();// license
			}

			// deploymentRepository.pathOf(null);
			Set dependencies = mavenDependencyManager
					.getTransitiveProjectDependencies(project, remoteRepos,
							local);
			for (Iterator it = dependencies.iterator(); it.hasNext();) {
				Artifact depArtifact = (Artifact) it.next();

				String symbolicName = null;
				String version = null;
				File artifactFile = depArtifact.getFile();
				JarInputStream jarInputStream = null;
				try {
					jarInputStream = new JarInputStream(new FileInputStream(
							artifactFile));
					symbolicName = jarInputStream.getManifest()
							.getMainAttributes()
							.getValue("Bundle-SymbolicName");
					version = jarInputStream.getManifest().getMainAttributes()
							.getValue("Bundle-Version");
				} catch (Exception e) {
					// probably not a jar, skipping
					if (getLog().isDebugEnabled())
						getLog().debug(
								"Skipping artifact " + depArtifact
										+ " because of " + e.getMessage());
					continue;
				} finally {
					IOUtils.closeQuietly(jarInputStream);
				}

				if (symbolicName != null && version != null) {
					xmlWriter.startElement("plugin");
					xmlWriter.addAttribute("id", symbolicName);
					xmlWriter.addAttribute("version", version);
					xmlWriter.addAttribute("unpack", "false");
					xmlWriter.endElement();// plugin
				}
			}
			/*
			 * List plugins = feature.getPlugins(); for (int i = 0; i <
			 * plugins.size(); i++) { Plugin plugin = (Plugin) plugins.get(i);
			 * xmlWriter.startElement("plugin"); xmlWriter.addAttribute("id",
			 * plugin.getId()); xmlWriter.addAttribute("version",
			 * plugin.getVersion()); xmlWriter.addAttribute("unpack",
			 * plugin.getUnpack()); xmlWriter.endElement();// plugin }
			 */
			xmlWriter.endElement();// feature
		} catch (Exception e) {
			throw new MojoExecutionException("Cannot write feature descriptor",
					e);
		}
		IOUtil.close(fileWriter);
		getLog().info("FeatureDescriptorMojo done");
	}
}
