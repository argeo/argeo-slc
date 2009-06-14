package org.argeo.slc.maven.plugins.osgi;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Base class for MoJo analyzing a set of bundles directories.
 * 
 * @author mbaudier
 * 
 */
public abstract class AbstractBundlesPackagerMojo extends AbstractOsgiMojo {

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

	/**
	 * Whether should fail if MANIFEST version are not in line with pom version.
	 * 
	 * @parameter expression="${strictManifestVersion}" default-value="false"
	 * @required
	 */
	protected boolean strictManifestVersion;

	/**
	 * Whether the manifest should be updated with the release version.
	 * 
	 * @parameter expression="${updateManifestWhenReleasing}"
	 *            default-value="true"
	 * @required
	 */
	protected boolean updateManifestWhenReleasing;

	/**
	 * Whether should fail if symbolic name does not match artifact id.
	 * 
	 * @parameter expression="${strictSymbolicName}" default-value="false"
	 * @required
	 */
	protected boolean strictSymbolicName;

	/**
	 * Build number (provided by the build number plugin in general).
	 * 
	 * @parameter expression="${buildNumber}"
	 * @required
	 */
	protected String buildNumber;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

	protected List analyze(boolean willGenerate) throws MojoExecutionException {
		List list = new ArrayList();

		File[] bundleDirs = bundlesDirectory.listFiles(bundleFileFilter());
		for (int i = 0; i < bundleDirs.length; i++) {

			File dir = bundleDirs[i];
			BundlePackage bundlePackage;
			try {
				bundlePackage = processBundleDir(dir, willGenerate);
			} catch (Exception e) {
				throw new MojoExecutionException("Could not analyze " + dir, e);
			}
			list.add(bundlePackage);

		}
		return list;
	}

	protected BundlePackage processBundleDir(File dir, boolean willGenerate)
			throws Exception {
		File manifestFile = manifestFileFromDir(dir);
		String artifactId = dir.getName();
		File destFile = new File(packagedBundlesDir.getPath() + File.separator
				+ artifactId + ".jar");

		String manifestStr = FileUtils.readFileToString(manifestFile);
		char lastChar = manifestStr.charAt(manifestStr.length() - 1);
		if (lastChar != '\n')
			throw new RuntimeException("Manifest " + manifestFile
					+ " is not valid,"
					+ " it does not end with and endline character.");

		Manifest manifest = readManifest(manifestFile);
		// Symbolic name
		String symbolicNameMf = manifest.getMainAttributes().getValue(
				"Bundle-SymbolicName");
		if (!artifactId.equals(symbolicNameMf)) {
			String msg = "Symbolic name " + symbolicNameMf
					+ " does not match with directory name " + artifactId;
			if (strictSymbolicName)
				throw new RuntimeException(msg);
			else
				getLog().warn(msg);
		}

		// Version
		String versionMf = manifest.getMainAttributes().getValue(
				"Bundle-Version");
		int qIndex = versionMf.lastIndexOf(".SNAPSHOT");
		String versionMfMain;
		if (qIndex >= 0)
			versionMfMain = versionMf.substring(0, qIndex);
		else
			versionMfMain = versionMf;

		int sIndex = snapshotIndex();
		String versionMain;
		String buildId;
		boolean isSnapshot = false;
		if (sIndex >= 0) {// SNAPSHOT
			versionMain = versionMain(sIndex);
			// buildId = "D_" + sdf.format(new Date());// D for dev
			buildId = "SNAPSHOT-r" + buildNumber;
			isSnapshot = true;
		} else {
			versionMain = project.getVersion();
			// buildId = "R_" + sdf.format(new Date());// R for release
			//buildId = "R" + sdf.format(new Date());
			buildId = "r" + buildNumber;
		}

		if (!versionMain.equals(versionMfMain)) {
			String msg = "Main manifest version " + versionMfMain
					+ " of bundle " + artifactId
					+ " do not match with main project version " + versionMain;
			if (strictManifestVersion)
				throw new RuntimeException(msg);
			else
				getLog().warn(msg);
		}

		String newVersionMf = versionMfMain + "." + buildId;
		String newVersionArt;
		if (isSnapshot) {
			newVersionArt = versionMfMain + "-SNAPSHOT";
		} else {
			newVersionArt = versionMfMain;
		}

		// boolean debug = true;
		boolean debug = getLog().isDebugEnabled();
		if (debug && willGenerate) {
			getLog().info("\n## " + artifactId);
			getLog().info("project.getVersion()=" + project.getVersion());
			getLog().info("newVersionMf=" + newVersionMf);
		}

		manifest.getMainAttributes().putValue("Bundle-Version", newVersionMf);
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION,
				"1.0");

		Artifact artifact = artifactFactory.createBuildArtifact(project
				.getGroupId(), artifactId, newVersionArt, "jar");
		return new BundlePackage(artifact, dir, new Manifest(manifest),
				destFile);
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

	protected StringBuffer createPomFileHeader(String parentGroupId,
			String parentArtifactId, String parentBaseVersion, String groupId,
			String artifactId, String packaging) {
		StringBuffer pom = new StringBuffer();
		// not using append() systematically for the sake of clarity
		pom.append("<project>\n");
		pom.append("\t<modelVersion>4.0.0</modelVersion>\n");
		pom.append("\t<parent>\n");
		pom.append("\t\t<groupId>" + parentGroupId + "</groupId>\n");
		pom.append("\t\t<artifactId>" + parentArtifactId + "</artifactId>\n");
		pom.append("\t\t<version>" + parentBaseVersion + "</version>\n");
		pom.append("\t</parent>\n");
		pom.append("\t<groupId>" + groupId + "</groupId>\n");
		pom.append("\t<artifactId>" + artifactId + "</artifactId>\n");
		pom.append("\t<packaging>" + packaging + "</packaging>\n");
		return pom;

		// TODO: use the Model object e.g.: (from install plugin)
		// Model model = new Model();
		// model.setModelVersion( "4.0.0" );
		// model.setGroupId( groupId );
		// model.setArtifactId( artifactId );
		// model.setVersion( version );
		// model.setPackaging( packaging );
		// model.setDescription( "POM was created from install:install-file" );
		// fw = new FileWriter( tempFile );
		// tempFile.deleteOnExit();
		// new MavenXpp3Writer().write( fw, model );
		// ArtifactMetadata metadata = new ProjectArtifactMetadata( artifact,
		// tempFile );
		// artifact.addMetadata( metadata );

	}

	/** Simple close the project tag */
	protected String closePomFile(StringBuffer pom) {
		pom.append("</project>\n");
		return pom.toString();
	}

	protected Manifest readManifest(File file) throws IOException {
		Manifest manifest = new Manifest();
		FileInputStream in = new FileInputStream(file);
		manifest.read(in);
		in.close();
		return manifest;
	}

	protected int snapshotIndex() {
		return project.getModel().getVersion().lastIndexOf("-SNAPSHOT");
	}

	protected String versionMain(int sIndex) {
		return project.getVersion().substring(0, sIndex);
	}

	protected File getBundleDirectory() {
		return bundlesDirectory;
	}

	protected FileFilter bundleFileFilter() {
		return new FileFilter() {
			public boolean accept(File file) {
				if (!file.isDirectory())
					return false;

				return manifestFileFromDir(file).exists();
			}
		};
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

		public File getManifestFile() {
			return new File(getPackageFile().getPath() + ".MF");
		}

		public File getPomFile() {
			return new File(getPackageFile().getPath() + ".pom.xml");
		}

		public String toString() {
			return "Bundle: " + bundleDir;
		}

	}

}
