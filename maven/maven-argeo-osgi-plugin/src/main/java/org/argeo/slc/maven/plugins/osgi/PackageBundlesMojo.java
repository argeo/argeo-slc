package org.argeo.slc.maven.plugins.osgi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.util.DefaultFileSet;

/**
 * @goal package-bundles
 * @phase package
 * @author mbaudier
 * 
 */
public class PackageBundlesMojo extends AbstractBundlesPackagerMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		StringBuffer bundlesPom = new StringBuffer();
		// not using append() systematically for the sake of clarity
		bundlesPom.append("<project>\n");
		bundlesPom.append("\t<modelVersion>4.0.0</modelVersion>\n");
		bundlesPom.append("\t<parent>\n");
		bundlesPom.append("\t\t<groupId>"
				+ project.getParentArtifact().getGroupId() + "</groupId>\n");
		bundlesPom.append("\t\t<artifactId>"
				+ project.getParentArtifact().getArtifactId()
				+ "</artifactId>\n");
		bundlesPom.append("\t\t<version>"
				+ project.getParentArtifact().getVersion() + "</version>\n");
		bundlesPom.append("\t</parent>\n");
		bundlesPom
				.append("\t<groupId>" + project.getGroupId() + "</groupId>\n");
		bundlesPom.append("\t<artifactId>" + bundlesPomArtifactId
				+ "</artifactId>\n");
		bundlesPom.append("\t<packaging>pom</packaging>\n");
		bundlesPom.append("\t<dependencies>\n");

		List bundlePackages = analyze();

		for (int i = 0; i < bundlePackages.size(); i++) {
			AbstractBundlesPackagerMojo.BundlePackage bundlePackage = (BundlePackage) bundlePackages
					.get(i);

			File manifestFile = new File(bundlePackage.getPackageFile()
					.getPath()
					+ ".MF");

			// Package as jar
			JarArchiver jarArchiver = new JarArchiver();
			jarArchiver.setDestFile(bundlePackage.getPackageFile());
			DefaultFileSet fileSet = new DefaultFileSet();
			fileSet.setDirectory(bundlePackage.getBundleDir());
			String[] includes = { "**/*" };
			String[] excludes = { "**/.svn", "**/.svn/**" };
			fileSet.setIncludes(includes);
			fileSet.setExcludes(excludes);
			try {
				jarArchiver.addFileSet(fileSet);

				// Write manifest
				FileOutputStream out = new FileOutputStream(manifestFile);
				bundlePackage.getManifest().getMainAttributes().put(
						Attributes.Name.MANIFEST_VERSION, "1.0");

				System.out.println("# BUNDLE "
						+ bundlePackage.getArtifact().getArtifactId());
				Attributes mainAttrs = bundlePackage.getManifest()
						.getMainAttributes();
				for (Iterator it = mainAttrs.keySet().iterator(); it.hasNext();) {
					Object key = it.next();
					Object value = mainAttrs.get(key);
					System.out.println(key + ": " + value);
				}

				bundlePackage.getManifest().write(out);
				out.close();
				jarArchiver.setManifest(manifestFile);

				jarArchiver.createArchive();
			} catch (Exception e) {
				throw new MojoExecutionException("Could not package bundle "
						+ bundlePackage.getBundleDir(), e);
			}

			// update dependencies POM
			bundlesPom.append("\t\t<dependency>\n");
			bundlesPom
					.append("\t\t\t<groupId>"
							+ bundlePackage.getArtifact().getGroupId()
							+ "</groupId>\n");
			bundlesPom.append("\t\t\t<artifactId>"
					+ bundlePackage.getArtifact().getArtifactId()
					+ "</artifactId>\n");
			bundlesPom
					.append("\t\t\t<version>"
							+ bundlePackage.getArtifact().getVersion()
							+ "</version>\n");
			bundlesPom.append("\t\t</dependency>\n");

		}

		bundlesPom.append("\t</dependencies>\n");
		bundlesPom.append("</project>\n");

		try {
			FileWriter writer = new FileWriter(bundlesPomFile());
			writer.write(bundlesPom.toString());
			writer.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write dependency pom",
					e);
		}
	}

}
