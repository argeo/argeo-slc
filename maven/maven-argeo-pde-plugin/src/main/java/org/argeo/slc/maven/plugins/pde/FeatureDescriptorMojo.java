package org.argeo.slc.maven.plugins.pde;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;

/**
 * Generates a feature descripto based on the pom
 * 
 * @goal featureDescriptor
 * @phase process-resources
 */
public class FeatureDescriptorMojo extends AbstractMojo {
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
	 * @required
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

			String projectVersion = project.getVersion();
			int indexSnapshot = projectVersion.indexOf("-SNAPSHOT");
			if (indexSnapshot > -1)
				projectVersion = projectVersion.substring(0, indexSnapshot);
			projectVersion = projectVersion + ".qualifier";

			// project.
			xmlWriter.addAttribute("version", projectVersion);

			List plugins = feature.getPlugins();
			for (int i = 0; i < plugins.size(); i++) {
				Plugin plugin = (Plugin) plugins.get(i);
				xmlWriter.startElement("plugin");
				xmlWriter.addAttribute("id", plugin.getId());
				xmlWriter.addAttribute("version", plugin.getVersion());
				xmlWriter.endElement();// plugin
			}

			xmlWriter.endElement();// feature
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot write feature descriptor",
					e);
		}
		IOUtil.close(fileWriter);
		getLog().info("FeatureDescriptorMojo done");
	}
}
