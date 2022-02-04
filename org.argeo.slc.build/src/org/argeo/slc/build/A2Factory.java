package org.argeo.slc.build;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
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

public class A2Factory {
	private final static Logger logger = System.getLogger(A2Factory.class.getName());

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

	public void processEclipseArchive(Path duDir) {
		try {
			String category = duDir.getParent().getFileName().toString();
			Path targetCategoryBase = factoryBase.resolve(category);
			Files.createDirectories(targetCategoryBase);
			Files.createDirectories(originBase);

			Path commonBnd = duDir.resolve("common.bnd");
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
								processBundleJar(file, targetCategoryBase);
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

	protected void processBundleJar(Path file, Path targetBase) throws IOException {
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
			try (OutputStream out = Files.newOutputStream(manifestPath)) {
				manifest.write(out);
			}
		}

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
				return download(uri, dir, null);
			} catch (FileNotFoundException e) {
				throw new FileNotFoundException("Cannot find " + uri);
			}

		// try to download
		for (String urlBase : urlBases) {
			String relativePath = uri.substring(uriPrefix.length());
			String url = urlBase + relativePath;
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

	public Path download(String url, Path dir, String name) throws IOException {

		Path dest;
		URL u = new URL(url);
		if (name == null) {
			name = u.getPath().substring(u.getPath().lastIndexOf('/') + 1);
		}

		dest = dir.resolve(name);
		if (Files.exists(dest)) {
			logger.log(Level.DEBUG, () -> "File " + dest + " already exists for " + url + ", not downloading again.");
			return dest;
		}

		try (InputStream in = u.openStream()) {
			Files.copy(in, dest);
		}
		return dest;
	}

	public static void main(String[] args) {
		Path originBase = Paths.get("../output/origin").toAbsolutePath();
		Path factoryBase = Paths.get("../output/a2").toAbsolutePath();
		A2Factory factory = new A2Factory(originBase, factoryBase);

		Path descriptorsBase = Paths.get("../tp").toAbsolutePath();

		factory.processEclipseArchive(
				descriptorsBase.resolve("org.argeo.tp.eclipse.equinox").resolve("eclipse-equinox"));
		factory.processEclipseArchive(descriptorsBase.resolve("org.argeo.tp.eclipse.rap").resolve("eclipse-rap"));
		factory.processEclipseArchive(descriptorsBase.resolve("org.argeo.tp.eclipse.rcp").resolve("eclipse-rcp"));
	}
}
