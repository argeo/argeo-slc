package org.argeo.slc.maven.plugins.osgi;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * @author mbaudier
 * 
 */
public abstract class AbstractBundlesPackagerMojo extends AbstractOsgiMojo {

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * Directory of the simple bundles
	 * 
	 * @parameter expression="${bundlesDirectory}" default-value="."
	 * @required
	 */
	private File bundlesDirectory;

	/**
	 * Directory containing the packaged bundles.
	 * 
	 * @parameter expression="${packagedBundlesDir}"
	 *            default-value="${project.build.directory}/argeo-osgi"
	 * @required
	 */
	protected File packagedBundlesDir;

	/**
	 * Artifact id for the dependency pom
	 * 
	 * @parameter expression="${bundlesPomArtifactId}" default-value="bundles"
	 * @required
	 */
	protected String bundlesPomArtifactId;

	protected List analyze() throws MojoExecutionException {
		List list = new ArrayList();

		File[] bundleDirs = bundlesDirectory.listFiles(new FileFilter() {
			public boolean accept(File file) {
				if (!file.isDirectory())
					return false;

				return manifestFileFromDir(file).exists();
			}
		});
		for (int i = 0; i < bundleDirs.length; i++) {
			File dir = bundleDirs[i];
			File manifestFile = manifestFileFromDir(dir);
			String artifactId = dir.getName();
			File destFile = new File(packagedBundlesDir.getPath()
					+ File.separator + artifactId + ".jar");
			try {
				Manifest manifest = readManifest(manifestFile);
				// Symbolic name
				String symbolicNameMf = manifest.getMainAttributes().getValue(
						"Bundle-SymbolicName");
				if (!artifactId.equals(symbolicNameMf))
					getLog().warn(
							"Symbolic name " + symbolicNameMf
									+ " does not match with directory name "
									+ artifactId);

				// Version
				String versionMf = manifest.getMainAttributes().getValue(
						"Bundle-Version");
				int qIndex = versionMf.lastIndexOf(".qualifier");
				String versionMfMain;
				if (qIndex >= 0)
					versionMfMain = versionMf.substring(0, qIndex);
				else
					versionMfMain = versionMf;

				int sIndex = project.getVersion().lastIndexOf("-SNAPSHOT");
				String versionMain;
				boolean isSnapshot = false;
				if (sIndex >= 0) {// SNAPSHOT
					versionMain = project.getVersion().substring(0, sIndex);
					isSnapshot = true;
				} else {
					versionMain = project.getVersion();
				}

				if (!versionMain.equals(versionMfMain))
					getLog()
							.warn(
									"Main manifest version "
											+ versionMfMain
											+ " of bundle "
											+ artifactId
											+ " do not match with main project version "
											+ versionMain);

				String newVersionMf;
				String newVersionArt;
				if (isSnapshot) {
					newVersionMf = versionMfMain + ".SNAPSHOT";
					newVersionArt = versionMfMain + "-SNAPSHOT";
				} else {
					newVersionMf = versionMfMain;
					newVersionArt = versionMfMain;
				}

				manifest.getMainAttributes().putValue("Bundle-Version",
						newVersionMf);
				Artifact artifact = artifactFactory.createBuildArtifact(project
						.getGroupId(), artifactId, newVersionArt, "jar");
				BundlePackage bundlePackage = new BundlePackage(artifact, dir,
						manifest, destFile);
				list.add(bundlePackage);
			} catch (Exception e) {
				throw new MojoExecutionException("Could not analyze " + dir, e);
			}
		}
		return list;
	}

	protected File manifestFileFromDir(File dir) {
		return new File(dir + File.separator + "META-INF" + File.separator
				+ "MANIFEST.MF");
	}

	protected File bundlesPomFile() {
		return new File(packagedBundlesDir + File.separator + "bundles.pom");
	}

	protected Artifact bundlesPomArtifact() {
		return artifactFactory.createBuildArtifact(project.getGroupId(),
				bundlesPomArtifactId, project.getVersion(), "pom");
	}

	protected static class BundlePackage {
		private final Artifact artifact;
		private final File bundleDir;
		private final Manifest manifest;
		private final File packageFile;

		public BundlePackage(Artifact artifact, File bundleDir,
				Manifest manifest, File packageFile) {
			super();
			this.artifact = artifact;
			this.bundleDir = bundleDir;
			this.manifest = manifest;
			this.packageFile = packageFile;
		}

		public Artifact getArtifact() {
			return artifact;
		}

		public File getPackageFile() {
			return packageFile;
		}

		public File getBundleDir() {
			return bundleDir;
		}

		public Manifest getManifest() {
			return manifest;
		}
	}

	protected Manifest readManifest(File file) throws IOException {
		Manifest manifest = new Manifest();
		FileInputStream in = new FileInputStream(file);
		manifest.read(in);
		in.close();
		return manifest;
	}
}
