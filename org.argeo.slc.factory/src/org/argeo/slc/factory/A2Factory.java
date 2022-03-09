package org.argeo.slc.factory;

import static java.lang.System.Logger.Level.DEBUG;
import static org.argeo.slc.ManifestConstants.BUNDLE_SYMBOLICNAME;
import static org.argeo.slc.ManifestConstants.BUNDLE_VERSION;
import static org.argeo.slc.ManifestConstants.EXPORT_PACKAGE;
import static org.argeo.slc.ManifestConstants.SLC_ORIGIN_M2;
import static org.argeo.slc.ManifestConstants.SLC_ORIGIN_M2_REPO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.argeo.slc.DefaultCategoryNameVersion;
import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.ManifestConstants;
import org.argeo.slc.NameVersion;
import org.argeo.slc.factory.m2.DefaultArtifact;
import org.argeo.slc.factory.m2.MavenConventionsUtils;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Jar;

/** The central class for A2 packaging. */
public class A2Factory {
	private final static Logger logger = System.getLogger(A2Factory.class.getName());

	private final static String COMMON_BND = "common.bnd";
	private final static String MERGE_BND = "merge.bnd";

	private Path originBase;
	private Path a2Base;

	/** key is URI prefix, value list of base URLs */
	private Map<String, List<String>> mirrors = new HashMap<String, List<String>>();

	public A2Factory(Path a2Base) {
		this.originBase = Paths.get(System.getProperty("user.home"), ".cache", "argeo/slc/origin");
		this.a2Base = a2Base;

		// TODO make it configurable
		List<String> eclipseMirrors = new ArrayList<>();
		eclipseMirrors.add("https://archive.eclipse.org/");
		eclipseMirrors.add("http://ftp-stud.hs-esslingen.de/Mirrors/eclipse/");
		eclipseMirrors.add("http://ftp.fau.de/eclipse/");

		mirrors.put("http://www.eclipse.org/downloads", eclipseMirrors);
	}

