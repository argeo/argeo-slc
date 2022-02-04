package org.argeo.slc.build;

import static org.argeo.slc.ManifestConstants.BUNDLE_LICENSE;
import static org.argeo.slc.ManifestConstants.SLC_ORIGIN_M2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.ManifestConstants;
import org.argeo.slc.NameVersion;
import org.argeo.slc.build.m2.DefaultArtifact;
import org.argeo.slc.build.m2.MavenConventionsUtils;

public class A2Factory {
	private final static Logger logger = System.getLogger(A2Factory.class.getName());

	private final static String COMMON_BND = "common.bnd";

	private Path originBase;
	private Path factoryBase;

	/** key is URI prefix, value list of base URLs */
	private Map<String, List<String>> mirrors = new HashMap<String, List<String>>();

	public A2Factory(Path originBase, Path factoryBase) {
		super();
		this.originBase = originBase;
		this.factoryBase = factoryBase;

		// TODO make it configurable
		List<String> eclipseMirrors = new ArrayList<>();
		eclipseMirrors.add("https://archive.eclipse.org/");

		mirrors.put("http://www.eclipse.org/downloads", eclipseMirrors);
	}

	public void processM2BasedDistributionUnit(Path duDir) {
		try {
			String category = duDir.getParent().getFileName().toString();
			Path targetCategoryBase = factoryBase.resolve(category);
			Path commonBnd = duDir.resolve(COMMON_BND);
			Properties commonProps = new Properties();
			try (InputStream in = Files.newInputStream(commonBnd)) {
				commonProps.load(in);
			}

			String m2Version = commonProps.getProperty(SLC_ORIGIN_M2.toString());
			if (!m2Version.startsWith(":")) {
				throw new IllegalStateException("Only the M2 version can be specified: " + m2Version);
			}
			m2Version = m2Version.substring(1);

			// String license = commonProps.getProperty(BUNDLE_LICENSE.toString());

			DirectoryStream<Path> ds = Files.newDirectoryStream(duDir,
					(p) -> p.getFileName().toString().endsWith(".bnd")
							&& !p.getFileName().toString().equals(COMMON_BND));
			for (Path p : ds) {
				Properties fileProps = new Properties();
				try (InputStream in = Files.newInputStream(p)) {
					fileProps.load(in);
				}
				String m2Coordinates = fileProps.getProperty(SLC_ORIGIN_M2.toString());
				DefaultArtifact artifact = new DefaultArtifact(m2Coordinates);

				// temporary rewrite, for migration
				String localLicense = fileProps.getProperty(BUNDLE_LICENSE.toString());
				if (localLicense != null || artifact.getVersion() != null) {
					fileProps.remove(BUNDLE_LICENSE.toString());
					fileProps.put(SLC_ORIGIN_M2.toString(), artifact.getGroupId() + ":" + artifact.getArtifactId());
					try (Writer writer = Files.newBufferedWriter(p)) {
						for (Object key : fileProps.keySet()) {
							String value = fileProps.getProperty(key.toString());
							writer.write(key + ": " + value + '\n');
						}
						logger.log(Level.DEBUG, () -> "Migrated  " + p);
					}
				}

				artifact.setVersion(m2Version);
				URL url = MavenConventionsUtils.mavenCentralUrl(artifact);
				Path downloaded = download(url, originBase, artifact.toM2Coordinates() + ".jar");

				// prepare manifest entries
				Map<String, String> entries = new HashMap<>();
				for (Object key : commonProps.keySet()) {
					entries.put(key.toString(), commonProps.getProperty(key.toString()));
				}
				fileEntries: for (Object key : fileProps.keySet()) {
					if (ManifestConstants.SLC_ORIGIN_M2.toString().equals(key))
						continue fileEntries;
					String value = fileProps.getProperty(key.toString());
					String previousValue = entries.put(key.toString(), value);
					if (previousValue != null) {
						logger.log(Level.WARNING,
								downloaded + ": " + key + " was " + previousValue + ", overridden with " + value);
					}
				}
				entries.put(ManifestConstants.SLC_ORIGIN_M2.toString(), artifact.toM2Coordinates());
				Path targetBundleDir = processBundleJar(downloaded, targetCategoryBase, entries);
				logger.log(Level.DEBUG, () -> "Processed " + downloaded);

				// sources
				DefaultArtifact sourcesArtifact = new DefaultArtifact(artifact.toM2Coordinates(), "sources");
				URL sourcesUrl = MavenConventionsUtils.mavenCentralUrl(sourcesArtifact);
				Path sourcesDownloaded = download(sourcesUrl, originBase, artifact.toM2Coordinates() + ".sources.jar");
				processM2SourceJar(sourcesDownloaded, targetBundleDir);
				logger.log(Level.DEBUG, () -> "Processed " + sourcesDownloaded);
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot process " + duDir, e);
		}

	}

	protected void processM2SourceJar(Path file, Path targetBundleDir) throws IOException {
		try (JarInputStream jarIn = new JarInputStream(Files.newInputStream(file), false)) {
			Path targetSourceDir = targetBundleDir.resolve("OSGI-OPT/src");

			// TODO make it less dangerous?
			if (Files.exists(targetSourceDir)) {
				deleteDirectory(targetSourceDir);
			} else {
				Files.createDirectories(targetSourceDir);
			}

			// copy entries
			JarEntry entry;
			entries: while ((entry = jarIn.getNextJarEntry()) != null) {
				if (entry.isDirectory())
					continue entries;
				if (entry.getName().startsWith("META-INF"))// skip META-INF entries
					continue entries;
				Path target = targetSourceDir.resolve(entry.getName());
				Files.createDirectories(target.getParent());
				Files.copy(jarIn, target);
				logger.log(Level.TRACE, () -> "Copied source " + target);
			}
		}

	}

	public void processEclipseArchive(Path duDir) {
		try {
			String category = duDir.getParent().getFileName().toString();
			Path targetCategoryBase = factoryBase.resolve(category);
			Files.createDirectories(targetCategoryBase);
			Files.createDirectories(originBase);

			Path commonBnd = duDir.resolve(COMMON_BND);
			Properties commonProps = new Properties();
			try (InputStream in = Files.newInputStream(commonBnd)) {
				commonProps.load(in);
			}
			Properties includes = new Properties();
			try (InputStream in = Files.newInputStream(duDir.resolve("includes.properties"))) {
				includes.load(in);
			}
			String url = commonProps.getProperty(ManifestConstants.SLC_ORIGIN_URI.toString());
			Path downloaded = tryDownload(url, originBase);

			FileSystem zipFs = FileSystems.newFileSystem(downloaded, null);

			List<PathMatcher> pathMatchers = new ArrayList<>();
			for (Object pattern : includes.keySet()) {
				PathMatcher pathMatcher = zipFs.getPathMatcher("glob:/" + pattern);
				pathMatchers.add(pathMatcher);
			}

			Files.walkFileTree(zipFs.getRootDirectories().iterator().next(), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					pathMatchers: for (PathMatcher pathMatcher : pathMatchers) {
						if (pathMatcher.matches(file)) {
//							Path target = targetBase.resolve(file.getFileName().toString());
//							if (!Files.exists(target)) {
//								Files.copy(file, target);
//								logger.log(Level.DEBUG, () -> "Copied " + target + " from " + downloaded);
//							} else {
//								logger.log(Level.DEBUG, () -> target + " already exists.");
//
//							}
							if (file.getFileName().toString().contains(".source_")) {
								processEclipseSourceJar(file, targetCategoryBase);
								logger.log(Level.DEBUG, () -> "Processed source " + file);

							} else {
								processBundleJar(file, targetCategoryBase, new HashMap<>());
								logger.log(Level.DEBUG, () -> "Processed " + file);
							}
							continue pathMatchers;
						}
					}
					return super.visitFile(file, attrs);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException("Cannot process " + duDir, e);
		}

	}

	protected Path processBundleJar(Path file, Path targetBase, Map<String, String> additionalManifestEntries)
			throws IOException {
		NameVersion nameVersion;
		Path targetBundleDir;
		try (JarInputStream jarIn = new JarInputStream(Files.newInputStream(file), false)) {
			Manifest manifest = jarIn.getManifest();
			nameVersion = nameVersionFromManifest(manifest);
			targetBundleDir = targetBase.resolve(nameVersion.getName() + "." + nameVersion.getBranch());

			// TODO make it less dangerous?
			if (Files.exists(targetBundleDir)) {
				deleteDirectory(targetBundleDir);
			}

			// copy entries
			JarEntry entry;
			entries: while ((entry = jarIn.getNextJarEntry()) != null) {
				if (entry.isDirectory())
					continue entries;
				Path target = targetBundleDir.resolve(entry.getName());
				Files.createDirectories(target.getParent());
				Files.copy(jarIn, target);
				logger.log(Level.TRACE, () -> "Copied " + target);
			}

			// copy MANIFEST
			Path manifestPath = targetBundleDir.resolve("META-INF/MANIFEST.MF");
			Files.createDirectories(manifestPath.getParent());
			for (String key : additionalManifestEntries.keySet()) {
				String value = additionalManifestEntries.get(key);
				Object previousValue = manifest.getMainAttributes().putValue(key, value);
				if (previousValue != null && !previousValue.equals(value)) {
					logger.log(Level.WARNING,
							file.getFileName() + ": " + key + " was " + previousValue + ", overridden with " + value);
				}
			}
			try (OutputStream out = Files.newOutputStream(manifestPath)) {
				manifest.write(out);
			}
		}
		return targetBundleDir;
	}

	protected void processEclipseSourceJar(Path file, Path targetBase) throws IOException {
		// NameVersion nameVersion;
		Path targetBundleDir;
		try (JarInputStream jarIn = new JarInputStream(Files.newInputStream(file), false)) {
			Manifest manifest = jarIn.getManifest();
			// nameVersion = nameVersionFromManifest(manifest);

			String[] relatedBundle = manifest.getMainAttributes().getValue("Eclipse-SourceBundle").split(";");
			String version = relatedBundle[1].substring("version=\"".length());
			version = version.substring(0, version.length() - 1);
			NameVersion nameVersion = new DefaultNameVersion(relatedBundle[0], version);
			targetBundleDir = targetBase.resolve(nameVersion.getName() + "." + nameVersion.getBranch());

			Path targetSourceDir = targetBundleDir.resolve("OSGI-OPT/src");

			// TODO make it less dangerous?
			if (Files.exists(targetSourceDir)) {
				deleteDirectory(targetSourceDir);
			} else {
				Files.createDirectories(targetSourceDir);
			}

			// copy entries
			JarEntry entry;
			entries: while ((entry = jarIn.getNextJarEntry()) != null) {
				if (entry.isDirectory())
					continue entries;
				if (entry.getName().startsWith("META-INF"))// skip META-INF entries
					continue entries;
				Path target = targetSourceDir.resolve(entry.getName());
				Files.createDirectories(target.getParent());
				Files.copy(jarIn, target);
				logger.log(Level.TRACE, () -> "Copied source " + target);
			}

			// copy MANIFEST
//			Path manifestPath = targetBundleDir.resolve("META-INF/MANIFEST.MF");
//			Files.createDirectories(manifestPath.getParent());
//			try (OutputStream out = Files.newOutputStream(manifestPath)) {
//				manifest.write(out);
//			}
		}

	}

	static void deleteDirectory(Path path) throws IOException {
		if (!Files.exists(path))
			return;
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult postVisitDirectory(Path directory, IOException e) throws IOException {
				if (e != null)
					throw e;
				Files.delete(directory);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	protected NameVersion nameVersionFromManifest(Manifest manifest) {
		Attributes attrs = manifest.getMainAttributes();
		// symbolic name
		String symbolicName = attrs.getValue(ManifestConstants.BUNDLE_SYMBOLICNAME.toString());
		// make sure there is no directive
		symbolicName = symbolicName.split(";")[0];

		String version = attrs.getValue(ManifestConstants.BUNDLE_VERSION.toString());
		return new DefaultNameVersion(symbolicName, version);
	}

	protected Path tryDownload(String uri, Path dir) throws IOException {
		// find mirror
		List<String> urlBases = null;
		String uriPrefix = null;
		uriPrefixes: for (String uriPref : mirrors.keySet()) {
			if (uri.startsWith(uriPref)) {
				if (mirrors.get(uriPref).size() > 0) {
					urlBases = mirrors.get(uriPref);
					uriPrefix = uriPref;
					break uriPrefixes;
				}
			}
		}
		if (urlBases == null)
			try {
				return download(new URL(uri), dir, null);
			} catch (FileNotFoundException e) {
				throw new FileNotFoundException("Cannot find " + uri);
			}

		// try to download
		for (String urlBase : urlBases) {
			String relativePath = uri.substring(uriPrefix.length());
			URL url = new URL(urlBase + relativePath);
			try {
				return download(url, dir, null);
			} catch (FileNotFoundException e) {
				logger.log(Level.WARNING, "Cannot download " + url + ", trying another mirror");
			}
		}
		throw new FileNotFoundException("Cannot find " + uri);
	}

//	protected String simplifyName(URL u) {
//	String	name = u.getPath().substring(u.getPath().lastIndexOf('/') + 1);
//		
//	}

	protected Path download(URL url, Path dir, String name) throws IOException {

		Path dest;
		if (name == null) {
			name = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
		}

		dest = dir.resolve(name);
		if (Files.exists(dest)) {
			logger.log(Level.TRACE, () -> "File " + dest + " already exists for " + url + ", not downloading again");
			return dest;
		}

		try (InputStream in = url.openStream()) {
			Files.copy(in, dest);
			logger.log(Level.DEBUG, () -> "Downloaded " + dest + " from " + url);
		}
		return dest;
	}

	public static void main(String[] args) {
		Path originBase = Paths.get("../output/origin").toAbsolutePath().normalize();
		Path factoryBase = Paths.get("../output/a2").toAbsolutePath().normalize();
		A2Factory factory = new A2Factory(originBase, factoryBase);

		Path descriptorsBase = Paths.get("../tp").toAbsolutePath().normalize();

//		factory.processEclipseArchive(
//				descriptorsBase.resolve("org.argeo.tp.eclipse.equinox").resolve("eclipse-equinox"));
//		factory.processEclipseArchive(descriptorsBase.resolve("org.argeo.tp.eclipse.rap").resolve("eclipse-rap"));
//		factory.processEclipseArchive(descriptorsBase.resolve("org.argeo.tp.eclipse.rcp").resolve("eclipse-rcp"));

		factory.processM2BasedDistributionUnit(descriptorsBase.resolve("org.argeo.tp").resolve("jetty"));
	}
}
