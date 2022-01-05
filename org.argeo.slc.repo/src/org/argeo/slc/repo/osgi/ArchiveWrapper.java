package org.argeo.slc.repo.osgi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.CategoryNameVersion;
import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.ModuleSet;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.build.License;
import org.argeo.slc.repo.OsgiFactory;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.slc.repo.internal.springutil.AntPathMatcher;
import org.argeo.slc.repo.internal.springutil.PathMatcher;
import org.argeo.slc.repo.maven.ArtifactIdComparator;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

import aQute.bnd.osgi.Jar;

/**
 * Download a software distribution and generates the related OSGi bundles from
 * the jars, or import them directly if they are already OSGi bundles and don't
 * need further modification.
 */
public class ArchiveWrapper implements Runnable, ModuleSet, Distribution {
	private final static CmsLog log = CmsLog.getLog(ArchiveWrapper.class);

	private OsgiFactory osgiFactory;
	private String version;
	private License license;

	private String uri;

	/** Jars to wrap as OSGi bundles */
	private Map<String, BndWrapper> wrappers = new HashMap<String, BndWrapper>();

	private SourcesProvider sourcesProvider;

	// pattern of OSGi bundles to import
	private PathMatcher pathMatcher = new AntPathMatcher();
	private Map<String, String> includes = new HashMap<String, String>();
	private List<String> excludes = new ArrayList<String>();

	private Boolean mavenGroupIndexes = false;

	public void init() {
		for (BndWrapper wrapper : wrappers.values()) {
			wrapper.setFactory(this);
			if (version != null && wrapper.getVersion() == null)
				wrapper.setVersion(version);
			if (license != null && wrapper.getLicense() == null)
				wrapper.setLicense(license);
		}
	}

	public void destroy() {

	}

	public String getDistributionId() {
		return uri;
	}

	public String getVersion() {
		return version;
	}

	public License getLicense() {
		return license;
	}

	public String getUri() {
		return uri;
	}

	public Iterator<? extends NameVersion> nameVersions() {
		if (wrappers.size() > 0)
			return wrappers.values().iterator();
		else
			return osgiNameVersions();
	}

	@SuppressWarnings("resource")
	protected Iterator<? extends NameVersion> osgiNameVersions() {
		List<CategoryNameVersion> nvs = new ArrayList<CategoryNameVersion>();

		Session distSession = null;
		ZipInputStream zin = null;
		try {
			distSession = osgiFactory.openDistSession();

			Node distNode = osgiFactory.getDist(distSession, uri);
			zin = new ZipInputStream(
					distNode.getNode(Node.JCR_CONTENT).getProperty(Property.JCR_DATA).getBinary().getStream());

			ZipEntry zentry = null;
			entries: while ((zentry = zin.getNextEntry()) != null) {
				String name = zentry.getName();
				if (log.isTraceEnabled())
					log.trace("Zip entry " + name);
				for (String exclude : excludes)
					if (pathMatcher.match(exclude, name))
						continue entries;

				for (String include : includes.keySet()) {
					if (pathMatcher.match(include, name)) {
						String groupId = includes.get(include);
						JarInputStream jis = new JarInputStream(zin);
						if (jis.getManifest() == null) {
							log.warn("No MANIFEST in entry " + name + ", skipping...");
							continue entries;
						}
						NameVersion nv = RepoUtils.readNameVersion(jis.getManifest());
						if (nv != null) {
							if (nv.getName().endsWith(".source"))
								continue entries;
							CategoryNameVersion cnv = new ArchiveWrapperCNV(groupId, nv.getName(), nv.getVersion(),
									this);
							nvs.add(cnv);
							// no need to process further includes
							continue entries;
						}
					}
				}
			}
			return nvs.iterator();
		} catch (Exception e) {
			throw new SlcException("Cannot wrap distribution " + uri, e);
		} finally {
			IOUtils.closeQuietly(zin);
			JcrUtils.logoutQuietly(distSession);
		}
	}

