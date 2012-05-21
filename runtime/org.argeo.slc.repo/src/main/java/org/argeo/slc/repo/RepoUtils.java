package org.argeo.slc.repo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.BasicNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.maven.MavenConventionsUtils;
import org.osgi.framework.Constants;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/** Utilities around repo */
public class RepoUtils implements SlcNames {
	private final static Log log = LogFactory.getLog(RepoUtils.class);

	/** Packages a regular sources jar as PDE source. */
	public static void packagesAsPdeSource(File sourceFile,
			NameVersion nameVersion, OutputStream out) throws IOException {
		if (isAlreadyPdeSource(sourceFile)) {
			FileInputStream in = new FileInputStream(sourceFile);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
		} else {
			String sourceSymbolicName = nameVersion.getName() + ".source";

			Manifest sourceManifest = null;
			sourceManifest = new Manifest();
			sourceManifest.getMainAttributes().put(
					Attributes.Name.MANIFEST_VERSION, "1.0");
			sourceManifest.getMainAttributes().putValue("Bundle-SymbolicName",
					sourceSymbolicName);
			sourceManifest.getMainAttributes().putValue("Bundle-Version",
					nameVersion.getVersion());
			sourceManifest.getMainAttributes().putValue(
					"Eclipse-SourceBundle",
					nameVersion.getName() + ";version="
							+ nameVersion.getVersion());
			copyJar(sourceFile, out, sourceManifest);
		}
	}

	public static byte[] packageAsPdeSource(InputStream sourceJar,
			NameVersion nameVersion) {
		String sourceSymbolicName = nameVersion.getName() + ".source";

		Manifest sourceManifest = null;
		sourceManifest = new Manifest();
		sourceManifest.getMainAttributes().put(
				Attributes.Name.MANIFEST_VERSION, "1.0");
		sourceManifest.getMainAttributes().putValue("Bundle-SymbolicName",
				sourceSymbolicName);
		sourceManifest.getMainAttributes().putValue("Bundle-Version",
				nameVersion.getVersion());
		sourceManifest.getMainAttributes().putValue("Eclipse-SourceBundle",
				nameVersion.getName() + ";version=" + nameVersion.getVersion());

		return modifyManifest(sourceJar, sourceManifest);
	}

	/**
	 * Check whether the file as already been packaged as PDE source, in order
	 * not to mess with Jar signing
	 */
	private static boolean isAlreadyPdeSource(File sourceFile) {
		JarInputStream jarInputStream = null;

		try {
			jarInputStream = new JarInputStream(new FileInputStream(sourceFile));

			Manifest manifest = jarInputStream.getManifest();
			Iterator<?> it = manifest.getMainAttributes().keySet().iterator();
			boolean res = false;
			// containsKey() does not work, iterating...
			while (it.hasNext())
				if (it.next().toString().equals("Eclipse-SourceBundle")) {
					res = true;
					break;
				}
			// boolean res = manifest.getMainAttributes().get(
			// "Eclipse-SourceBundle") != null;
			if (res)
				log.info(sourceFile + " is already a PDE source");
			return res;
		} catch (Exception e) {
			// probably not a jar, skipping
			if (log.isDebugEnabled())
				log.debug("Skipping " + sourceFile + " because of "
						+ e.getMessage());
			return false;
		} finally {
			IOUtils.closeQuietly(jarInputStream);
		}
	}

	/**
	 * Copy a jar, replacing its manifest with the provided one
	 * 
	 * @param manifest
	 *            can be null
	 */
	private static void copyJar(File source, OutputStream out, Manifest manifest)
			throws IOException {
		JarFile sourceJar = null;
		JarOutputStream output = null;
		try {
			output = manifest != null ? new JarOutputStream(out, manifest)
					: new JarOutputStream(out);
			sourceJar = new JarFile(source);

			entries: for (Enumeration<?> entries = sourceJar.entries(); entries
					.hasMoreElements();) {
				JarEntry entry = (JarEntry) entries.nextElement();
				if (manifest != null
						&& entry.getName().equals("META-INF/MANIFEST.MF"))
					continue entries;

				InputStream entryStream = sourceJar.getInputStream(entry);
				JarEntry newEntry = new JarEntry(entry.getName());
				// newEntry.setMethod(JarEntry.DEFLATED);
				output.putNextEntry(newEntry);
				IOUtils.copy(entryStream, output);
			}
		} finally {
			IOUtils.closeQuietly(output);
			try {
				if (sourceJar != null)
					sourceJar.close();
			} catch (IOException e) {
				// silent
			}
		}
	}

