package org.argeo.slc.repo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipInputStream;

import javax.jcr.Credentials;
import javax.jcr.GuestCredentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.api.cms.keyring.Keyring;
import org.argeo.cms.ArgeoNames;
import org.argeo.cms.ArgeoTypes;
import org.argeo.cms.jcr.CmsJcrUtils;
import org.argeo.jcr.JcrMonitor;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.repo.maven.ArtifactIdComparator;
import org.argeo.slc.repo.maven.MavenConventionsUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.osgi.framework.Constants;

/** Utilities around repo */
public class RepoUtils implements ArgeoNames, SlcNames {
	private final static CmsLog log = CmsLog.getLog(RepoUtils.class);

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
				if (!jarEntry.getName().equals("META-INF/MANIFEST.MF")) {
					JarEntry newJarEntry = new JarEntry(jarEntry.getName());
					jarOut.putNextEntry(newJarEntry);
					IOUtils.copy(jarIn, jarOut);
					jarIn.closeEntry();
					jarOut.closeEntry();
				}
			}
		} catch (IOException e) {
			throw new SlcException("Could not copy jar with MANIFEST "
					+ manifest.getMainAttributes(), e);
		} finally {
			if (!(in instanceof ZipInputStream))
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
		try {
			return readNameVersion(new FileInputStream(artifactFile));
		} catch (Exception e) {
			// probably not a jar, skipping
			if (log.isDebugEnabled()) {
				log.debug("Skipping " + artifactFile + " because of " + e);
				// e.printStackTrace();
			}
		}
		return null;
	}

	/** Read the OSGi {@link NameVersion} */
	public static NameVersion readNameVersion(InputStream in) {
		JarInputStream jarInputStream = null;
		try {
			jarInputStream = new JarInputStream(in);
			return readNameVersion(jarInputStream.getManifest());
		} catch (Exception e) {
			// probably not a jar, skipping
			if (log.isDebugEnabled()) {
				log.debug("Skipping because of " + e);
			}
		} finally {
			IOUtils.closeQuietly(jarInputStream);
		}
		return null;
	}

	/** Read the OSGi {@link NameVersion} */
	public static NameVersion readNameVersion(Manifest manifest) {
		DefaultNameVersion nameVersion = new DefaultNameVersion();
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
		} else if (node.isNodeType(SlcTypes.SLC_MODULE_COORDINATES)) {
			return new DefaultArtifact(node.getProperty(SLC_CATEGORY)
					.getString(), node.getProperty(SLC_NAME).getString(),
					"jar", node.getProperty(SLC_VERSION).getString());
		} else {
			throw new SlcException("Unsupported node type for " + node);
		}
	}

	/**
	 * The path to the PDE source related to this artifact (or artifact version
	 * base). There may or there may not be a node at this location (the
	 * returned path will typically be used to test whether PDE sources are
	 * attached to this artifact).
	 */
	public static String relatedPdeSourcePath(String artifactBasePath,
			Node artifactNode) throws RepositoryException {
		Artifact artifact = asArtifact(artifactNode);
		Artifact pdeSourceArtifact = new DefaultArtifact(artifact.getGroupId(),
				artifact.getArtifactId() + ".source", artifact.getExtension(),
				artifact.getVersion());
		return MavenConventionsUtils.artifactPath(artifactBasePath,
				pdeSourceArtifact);
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

	/** If a source return the base bundle name, does not change otherwise */
	public static String extractBundleNameFromSourceName(String sourceBundleName) {
		if (sourceBundleName.endsWith(".source"))
			return sourceBundleName.substring(0, sourceBundleName.length()
					- ".source".length());
		else
			return sourceBundleName;
	}

	/*
	 * SOFTWARE REPOSITORIES
	 */

	/** Retrieve repository based on information in the repo node */
	public static Repository getRepository(RepositoryFactory repositoryFactory,
			Keyring keyring, Node repoNode) {
		try {
			Repository repository;
			if (repoNode.isNodeType(ArgeoTypes.ARGEO_REMOTE_REPOSITORY)) {
				String uri = repoNode.getProperty(ARGEO_URI).getString();
				if (uri.startsWith("http")) {// http, https
					repository = CmsJcrUtils.getRepositoryByUri(
							repositoryFactory, uri);
				} else if (uri.startsWith("vm:")) {// alias
					repository = CmsJcrUtils.getRepositoryByUri(
							repositoryFactory, uri);
				} else {
					throw new SlcException("Unsupported repository uri " + uri);
				}
				return repository;
			} else {
				throw new SlcException("Unsupported node type " + repoNode);
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot connect to repository " + repoNode,
					e);
		}
	}

	/**
	 * Reads credentials from node, using keyring if there is a password. Can
	 * return null if no credentials needed (local repo) at all, but returns
	 * {@link GuestCredentials} if user id is 'anonymous' .
	 */
	public static Credentials getRepositoryCredentials(Keyring keyring,
			Node repoNode) {
		try {
			if (repoNode.isNodeType(ArgeoTypes.ARGEO_REMOTE_REPOSITORY)) {
				if (!repoNode.hasProperty(ARGEO_USER_ID))
					return null;

				String userId = repoNode.getProperty(ARGEO_USER_ID).getString();
				if (userId.equals("anonymous"))// FIXME hardcoded userId
					return new GuestCredentials();
				char[] password = keyring.getAsChars(repoNode.getPath() + '/'
						+ ARGEO_PASSWORD);
				Credentials credentials = new SimpleCredentials(userId,
						password);
				return credentials;
			} else {
				throw new SlcException("Unsupported node type " + repoNode);
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot connect to repository " + repoNode,
					e);
		}
	}

	/**
	 * Shortcut to retrieve a session given variable information: Handle the
	 * case where we only have an URI of the repository, that we want to connect
	 * as anonymous or the case of a identified connection to a local or remote
	 * repository.
	 * 
	 * Callers must close the session once it has been used
	 */
	public static Session getRemoteSession(RepositoryFactory repositoryFactory,
			Keyring keyring, Node repoNode, String uri, String workspaceName) {
		try {
			if (repoNode == null && uri == null)
				throw new SlcException(
						"At least one of repoNode and uri must be defined");
			Repository currRepo = null;
			Credentials credentials = null;
			// Anonymous URI only workspace
			if (repoNode == null)
				// Anonymous
				currRepo = CmsJcrUtils.getRepositoryByUri(repositoryFactory, uri);
			else {
				currRepo = RepoUtils.getRepository(repositoryFactory, keyring,
						repoNode);
				credentials = RepoUtils.getRepositoryCredentials(keyring,
						repoNode);
			}
			return currRepo.login(credentials, workspaceName);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot connect to workspace "
					+ workspaceName + " of repository " + repoNode
					+ " with URI " + uri, e);
		}
	}

	/**
	 * Shortcut to retrieve a session on a remote Jrc Repository from
	 * information stored in a local argeo node or from an URI: Handle the case
	 * where we only have an URI of the repository, that we want to connect as
	 * anonymous or the case of a identified connection to a local or remote
	 * repository.
	 * 
	 * Callers must close the session once it has been used
	 */
	public static Session getRemoteSession(RepositoryFactory repositoryFactory,
			Keyring keyring, Repository localRepository, String repoNodePath,
			String uri, String workspaceName) {
		Session localSession = null;
		Node repoNode = null;
		try {
			localSession = localRepository.login();
			if (repoNodePath != null && localSession.nodeExists(repoNodePath))
				repoNode = localSession.getNode(repoNodePath);

			return RepoUtils.getRemoteSession(repositoryFactory, keyring,
					repoNode, uri, workspaceName);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot log to workspace " + workspaceName
					+ " for repo defined in " + repoNodePath, e);
		} finally {
			JcrUtils.logoutQuietly(localSession);
		}
	}

	/**
	 * Write group indexes: 'binaries' lists all bundles and their versions,
	 * 'sources' list their sources, and 'sdk' aggregates both.
	 */
	public static void writeGroupIndexes(Session session,
			String artifactBasePath, String groupId, String version,
			Set<Artifact> binaries, Set<Artifact> sources) {
		try {
			Set<Artifact> indexes = new TreeSet<Artifact>(
					new ArtifactIdComparator());
			Artifact binariesArtifact = writeIndex(session, artifactBasePath,
					groupId, RepoConstants.BINARIES_ARTIFACT_ID, version,
					binaries);
			indexes.add(binariesArtifact);
			if (sources != null) {
				Artifact sourcesArtifact = writeIndex(session,
						artifactBasePath, groupId,
						RepoConstants.SOURCES_ARTIFACT_ID, version, sources);
				indexes.add(sourcesArtifact);
			}
			// sdk
			writeIndex(session, artifactBasePath, groupId,
					RepoConstants.SDK_ARTIFACT_ID, version, indexes);
			session.save();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot write indexes for group " + groupId,
					e);
		}
	}

	/** Write a group index. */
	private static Artifact writeIndex(Session session,
			String artifactBasePath, String groupId, String artifactId,
			String version, Set<Artifact> artifacts) throws RepositoryException {
		Artifact artifact = new DefaultArtifact(groupId, artifactId, "pom",
				version);
		String pom = MavenConventionsUtils.artifactsAsDependencyPom(artifact,
				artifacts, null);
		Node node = RepoUtils.copyBytesAsArtifact(
				session.getNode(artifactBasePath), artifact, pom.getBytes());
		addMavenChecksums(node);
		return artifact;
	}

	/** Add files containing the SHA-1 and MD5 checksums. */
	public static void addMavenChecksums(Node node) throws RepositoryException {
		// TODO optimize
		String sha = JcrUtils.checksumFile(node, "SHA-1");
		JcrUtils.copyBytesAsFile(node.getParent(), node.getName() + ".sha1",
				sha.getBytes());
		String md5 = JcrUtils.checksumFile(node, "MD5");
		JcrUtils.copyBytesAsFile(node.getParent(), node.getName() + ".md5",
				md5.getBytes());
	}

	/**
	 * Custom copy since the one in commons does not fit the needs when copying
	 * a workspace completely.
	 */
	public static void copy(Node fromNode, Node toNode) {
		copy(fromNode, toNode, null);
	}

	public static void copy(Node fromNode, Node toNode, JcrMonitor monitor) {
		try {
			String fromPath = fromNode.getPath();
			if (monitor != null)
				monitor.subTask("copying node :" + fromPath);
			if (log.isDebugEnabled())
				log.debug("copy node :" + fromPath);

			// FIXME : small hack to enable specific workspace copy
			if (fromNode.isNodeType("rep:ACL")
					|| fromNode.isNodeType("rep:system")) {
				if (log.isTraceEnabled())
					log.trace("node " + fromNode + " skipped");
				return;
			}

			// add mixins
			for (NodeType mixinType : fromNode.getMixinNodeTypes()) {
				toNode.addMixin(mixinType.getName());
			}

			// Double check
			for (NodeType mixinType : toNode.getMixinNodeTypes()) {
				if (log.isDebugEnabled())
					log.debug(mixinType.getName());
			}

			// process properties
			PropertyIterator pit = fromNode.getProperties();
			properties: while (pit.hasNext()) {
				Property fromProperty = pit.nextProperty();
				String propName = fromProperty.getName();
				try {
					String propertyName = fromProperty.getName();
					if (toNode.hasProperty(propertyName)
							&& toNode.getProperty(propertyName).getDefinition()
									.isProtected())
						continue properties;

					if (fromProperty.getDefinition().isProtected())
						continue properties;

					if (propertyName.equals("jcr:created")
							|| propertyName.equals("jcr:createdBy")
							|| propertyName.equals("jcr:lastModified")
							|| propertyName.equals("jcr:lastModifiedBy"))
						continue properties;

					if (fromProperty.isMultiple()) {
						toNode.setProperty(propertyName,
								fromProperty.getValues());
					} else {
						toNode.setProperty(propertyName,
								fromProperty.getValue());
					}
				} catch (RepositoryException e) {
					throw new SlcException("Cannot property " + propName, e);
				}
			}

			// recursively process children nodes
			NodeIterator nit = fromNode.getNodes();
			while (nit.hasNext()) {
				Node fromChild = nit.nextNode();
				Integer index = fromChild.getIndex();
				String nodeRelPath = fromChild.getName() + "[" + index + "]";
				Node toChild;
				if (toNode.hasNode(nodeRelPath))
					toChild = toNode.getNode(nodeRelPath);
				else
					toChild = toNode.addNode(fromChild.getName(), fromChild
							.getPrimaryNodeType().getName());
				copy(fromChild, toChild);
			}

			// update jcr:lastModified and jcr:lastModifiedBy in toNode in
			// case
			// they existed
			if (!toNode.getDefinition().isProtected()
					&& toNode.isNodeType(NodeType.MIX_LAST_MODIFIED))
				JcrUtils.updateLastModified(toNode);

			// Workaround to reduce session size: artifact is a saveable
			// unity
			if (toNode.isNodeType(SlcTypes.SLC_ARTIFACT))
				toNode.getSession().save();

			if (monitor != null)
				monitor.worked(1);

		} catch (RepositoryException e) {
			throw new SlcException("Cannot copy " + fromNode + " to " + toNode, e);
		}
	}

}