	public void run() {
		if (mavenGroupIndexes && (version == null))
			throw new SlcException("'mavenGroupIndexes' requires 'version' to be set");

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
			boolean nothingWasDone = true;

			Node distNode = osgiFactory.getDist(distSession, uri);
			zin = new ZipInputStream(
					distNode.getNode(Node.JCR_CONTENT).getProperty(Property.JCR_DATA).getBinary().getStream());

			ZipEntry zentry = null;
			entries: while ((zentry = zin.getNextEntry()) != null) {
				String name = zentry.getName();

				// sources autodetect
				String baseName = FilenameUtils.getBaseName(name);
				if (baseName.endsWith("-sources")) {
					String bundle = baseName.substring(0, baseName.length() - "-sources".length());
					// log.debug(name + "," + baseName + ", " + bundle);
					String bundlePath = FilenameUtils.getPath(name) + bundle + ".jar";
					if (wrappers.containsKey(bundlePath)) {
						BndWrapper wrapper = wrappers.get(bundlePath);
						NameVersion bundleNv = new DefaultNameVersion(wrapper.getName(), wrapper.getVersion());
						byte[] pdeSource = RepoUtils.packageAsPdeSource(zin, bundleNv);
						Artifact sourcesArtifact = new DefaultArtifact(wrapper.getCategory(),
								wrapper.getName() + ".source", "jar", wrapper.getVersion());
						Node pdeSourceNode = RepoUtils.copyBytesAsArtifact(javaSession.getRootNode(), sourcesArtifact,
								pdeSource);
						osgiFactory.indexNode(pdeSourceNode);
						pdeSourceNode.getSession().save();
						if (log.isDebugEnabled())
							log.debug("Added sources " + sourcesArtifact + " for bundle " + wrapper.getArtifact()
									+ "from " + name + " in binary archive.");
					}

				}
				// else if (baseName.endsWith(".source")) {
				// }

				// binaries
				if (wrappers.containsKey(name)) {
					BndWrapper wrapper = (BndWrapper) wrappers.get(name);
					// we must copy since the stream is closed by BND
					byte[] origJarBytes = IOUtils.toByteArray(zin);
					Artifact artifact = wrapZipEntry(javaSession, zentry, origJarBytes, wrapper);
					nothingWasDone = false;
					addArtifactToIndex(binaries, wrapper.getGroupId(), artifact);
				} else {
					for (String wrapperKey : wrappers.keySet())
						if (pathMatcher.match(wrapperKey, name)) {
							// first matched is taken
							BndWrapper wrapper = (BndWrapper) wrappers.get(wrapperKey);
							// we must copy since the stream is closed by BND
							byte[] origJarBytes = IOUtils.toByteArray(zin);
							Artifact artifact = wrapZipEntry(javaSession, zentry, origJarBytes, wrapper);
							nothingWasDone = false;
							addArtifactToIndex(binaries, wrapper.getGroupId(), artifact);
							continue entries;
						} else {
							if (log.isTraceEnabled())
								log.trace(name + " not matched by " + wrapperKey);
						}

					for (String exclude : excludes)
						if (pathMatcher.match(exclude, name))
							continue entries;

					for (String include : includes.keySet()) {
						if (pathMatcher.match(include, name)) {
							String groupId = includes.get(include);
							byte[] origJarBytes = IOUtils.toByteArray(zin);
							Artifact artifact = importZipEntry(javaSession, zentry, origJarBytes, groupId);
							if (artifact == null) {
								log.warn("Skipped non identified " + zentry);
								continue entries;
							}
							nothingWasDone = false;
							if (artifact.getArtifactId().endsWith(".source"))
								addArtifactToIndex(sources, groupId, artifact);
							else
								addArtifactToIndex(binaries, groupId, artifact);
							// no need to process this entry further
							continue entries;
						}
					}
				}
			}

			// indexes
			if (mavenGroupIndexes && version != null) {
				for (String groupId : binaries.keySet()) {
					RepoUtils.writeGroupIndexes(javaSession, "/", groupId, version, binaries.get(groupId),
							sources.containsKey(groupId) ? sources.get(groupId) : null);
				}
			}

			if (nothingWasDone) {
				log.error("Nothing was done when wrapping " + uri + ". THE DISTRIBUTION IS INCONSISTENT.");
				// throw new SlcException("Nothing was done");
				// TODO Fail if not all wrappers matched
			}

		} catch (Exception e) {
			throw new SlcException("Cannot wrap distribution " + uri, e);
		} finally {
			IOUtils.closeQuietly(zin);
			JcrUtils.logoutQuietly(distSession);
			JcrUtils.logoutQuietly(javaSession);
		}
	}

	protected Artifact wrapZipEntry(Session javaSession, ZipEntry zentry, byte[] origJarBytes, BndWrapper wrapper)
			throws RepositoryException {
		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		Node newJarNode;
		Jar jar = null;
		try {
			out = new ByteArrayOutputStream((int) zentry.getSize());
			in = new ByteArrayInputStream(origJarBytes);
			wrapper.wrapJar(in, out);

			Artifact artifact = wrapper.getArtifact();
			newJarNode = RepoUtils.copyBytesAsArtifact(javaSession.getRootNode(), artifact, out.toByteArray());
			osgiFactory.indexNode(newJarNode);
			newJarNode.getSession().save();
			if (log.isDebugEnabled())
				log.debug("Wrapped jar " + zentry.getName() + " to " + newJarNode.getPath());

			if (sourcesProvider != null)
				addSource(javaSession, artifact, out.toByteArray());

			return artifact;
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			if (jar != null)
				jar.close();
		}
	}

	protected void addSource(Session javaSession, Artifact artifact, byte[] binaryJarBytes) {
		InputStream in = null;
		ByteArrayOutputStream out = null;
		Jar jar = null;
		try {
			in = new ByteArrayInputStream(binaryJarBytes);
			jar = new Jar(null, in);
			List<String> packages = jar.getPackages();

			out = new ByteArrayOutputStream();
			sourcesProvider.writeSources(packages, new ZipOutputStream(out));

			IOUtils.closeQuietly(in);
			in = new ByteArrayInputStream(out.toByteArray());
			byte[] sourcesJar = RepoUtils.packageAsPdeSource(in,
					new DefaultNameVersion(artifact.getArtifactId(), artifact.getVersion()));
			Artifact sourcesArtifact = new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId() + ".source",
					"jar", artifact.getVersion());
			Node sourcesJarNode = RepoUtils.copyBytesAsArtifact(javaSession.getRootNode(), sourcesArtifact, sourcesJar);
			sourcesJarNode.getSession().save();

			if (log.isDebugEnabled())
				log.debug("Added sources " + sourcesArtifact + " for bundle " + artifact + "from source provider "
						+ sourcesProvider);
		} catch (Exception e) {
			throw new SlcException("Cannot get sources for " + artifact, e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			if (jar != null)
				jar.close();
		}
	}

	protected Artifact importZipEntry(Session javaSession, ZipEntry zentry, byte[] binaryJarBytes, String groupId)
			throws RepositoryException {
		ByteArrayInputStream in = null;
		Node newJarNode;
		try {
			in = new ByteArrayInputStream(binaryJarBytes);
			NameVersion nameVersion = RepoUtils.readNameVersion(in);
			if (nameVersion == null) {
				log.warn("Cannot identify " + zentry.getName());
				return null;
			}
			Artifact artifact = new DefaultArtifact(groupId, nameVersion.getName(), "jar", nameVersion.getVersion());
			newJarNode = RepoUtils.copyBytesAsArtifact(javaSession.getRootNode(), artifact, binaryJarBytes);
			osgiFactory.indexNode(newJarNode);
			newJarNode.getSession().save();
			if (log.isDebugEnabled()) {
				log.debug(zentry.getName() + " => " + artifact);
			}

			if (sourcesProvider != null)
				addSource(javaSession, artifact, binaryJarBytes);

			return artifact;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private void addArtifactToIndex(Map<String, Set<Artifact>> index, String groupId, Artifact artifact) {
		if (!index.containsKey(groupId))
			index.put(groupId, new TreeSet<Artifact>(new ArtifactIdComparator()));
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

	public void setLicense(License license) {
		this.license = license;
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

	public void setSourcesProvider(SourcesProvider sourcesProvider) {
		this.sourcesProvider = sourcesProvider;
	}

	public Map<String, BndWrapper> getWrappers() {
		return wrappers;
	}

	public Map<String, String> getIncludes() {
		return includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

}
