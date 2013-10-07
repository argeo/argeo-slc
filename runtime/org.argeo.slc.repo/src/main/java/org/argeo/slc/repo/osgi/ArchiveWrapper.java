package org.argeo.slc.repo.osgi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.ModuleSet;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.aether.ArtifactIdComparator;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.repo.OsgiFactory;
import org.argeo.slc.repo.RepoUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * Download a software distribution and generates the related OSGi bundles from
 * the jars, or import them directly if they are already OSGi bundles and don't
 * need further modification.
 */
public class ArchiveWrapper implements Runnable, ModuleSet, Distribution {
	private final static Log log = LogFactory.getLog(ArchiveWrapper.class);

	private OsgiFactory osgiFactory;
	private String version;

	private String uri;

	// jars to wrap as OSGi bundles
	private Map<String, BndWrapper> wrappers = new HashMap<String, BndWrapper>();

	// pattern of OSGi bundles to import
	private PathMatcher pathMatcher = new AntPathMatcher();
	private Map<String, String> includes = new HashMap<String, String>();
	private List<String> excludes = new ArrayList<String>();

	private Boolean mavenGroupIndexes = false;

	public void init() {
		if (version != null)
			for (BndWrapper wrapper : wrappers.values()) {
				if (wrapper.getVersion() == null)
					wrapper.setVersion(version);
			}
	}

	public void destroy() {

	}

	public String getDistributionId() {
		return uri;
	}

	public Iterator<? extends NameVersion> nameVersions() {
		return wrappers.values().iterator();
	}

	public void run() {
		if (mavenGroupIndexes && (version == null))
			throw new SlcException(
					"'mavenGroupIndexes' requires 'version' to be set");

		Map<String, Set<Artifact>> binaries = new HashMap<String, Set<Artifact>>();
		Map<String, Set<Artifact>> sources = new HashMap<String, Set<Artifact>>();

		Session distSession = null;
		Session javaSession = null;
		ZipInputStream zin = null;
		try {
			javaSession = osgiFactory.openJavaSession();
			distSession = osgiFactory.openDistSession();

			if (log.isDebugEnabled())
				log.debug("Wrapping " + uri);

			Node distNode = osgiFactory.getDist(distSession, uri);
			zin = new ZipInputStream(distNode.getNode(Node.JCR_CONTENT)
					.getProperty(Property.JCR_DATA).getBinary().getStream());

			ZipEntry zentry = null;
			entries: while ((zentry = zin.getNextEntry()) != null) {
				String name = zentry.getName();
				if (wrappers.containsKey(name)) {
					BndWrapper wrapper = (BndWrapper) wrappers.get(name);
					// we must copy since the stream is closed by BND
					byte[] sourceJarBytes = IOUtils.toByteArray(zin);
					Artifact artifact = wrapZipEntry(javaSession, zentry,
							sourceJarBytes, wrapper);
					addArtifactToIndex(binaries, wrapper.getGroupId(), artifact);
				} else {
					for (String wrapperKey : wrappers.keySet())
						if (pathMatcher.match(wrapperKey, name)) {
							// first matched is taken
							BndWrapper wrapper = (BndWrapper) wrappers
									.get(wrapperKey);
							// we must copy since the stream is closed by BND
							byte[] sourceJarBytes = IOUtils.toByteArray(zin);
							Artifact artifact = wrapZipEntry(javaSession,
									zentry, sourceJarBytes, wrapper);
							addArtifactToIndex(binaries, wrapper.getGroupId(),
									artifact);
							continue entries;
						} else {
							if (log.isTraceEnabled())
								log.trace(name + " not matched by "
										+ wrapperKey);
						}

					for (String exclude : excludes)
						if (pathMatcher.match(exclude, name))
							continue entries;

					for (String include : includes.keySet()) {
						if (pathMatcher.match(include, name)) {
							String groupId = includes.get(include);
							byte[] sourceJarBytes = IOUtils.toByteArray(zin);
							Artifact artifact = importZipEntry(javaSession,
									zentry, sourceJarBytes, groupId);
							if (artifact.getArtifactId().endsWith(".source"))
								addArtifactToIndex(sources, groupId, artifact);
							else
								addArtifactToIndex(binaries, groupId, artifact);
						}
					}
				}
			}

			// indexes
			if (mavenGroupIndexes && version != null) {
				for (String groupId : binaries.keySet()) {
					RepoUtils.writeGroupIndexes(javaSession, "/", groupId,
							version, binaries.get(groupId),
							sources.containsKey(groupId) ? sources.get(groupId)
									: null);
				}
			}

		} catch (Exception e) {
			throw new SlcException("Cannot wrap distribution " + uri, e);
		} finally {
			IOUtils.closeQuietly(zin);
			JcrUtils.logoutQuietly(distSession);
			JcrUtils.logoutQuietly(javaSession);
		}
	}

	protected Artifact wrapZipEntry(Session javaSession, ZipEntry zentry,
			byte[] sourceJarBytes, BndWrapper wrapper)
			throws RepositoryException {
		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		Node newJarNode;
		try {
			out = new ByteArrayOutputStream((int) zentry.getSize());
			in = new ByteArrayInputStream(sourceJarBytes);
			wrapper.wrapJar(in, out);

			Artifact artifact = wrapper.getArtifact();
			newJarNode = RepoUtils.copyBytesAsArtifact(
					javaSession.getRootNode(), artifact, out.toByteArray());
			osgiFactory.indexNode(newJarNode);
			newJarNode.getSession().save();
			if (log.isDebugEnabled())
				log.debug("Wrapped jar " + zentry.getName() + " to "
						+ newJarNode.getPath());
			return artifact;
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	protected Artifact importZipEntry(Session javaSession, ZipEntry zentry,
			byte[] sourceJarBytes, String groupId) throws RepositoryException {
		ByteArrayInputStream in = null;
		Node newJarNode;
		try {
			in = new ByteArrayInputStream(sourceJarBytes);
			NameVersion nameVersion = RepoUtils.readNameVersion(in);
			Artifact artifact = new DefaultArtifact(groupId,
					nameVersion.getName(), "jar", nameVersion.getVersion());
			newJarNode = RepoUtils.copyBytesAsArtifact(
					javaSession.getRootNode(), artifact, sourceJarBytes);
			osgiFactory.indexNode(newJarNode);
			newJarNode.getSession().save();
			if (log.isDebugEnabled())
				log.debug("Imported OSGi bundle " + zentry.getName() + " to "
						+ newJarNode.getPath());
			return artifact;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private void addArtifactToIndex(Map<String, Set<Artifact>> index,
			String groupId, Artifact artifact) {
		if (!index.containsKey(groupId))
			index.put(groupId,
					new TreeSet<Artifact>(new ArtifactIdComparator()));
		index.get(groupId).add(artifact);
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setWrappers(Map<String, BndWrapper> wrappers) {
		this.wrappers = wrappers;
	}

	public void setOsgiFactory(OsgiFactory osgiFactory) {
		this.osgiFactory = osgiFactory;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	public void setIncludes(Map<String, String> includes) {
		this.includes = includes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public void setMavenGroupIndexes(Boolean mavenGroupIndexes) {
		this.mavenGroupIndexes = mavenGroupIndexes;
	}

}
