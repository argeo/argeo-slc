package org.argeo.slc.maven.plugins.osgi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.util.DefaultFileSet;

/** Build the bundle jars
 * @goal package-bundles
 * @phase package
 * @author mbaudier
 * 
 */
public class PackageBundlesMojo extends AbstractBundlesPackagerMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		StringBuffer bundlesPom = createPomFileHeader(project
				.getParentArtifact().getGroupId(), project.getParentArtifact()
				.getArtifactId(), project.getParentArtifact().getBaseVersion(),
				project.getGroupId(), bundlesPomArtifactId, "pom");
		bundlesPom.append("\t<dependencies>\n");

		List bundlePackages = analyze(true);

		for (int i = 0; i < bundlePackages.size(); i++) {
			AbstractBundlesPackagerMojo.BundlePackage bundlePackage = (BundlePackage) bundlePackages
					.get(i);

			// Package as jar
			JarArchiver jarArchiver = new JarArchiver();
			jarArchiver.setDestFile(bundlePackage.getPackageFile());
			DefaultFileSet fileSet = new DefaultFileSet();
			fileSet.setDirectory(bundlePackage.getBundleDir());
			String[] includes = { "**/*" };
			String[] excludes = { "**/.svn", "**/.svn/**" };
			fileSet.setIncludes(includes);
			fileSet.setExcludes(excludes);
			
			FileOutputStream manifestOut = null;
			try {
				File manifestFile = bundlePackage.getManifestFile();
				jarArchiver.addFileSet(fileSet);

				// Write manifest
				manifestOut = new FileOutputStream(manifestFile);

				System.out.println("\n# BUNDLE "
						+ bundlePackage.getArtifact().getArtifactId());
				Attributes mainAttrs = bundlePackage.getManifest()
						.getMainAttributes();
				for (Iterator it = mainAttrs.keySet().iterator(); it.hasNext();) {
					Object key = it.next();
					Object value = mainAttrs.get(key);
					System.out.println(key + ": " + value);
				}

				bundlePackage.getManifest().write(manifestOut);

				// Write jar
				jarArchiver.setManifest(manifestFile);
				jarArchiver.createArchive();
			} catch (Exception e) {
				throw new MojoExecutionException("Could not package bundle "
						+ bundlePackage.getBundleDir(), e);
			}finally{
				IOUtils.closeQuietly(manifestOut);
			}

			// Write bundle POM
			File pomFile = bundlePackage.getPomFile();
			StringBuffer pomBuf = createPomFileHeader(project
					.getParentArtifact().getGroupId(), project
					.getParentArtifact().getArtifactId(), project
					.getParentArtifact().getBaseVersion(), bundlePackage
					.getArtifact().getGroupId(), bundlePackage.getArtifact()
					.getArtifactId(), "jar");
			String pomStr = closePomFile(pomBuf);
			try {
				FileUtils.writeStringToFile(pomFile, pomStr);
			} catch (IOException e) {
				throw new MojoExecutionException(
						"Could not write pom for bundle "
								+ bundlePackage.getArtifact().getArtifactId(),
						e);
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
		String bundlePomStr = closePomFile(bundlesPom);

		try {
			FileUtils.writeStringToFile(bundlesPomFile(), bundlePomStr);
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write dependency pom",
					e);
		}
	}
}
