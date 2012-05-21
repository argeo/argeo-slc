package org.argeo.slc.repo.osgi;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.ArtifactIndexer;
import org.argeo.slc.repo.JarFileIndexer;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.slc.repo.maven.MavenConventionsUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Import all bundles in a zip file (typically an Eclipse distribution) into the
 * workspace.
 */
public class ImportBundlesZip implements Runnable {
	private final static Log log = LogFactory.getLog(ImportBundlesZip.class);
	private Repository repository;
	private String workspace;
	private String groupId;
	private String artifactBasePath = "/";

	private ArtifactIndexer artifactIndexer = new ArtifactIndexer();
	private JarFileIndexer jarFileIndexer = new JarFileIndexer();

	private String zipFile;

	private List<String> excludedBundles = new ArrayList<String>();

	public void run() {
		ZipInputStream zipIn = null;
		JarInputStream jarIn = null;
		Session session = null;
		try {
			URL url = new URL(zipFile);
			session = repository.login(workspace);

			// clear
			String groupPath = MavenConventionsUtils.groupPath(
					artifactBasePath, groupId);
			if (session.itemExists(groupPath)) {
				session.getNode(groupPath).remove();
				session.save();
				if (log.isDebugEnabled())
					log.debug("Cleared " + groupPath);
			}

			zipIn = new ZipInputStream(url.openStream());
			ZipEntry zipEntry = null;
			entries: while ((zipEntry = zipIn.getNextEntry()) != null) {
				String entryName = zipEntry.getName();
				if (!entryName.endsWith(".jar")
						|| entryName.contains("feature"))
					continue entries;// skip
				byte[] jarBytes = IOUtils.toByteArray(zipIn);
				zipIn.closeEntry();
				jarIn = new JarInputStream(new ByteArrayInputStream(jarBytes));
				Manifest manifest = jarIn.getManifest();
				IOUtils.closeQuietly(jarIn);
				if (manifest == null) {
					log.warn(entryName + " has no MANIFEST");
					continue entries;
				}
				NameVersion nv = RepoUtils.readNameVersion(manifest);

				// skip excluded bundles and their sources
				if (excludedBundles.contains(extractBundleNameFromSourceName(nv
						.getName())))
					continue entries;

				Artifact artifact = new DefaultArtifact(groupId, nv.getName(),
						"jar", nv.getVersion());
				Node artifactNode = RepoUtils.copyBytesAsArtifact(
						session.getNode(artifactBasePath), artifact, jarBytes);
				jarBytes = null;// superstition, in order to free memory

				// indexes
				artifactIndexer.index(artifactNode);
				jarFileIndexer.index(artifactNode);
				session.save();
				if (log.isDebugEnabled())
					log.debug("Imported " + entryName + " to " + artifactNode);
			}
		} catch (Exception e) {
			throw new SlcException("Cannot import zip " + zipFile + " to "
					+ workspace, e);
		} finally {
			IOUtils.closeQuietly(zipIn);
			IOUtils.closeQuietly(jarIn);
			JcrUtils.logoutQuietly(session);
		}

	}

	/** If a source return the base bundle name, does not change otherwise */
	private String extractBundleNameFromSourceName(String sourceBundleName) {
		if (sourceBundleName.endsWith(".source"))
			return sourceBundleName.substring(0, sourceBundleName.length()
					- ".source".length());
		else
			return sourceBundleName;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setArtifactBasePath(String artifactBasePath) {
		this.artifactBasePath = artifactBasePath;
	}

	public void setZipFile(String zipFile) {
		this.zipFile = zipFile;
	}

	public void setExcludedBundles(List<String> excludedBundles) {
		this.excludedBundles = excludedBundles;
	}

}