	/** Copy a jar changing onlythe manifest */
	public static void copyJar(InputStream in, OutputStream out,
			Manifest manifest) {
		JarInputStream jarIn = null;
		JarOutputStream jarOut = null;
		try {
			jarIn = new JarInputStream(in);
			jarOut = new JarOutputStream(out, manifest);
			JarEntry jarEntry = null;
			while ((jarEntry = jarIn.getNextJarEntry()) != null) {
				jarOut.putNextEntry(jarEntry);
				IOUtils.copy(jarIn, jarOut);
				jarIn.closeEntry();
				jarOut.closeEntry();
			}
		} catch (IOException e) {
			throw new SlcException("Could not copy jar with MANIFEST "
					+ manifest.getMainAttributes(), e);
		} finally {
			IOUtils.closeQuietly(jarIn);
			IOUtils.closeQuietly(jarOut);
		}
	}

	/** Reads a jar file, modify its manifest */
	public static byte[] modifyManifest(InputStream in, Manifest manifest) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(200 * 1024);
		try {
			copyJar(in, out, manifest);
			return out.toByteArray();
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/** Read the OSGi {@link NameVersion} */
	public static NameVersion readNameVersion(Artifact artifact) {
		File artifactFile = artifact.getFile();
		if (artifact.getExtension().equals("pom")) {
			// hack to process jars which weirdly appear as POMs
			File jarFile = new File(artifactFile.getParentFile(),
					FilenameUtils.getBaseName(artifactFile.getPath()) + ".jar");
			if (jarFile.exists()) {
				log.warn("Use " + jarFile + " instead of " + artifactFile
						+ " for " + artifact);
				artifactFile = jarFile;
			}
		}
		return readNameVersion(artifactFile);
	}

	/** Read the OSGi {@link NameVersion} */
	public static NameVersion readNameVersion(File artifactFile) {
		JarInputStream jarInputStream = null;
		try {
			jarInputStream = new JarInputStream(new FileInputStream(
					artifactFile));
			return readNameVersion(jarInputStream.getManifest());
		} catch (Exception e) {
			// probably not a jar, skipping
			if (log.isDebugEnabled()) {
				log.debug("Skipping " + artifactFile + " because of " + e);
				// e.printStackTrace();
			}
		} finally {
			IOUtils.closeQuietly(jarInputStream);
		}
		return null;
	}

	/** Read the OSGi {@link NameVersion} */
	public static NameVersion readNameVersion(Manifest manifest) {
		BasicNameVersion nameVersion = new BasicNameVersion();
		nameVersion.setName(manifest.getMainAttributes().getValue(
				Constants.BUNDLE_SYMBOLICNAME));

		// Skip additional specs such as
		// ; singleton:=true
		if (nameVersion.getName().indexOf(';') > -1) {
			nameVersion
					.setName(new StringTokenizer(nameVersion.getName(), " ;")
							.nextToken());
		}

		nameVersion.setVersion(manifest.getMainAttributes().getValue(
				Constants.BUNDLE_VERSION));

		return nameVersion;
	}

	/*
	 * DATA MODEL
	 */
	/** The artifact described by this node */
	public static Artifact asArtifact(Node node) throws RepositoryException {
		if (node.isNodeType(SlcTypes.SLC_ARTIFACT_VERSION_BASE)) {
			// FIXME update data model to store packaging at this level
			String extension = "jar";
			return new DefaultArtifact(node.getProperty(SLC_GROUP_ID)
					.getString(),
					node.getProperty(SLC_ARTIFACT_ID).getString(), extension,
					node.getProperty(SLC_ARTIFACT_VERSION).getString());
		} else if (node.isNodeType(SlcTypes.SLC_ARTIFACT)) {
			return new DefaultArtifact(node.getProperty(SLC_GROUP_ID)
					.getString(),
					node.getProperty(SLC_ARTIFACT_ID).getString(), node
							.getProperty(SLC_ARTIFACT_CLASSIFIER).getString(),
					node.getProperty(SLC_ARTIFACT_EXTENSION).getString(), node
							.getProperty(SLC_ARTIFACT_VERSION).getString());
		} else {
			throw new SlcException("Unsupported node type for " + node);
		}
	}

	/**
	 * Copy this bytes array as an artifact, relative to the root of the
	 * repository (typically the workspace root node)
	 */
	public static Node copyBytesAsArtifact(Node artifactsBase,
			Artifact artifact, byte[] bytes) throws RepositoryException {
		String parentPath = MavenConventionsUtils.artifactParentPath(
				artifactsBase.getPath(), artifact);
		Node folderNode = JcrUtils.mkfolders(artifactsBase.getSession(),
				parentPath);
		return JcrUtils.copyBytesAsFile(folderNode,
				MavenConventionsUtils.artifactFileName(artifact), bytes);
	}

	private RepoUtils() {
	}
}