	public void processCategory(Path targetCategoryBase) {
		try {
			DirectoryStream<Path> bnds = Files.newDirectoryStream(targetCategoryBase,
					(p) -> p.getFileName().toString().endsWith(".bnd") && !p.getFileName().toString().equals(COMMON_BND)
							&& !p.getFileName().toString().equals(MERGE_BND));
			for (Path p : bnds) {
				processSingleM2ArtifactDistributionUnit(p);
			}

			DirectoryStream<Path> dus = Files.newDirectoryStream(targetCategoryBase, (p) -> Files.isDirectory(p));
			for (Path duDir : dus) {
				processM2BasedDistributionUnit(duDir);
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot process category " + targetCategoryBase, e);
		}
	}

	public void processSingleM2ArtifactDistributionUnit(Path bndFile) {
		try {
			String category = bndFile.getParent().getFileName().toString();
			Path targetCategoryBase = a2Base.resolve(category);
			Properties fileProps = new Properties();
			try (InputStream in = Files.newInputStream(bndFile)) {
				fileProps.load(in);
			}
			String repoStr = fileProps.containsKey(SLC_ORIGIN_M2_REPO.toString())
					? fileProps.getProperty(SLC_ORIGIN_M2_REPO.toString())
					: null;

			if (!fileProps.containsKey(BUNDLE_SYMBOLICNAME.toString())
					&& !fileProps.containsKey(ManifestConstants.SLC_ORIGIN_MANIFEST_NOT_MODIFIED.toString())) {
				// use file name as symbolic name
				String symbolicName = bndFile.getFileName().toString();
				symbolicName = symbolicName.substring(0, symbolicName.length() - ".bnd".length());
				fileProps.put(BUNDLE_SYMBOLICNAME.toString(), symbolicName);
			}

			String m2Coordinates = fileProps.getProperty(SLC_ORIGIN_M2.toString());
			if (m2Coordinates == null)
				throw new IllegalArgumentException("No M2 coordinates available for " + bndFile);
			DefaultArtifact artifact = new DefaultArtifact(m2Coordinates);
			URL url = MavenConventionsUtils.mavenRepoUrl(repoStr, artifact);
			Path downloaded = download(url, originBase, artifact.toM2Coordinates() + ".jar");

			Path targetBundleDir = processBndJar(downloaded, targetCategoryBase, fileProps, artifact);

			downloadAndProcessM2Sources(repoStr, artifact, targetBundleDir);

			createJar(targetBundleDir);
		} catch (Exception e) {
			throw new RuntimeException("Cannot process " + bndFile, e);
		}
	}

	public void processM2BasedDistributionUnit(Path duDir) {
		try {
			String category = duDir.getParent().getFileName().toString();
			Path targetCategoryBase = a2Base.resolve(category);

			// merge
			Path mergeBnd = duDir.resolve(MERGE_BND);
			if (Files.exists(mergeBnd)) {
				mergeM2Artifacts(mergeBnd);
				return;
			}

			Path commonBnd = duDir.resolve(COMMON_BND);
			Properties commonProps = new Properties();
			try (InputStream in = Files.newInputStream(commonBnd)) {
				commonProps.load(in);
			}

			String m2Version = commonProps.getProperty(SLC_ORIGIN_M2.toString());
			if (m2Version == null) {
				logger.log(Level.WARNING, "Ignoring " + duDir + " as it is not an M2-based distribution unit");
				return;// ignore, this is probably an Eclipse archive
			}
			if (!m2Version.startsWith(":")) {
				throw new IllegalStateException("Only the M2 version can be specified: " + m2Version);
			}
			m2Version = m2Version.substring(1);

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
//				String localLicense = fileProps.getProperty(BUNDLE_LICENSE.toString());
//				if (localLicense != null || artifact.getVersion() != null) {
//					fileProps.remove(BUNDLE_LICENSE.toString());
//					fileProps.put(SLC_ORIGIN_M2.toString(), artifact.getGroupId() + ":" + artifact.getArtifactId());
//					try (Writer writer = Files.newBufferedWriter(p)) {
//						for (Object key : fileProps.keySet()) {
//							String value = fileProps.getProperty(key.toString());
//							writer.write(key + ": " + value + '\n');
//						}
//						logger.log(DEBUG, () -> "Migrated  " + p);
//					}
//				}

				artifact.setVersion(m2Version);

				// prepare manifest entries
				Properties mergeProps = new Properties();
				mergeProps.putAll(commonProps);

				fileEntries: for (Object key : fileProps.keySet()) {
					if (ManifestConstants.SLC_ORIGIN_M2.toString().equals(key))
						continue fileEntries;
					String value = fileProps.getProperty(key.toString());
					Object previousValue = mergeProps.put(key.toString(), value);
					if (previousValue != null) {
						logger.log(Level.WARNING,
								commonBnd + ": " + key + " was " + previousValue + ", overridden with " + value);
					}
				}
				mergeProps.put(ManifestConstants.SLC_ORIGIN_M2.toString(), artifact.toM2Coordinates());
				if (!mergeProps.containsKey(BUNDLE_SYMBOLICNAME.toString())
						&& !mergeProps.containsKey(ManifestConstants.SLC_ORIGIN_MANIFEST_NOT_MODIFIED.toString())) {
					// use file name as symbolic name
					String symbolicName = p.getFileName().toString();
					symbolicName = symbolicName.substring(0, symbolicName.length() - ".bnd".length());
					mergeProps.put(BUNDLE_SYMBOLICNAME.toString(), symbolicName);
				}

				String repoStr = mergeProps.containsKey(SLC_ORIGIN_M2_REPO.toString())
						? mergeProps.getProperty(SLC_ORIGIN_M2_REPO.toString())
						: null;

				// download
				URL url = MavenConventionsUtils.mavenRepoUrl(repoStr, artifact);
				Path downloaded = download(url, originBase, artifact.toM2Coordinates() + ".jar");

				Path targetBundleDir = processBndJar(downloaded, targetCategoryBase, mergeProps, artifact);
//				logger.log(Level.DEBUG, () -> "Processed " + downloaded);

				// sources
				downloadAndProcessM2Sources(repoStr, artifact, targetBundleDir);

				createJar(targetBundleDir);
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot process " + duDir, e);
		}

	}

	protected void mergeM2Artifacts(Path mergeBnd) throws IOException {
		Path duDir = mergeBnd.getParent();
		String category = duDir.getParent().getFileName().toString();
		Path targetCategoryBase = a2Base.resolve(category);

		Properties mergeProps = new Properties();
		try (InputStream in = Files.newInputStream(mergeBnd)) {
			mergeProps.load(in);
		}
		String m2Version = mergeProps.getProperty(SLC_ORIGIN_M2.toString());
		if (m2Version == null) {
			logger.log(Level.WARNING, "Ignoring " + duDir + " as it is not an M2-based distribution unit");
			return;// ignore, this is probably an Eclipse archive
		}
		if (!m2Version.startsWith(":")) {
			throw new IllegalStateException("Only the M2 version can be specified: " + m2Version);
		}
		m2Version = m2Version.substring(1);

		String artifactsStr = mergeProps.getProperty(ManifestConstants.SLC_ORIGIN_M2_MERGE.toString());
		String repoStr = mergeProps.containsKey(SLC_ORIGIN_M2_REPO.toString())
				? mergeProps.getProperty(SLC_ORIGIN_M2_REPO.toString())
				: null;

		String bundleSymbolicName = mergeProps.getProperty(ManifestConstants.BUNDLE_SYMBOLICNAME.toString());
		DefaultCategoryNameVersion nameVersion = new DefaultArtifact(
				category + ":" + bundleSymbolicName + ":" + m2Version);
		Path targetBundleDir = targetCategoryBase.resolve(bundleSymbolicName + "." + nameVersion.getBranch());

		String[] artifacts = artifactsStr.split(",");
		artifacts: for (String str : artifacts) {
			String m2Coordinates = str.trim();
			if ("".equals(m2Coordinates))
				continue artifacts;
			DefaultArtifact artifact = new DefaultArtifact(m2Coordinates.trim());
			if (artifact.getVersion() == null)
				artifact.setVersion(m2Version);
			URL url = MavenConventionsUtils.mavenRepoUrl(repoStr, artifact);
			Path downloaded = download(url, originBase, artifact.toM2Coordinates() + ".jar");
			JarEntry entry;
			try (JarInputStream jarIn = new JarInputStream(Files.newInputStream(downloaded), false)) {
				entries: while ((entry = jarIn.getNextJarEntry()) != null) {
					if (entry.isDirectory())
						continue entries;
					if (entry.getName().endsWith(".RSA") || entry.getName().endsWith(".SF"))
						continue entries;
					if (entry.getName().startsWith("META-INF/versions/"))
						continue entries;
					Path target = targetBundleDir.resolve(entry.getName());
					Files.createDirectories(target.getParent());
					if (!Files.exists(target)) {
						Files.copy(jarIn, target);
					} else {
						if (entry.getName().startsWith("META-INF/services/")) {
							try (OutputStream out = Files.newOutputStream(target, StandardOpenOption.APPEND)) {
								jarIn.transferTo(out);
							}
						} else {
							throw new IllegalStateException("File " + target + " already exists");
						}
					}
					logger.log(Level.TRACE, () -> "Copied " + target);
				}

			}
			downloadAndProcessM2Sources(repoStr, artifact, targetBundleDir);
		}

		Map<String, String> entries = new TreeMap<>();
		try (Analyzer bndAnalyzer = new Analyzer()) {
			bndAnalyzer.setProperties(mergeProps);
			Jar jar = new Jar(targetBundleDir.toFile());
			bndAnalyzer.setJar(jar);
			Manifest manifest = bndAnalyzer.calcManifest();

			keys: for (Object key : manifest.getMainAttributes().keySet()) {
				Object value = manifest.getMainAttributes().get(key);

				switch (key.toString()) {
				case "Tool":
				case "Bnd-LastModified":
				case "Created-By":
					continue keys;
				}
				if ("Require-Capability".equals(key.toString())
						&& value.toString().equals("osgi.ee;filter:=\"(&(osgi.ee=JavaSE)(version=1.1))\""))
					continue keys;// hack for very old classes
				entries.put(key.toString(), value.toString());
				logger.log(DEBUG, () -> key + "=" + value);

			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot process " + mergeBnd, e);
		}

		Manifest manifest = new Manifest();
		Path manifestPath = targetBundleDir.resolve("META-INF/MANIFEST.MF");
		Files.createDirectories(manifestPath.getParent());
		for (String key : entries.keySet()) {
			String value = entries.get(key);
			manifest.getMainAttributes().putValue(key, value);
		}
		try (OutputStream out = Files.newOutputStream(manifestPath)) {
			manifest.write(out);
		}

		createJar(targetBundleDir);

	}

	protected void downloadAndProcessM2Sources(String repoStr, DefaultArtifact artifact, Path targetBundleDir)
			throws IOException {
		DefaultArtifact sourcesArtifact = new DefaultArtifact(artifact.toM2Coordinates(), "sources");
		URL sourcesUrl = MavenConventionsUtils.mavenRepoUrl(repoStr, sourcesArtifact);
		Path sourcesDownloaded = download(sourcesUrl, originBase, artifact.toM2Coordinates() + ".sources.jar");
		processM2SourceJar(sourcesDownloaded, targetBundleDir);
		logger.log(Level.DEBUG, () -> "Processed source " + sourcesDownloaded);

	}

	protected Path processBndJar(Path downloaded, Path targetCategoryBase, Properties fileProps,
			DefaultArtifact artifact) {

		try {
			Map<String, String> additionalEntries = new TreeMap<>();
			boolean doNotModify = Boolean.parseBoolean(fileProps
					.getOrDefault(ManifestConstants.SLC_ORIGIN_MANIFEST_NOT_MODIFIED.toString(), "false").toString());

			// we always force the symbolic name

			if (doNotModify) {
				fileEntries: for (Object key : fileProps.keySet()) {
					if (ManifestConstants.SLC_ORIGIN_M2.toString().equals(key))
						continue fileEntries;
					String value = fileProps.getProperty(key.toString());
					additionalEntries.put(key.toString(), value);
				}
			} else {
				if (artifact != null) {
					if (!fileProps.containsKey(BUNDLE_SYMBOLICNAME.toString())) {
						fileProps.put(BUNDLE_SYMBOLICNAME.toString(), artifact.getName());
					}
					if (!fileProps.containsKey(BUNDLE_VERSION.toString())) {
						fileProps.put(BUNDLE_VERSION.toString(), artifact.getVersion());
					}
				}

				if (!fileProps.containsKey(EXPORT_PACKAGE.toString())) {
					fileProps.put(EXPORT_PACKAGE.toString(),
							"*;version=\"" + fileProps.getProperty(BUNDLE_VERSION.toString()) + "\"");
				}
//				if (!fileProps.contains(IMPORT_PACKAGE.toString())) {
//					fileProps.put(IMPORT_PACKAGE.toString(), "*");
//				}

				try (Analyzer bndAnalyzer = new Analyzer()) {
					bndAnalyzer.setProperties(fileProps);
					Jar jar = new Jar(downloaded.toFile());
					bndAnalyzer.setJar(jar);
					Manifest manifest = bndAnalyzer.calcManifest();

					keys: for (Object key : manifest.getMainAttributes().keySet()) {
						Object value = manifest.getMainAttributes().get(key);

						switch (key.toString()) {
						case "Tool":
						case "Bnd-LastModified":
						case "Created-By":
							continue keys;
						}
						if ("Require-Capability".equals(key.toString())
								&& value.toString().equals("osgi.ee;filter:=\"(&(osgi.ee=JavaSE)(version=1.1))\""))
							continue keys;// hack for very old classes
						additionalEntries.put(key.toString(), value.toString());
						logger.log(DEBUG, () -> key + "=" + value);

					}
				}

//				try (Builder bndBuilder = new Builder()) {
//					Jar jar = new Jar(downloaded.toFile());
//					bndBuilder.addClasspath(jar);
//					Path targetBundleDir = targetCategoryBase.resolve(artifact.getName() + "." + artifact.getBranch());
//
//					Jar target = new Jar(targetBundleDir.toFile());
//					bndBuilder.setJar(target);
//					return targetBundleDir;
//				}
			}
			Path targetBundleDir = processBundleJar(downloaded, targetCategoryBase, additionalEntries);
			logger.log(Level.DEBUG, () -> "Processed " + downloaded);
			return targetBundleDir;
		} catch (Exception e) {
			throw new RuntimeException("Cannot BND process " + downloaded, e);
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
			Path targetCategoryBase = a2Base.resolve(category);
			Files.createDirectories(targetCategoryBase);
			// first delete all directories from previous builds
			for (Path dir : Files.newDirectoryStream(targetCategoryBase, (p) -> Files.isDirectory(p))) {
				deleteDirectory(dir);
			}

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

			FileSystem zipFs = FileSystems.newFileSystem(downloaded, (ClassLoader) null);

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
							if (file.getFileName().toString().contains(".source_")) {
								processEclipseSourceJar(file, targetCategoryBase);
								logger.log(Level.DEBUG, () -> "Processed source " + file);

							} else {
								processBundleJar(file, targetCategoryBase, new HashMap<>());
								logger.log(Level.DEBUG, () -> "Processed " + file);
							}
							break pathMatchers;
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});

			DirectoryStream<Path> dirs = Files.newDirectoryStream(targetCategoryBase, (p) -> Files.isDirectory(p));
			for (Path dir : dirs) {
				createJar(dir);
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot process " + duDir, e);
		}

	}

	protected Path processBundleJar(Path file, Path targetBase, Map<String, String> entries) throws IOException {
		DefaultNameVersion nameVersion;
		Path targetBundleDir;
		try (JarInputStream jarIn = new JarInputStream(Files.newInputStream(file), false)) {
			Manifest manifest = new Manifest(jarIn.getManifest());

			// remove problematic entries in MANIFEST
			manifest.getEntries().clear();
//			Set<String> entriesToDelete = new HashSet<>();
//			for (String key : manifest.getEntries().keySet()) {
////				logger.log(DEBUG, "## " + key);
//				Attributes attrs = manifest.getAttributes(key);
//				for (Object attrName : attrs.keySet()) {
////					logger.log(DEBUG, attrName + "=" + attrs.get(attrName));
//					if ("Specification-Version".equals(attrName.toString())
//							|| "Implementation-Version".equals(attrName.toString())) {
//						entriesToDelete.add(key);
//
//					}
//				}
//			}
//			for (String key : entriesToDelete) {
//				manifest.getEntries().remove(key);
//			}

			String symbolicNameFromEntries = entries.get(BUNDLE_SYMBOLICNAME.toString());
			String versionFromEntries = entries.get(BUNDLE_VERSION.toString());

			if (symbolicNameFromEntries != null && versionFromEntries != null) {
				nameVersion = new DefaultNameVersion(symbolicNameFromEntries, versionFromEntries);
			} else {
				nameVersion = nameVersionFromManifest(manifest);
				if (versionFromEntries != null && !nameVersion.getVersion().equals(versionFromEntries)) {
					logger.log(Level.WARNING, "Original version is " + nameVersion.getVersion()
							+ " while new version is " + versionFromEntries);
				}
				if (symbolicNameFromEntries != null) {
					// we always force our symbolic name
					nameVersion.setName(symbolicNameFromEntries);
				}
			}
			targetBundleDir = targetBase.resolve(nameVersion.getName() + "." + nameVersion.getBranch());

			// TODO make it less dangerous?
//			if (Files.exists(targetBundleDir)) {
//				deleteDirectory(targetBundleDir);
//			}

			// copy entries
			JarEntry entry;
			entries: while ((entry = jarIn.getNextJarEntry()) != null) {
				if (entry.isDirectory())
					continue entries;
				if (entry.getName().endsWith(".RSA") || entry.getName().endsWith(".SF"))
					continue entries;
				Path target = targetBundleDir.resolve(entry.getName());
				Files.createDirectories(target.getParent());
				Files.copy(jarIn, target);
				logger.log(Level.TRACE, () -> "Copied " + target);
			}

			// copy MANIFEST
			Path manifestPath = targetBundleDir.resolve("META-INF/MANIFEST.MF");
			Files.createDirectories(manifestPath.getParent());
			for (String key : entries.keySet()) {
				String value = entries.get(key);
				Object previousValue = manifest.getMainAttributes().putValue(key, value);
				if (previousValue != null && !previousValue.equals(value)) {
					if (ManifestConstants.IMPORT_PACKAGE.toString().equals(key)
							|| ManifestConstants.EXPORT_PACKAGE.toString().equals(key))
						logger.log(Level.WARNING, file.getFileName() + ": " + key + " was modified");

					else
						logger.log(Level.WARNING, file.getFileName() + ": " + key + " was " + previousValue
								+ ", overridden with " + value);
				}
			}
			try (OutputStream out = Files.newOutputStream(manifestPath)) {
				manifest.write(out);
			}
		}
		return targetBundleDir;
	}

	protected void processEclipseSourceJar(Path file, Path targetBase) throws IOException {
		try {
			Path targetBundleDir;
			try (JarInputStream jarIn = new JarInputStream(Files.newInputStream(file), false)) {
				Manifest manifest = jarIn.getManifest();

				String[] relatedBundle = manifest.getMainAttributes().getValue("Eclipse-SourceBundle").split(";");
				String version = relatedBundle[1].substring("version=\"".length());
				version = version.substring(0, version.length() - 1);
				NameVersion nameVersion = new DefaultNameVersion(relatedBundle[0], version);
				targetBundleDir = targetBase.resolve(nameVersion.getName() + "." + nameVersion.getBranch());

				Path targetSourceDir = targetBundleDir.resolve("OSGI-OPT/src");

				// TODO make it less dangerous?
				if (Files.exists(targetSourceDir)) {
//				deleteDirectory(targetSourceDir);
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
		} catch (IOException e) {
			throw new IllegalStateException("Cannot process " + file, e);
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

	protected DefaultNameVersion nameVersionFromManifest(Manifest manifest) {
		Attributes attrs = manifest.getMainAttributes();
		// symbolic name
		String symbolicName = attrs.getValue(ManifestConstants.BUNDLE_SYMBOLICNAME.toString());
		if (symbolicName == null)
			return null;
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

	protected Path createJar(Path bundleDir) throws IOException {
		Path jarPath = bundleDir.getParent().resolve(bundleDir.getFileName() + ".jar");
		Path manifestPath = bundleDir.resolve("META-INF/MANIFEST.MF");
		Manifest manifest;
		try (InputStream in = Files.newInputStream(manifestPath)) {
			manifest = new Manifest(in);
		}
		try (JarOutputStream jarOut = new JarOutputStream(Files.newOutputStream(jarPath), manifest)) {
			Files.walkFileTree(bundleDir, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (file.getFileName().toString().equals("MANIFEST.MF"))
						return super.visitFile(file, attrs);
					JarEntry entry = new JarEntry(bundleDir.relativize(file).toString());
					jarOut.putNextEntry(entry);
					Files.copy(file, jarOut);
					return super.visitFile(file, attrs);
				}

			});
		}
		deleteDirectory(bundleDir);
		return jarPath;
	}

	public static void main(String[] args) {
		Path factoryBase = Paths.get("../../output/a2").toAbsolutePath().normalize();
		A2Factory factory = new A2Factory(factoryBase);

		Path descriptorsBase = Paths.get("../tp").toAbsolutePath().normalize();

//		factory.processSingleM2ArtifactDistributionUnit(descriptorsBase.resolve("org.argeo.tp.apache").resolve("org.apache.xml.resolver.bnd"));
//		factory.processM2BasedDistributionUnit(descriptorsBase.resolve("org.argeo.tp.apache/apache-sshd"));
//		factory.processM2BasedDistributionUnit(descriptorsBase.resolve("org.argeo.tp.jetty/jetty"));
//		factory.processM2BasedDistributionUnit(descriptorsBase.resolve("org.argeo.tp.jetty/jetty-websocket"));
//		factory.processCategory(descriptorsBase.resolve("org.argeo.tp.eclipse.rcp"));
//		factory.processCategory(descriptorsBase.resolve("org.argeo.tp"));
//		factory.processCategory(descriptorsBase.resolve("org.argeo.tp.apache"));
//		factory.processCategory(descriptorsBase.resolve("org.argeo.tp.formats"));
		factory.processCategory(descriptorsBase.resolve("org.argeo.tp.gis"));
		System.exit(0);

		// Eclipse
		factory.processEclipseArchive(
				descriptorsBase.resolve("org.argeo.tp.eclipse.equinox").resolve("eclipse-equinox"));
		factory.processEclipseArchive(descriptorsBase.resolve("org.argeo.tp.eclipse.rap").resolve("eclipse-rap"));
		factory.processEclipseArchive(descriptorsBase.resolve("org.argeo.tp.eclipse.rcp").resolve("eclipse-rcp"));

		System.exit(0);

		// Maven
		factory.processCategory(descriptorsBase.resolve("org.argeo.tp.sdk"));
		factory.processCategory(descriptorsBase.resolve("org.argeo.tp"));
		factory.processCategory(descriptorsBase.resolve("org.argeo.tp.apache"));
		factory.processCategory(descriptorsBase.resolve("org.argeo.tp.jetty"));
		factory.processCategory(descriptorsBase.resolve("org.argeo.tp.jcr"));
	}
}
