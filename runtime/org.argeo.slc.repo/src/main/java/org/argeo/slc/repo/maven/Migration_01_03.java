package org.argeo.slc.repo.maven;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.ArtifactIndexer;
import org.argeo.slc.repo.JarFileIndexer;
import org.argeo.slc.repo.RepoUtils;
import org.osgi.framework.Constants;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Migrate the distribution from 1.2 to 1.4 by cleaning naming and dependencies.
 * The dependency to the SpringSource Enterprise Bundle repository is removed as
 * well as their naming conventions. All third party are move to org.argeo.tp
 * group IDs. Maven dependency for Eclipse artifacts don't use version ranges
 * anymore. Verison constraints on javax.* packages are removed (since they lead
 * to "use package conflicts" when Eclipse and Spring Security are used
 * together).
 */
public class Migration_01_03 implements Runnable, SlcNames {
	final String SPRING_SOURCE_PREFIX = "com.springsource";
	private final static Log log = LogFactory.getLog(Migration_01_03.class);

	private Repository repository;
	private String sourceWorkspace;
	private String targetWorkspace;

	private Session origSession;
	private Session targetSession;

	private List<String> systemPackages;

	private String artifactBasePath = "/";

	private ArtifactIndexer artifactIndexer = new ArtifactIndexer();
	private JarFileIndexer jarFileIndexer = new JarFileIndexer();

	public void init() throws RepositoryException {
		origSession = JcrUtils.loginOrCreateWorkspace(repository,
				sourceWorkspace);
		targetSession = JcrUtils.loginOrCreateWorkspace(repository,
				targetWorkspace);

		// works only in OSGi!!
		systemPackages = Arrays.asList(System.getProperty(
				"org.osgi.framework.system.packages").split(","));
	}

	public void destroy() {
		JcrUtils.logoutQuietly(origSession);
		JcrUtils.logoutQuietly(targetSession);
	}

	public void run() {

		try {
			NodeIterator origArtifacts = listArtifactVersions(origSession);

			// clear target
			NodeIterator nit = targetSession.getNode(artifactBasePath)
					.getNodes();
			while (nit.hasNext()) {
				Node node = nit.nextNode();
				if (node.isNodeType(NodeType.NT_FOLDER)
						|| node.isNodeType(NodeType.NT_UNSTRUCTURED))
					node.remove();
			}
			targetSession.save();

			// process
			while (origArtifacts.hasNext()) {
				Node origArtifactNode = origArtifacts.nextNode();
				if (log.isTraceEnabled())
					log.trace(origArtifactNode);

				processOrigArtifactVersion(origArtifactNode);
			}
		} catch (Exception e) {
			throw new SlcException("Cannot perform v1.3 migration from "
					+ sourceWorkspace + " to " + targetWorkspace, e);
		} finally {
			JcrUtils.discardQuietly(targetSession);
		}
	}

	protected void processOrigArtifactVersion(Node origArtifactNode)
			throws RepositoryException, IOException {
		Artifact origArtifact = RepoUtils.asArtifact(origArtifactNode);
		String origJarNodeName = MavenConventionsUtils
				.artifactFileName(origArtifact);
		if (!origArtifactNode.hasNode(origJarNodeName))
			throw new SlcException("Cannot find jar node for "
					+ origArtifactNode);
		Node origJarNode = origArtifactNode.getNode(origJarNodeName);

		// read MANIFEST
		Binary manifestBinary = origJarNode.getProperty(SLC_MANIFEST)
				.getBinary();
		Manifest origManifest = new Manifest(manifestBinary.getStream());
		JcrUtils.closeQuietly(manifestBinary);

		Boolean manifestModified = false;
		Manifest targetManifest = new Manifest(origManifest);

		// transform symbolic name
		String origSymbolicName = origManifest.getMainAttributes().getValue(
				Constants.BUNDLE_SYMBOLICNAME);
		final String targetSymbolicName;
		if (origSymbolicName.startsWith(SPRING_SOURCE_PREFIX)
				&& !origSymbolicName.equals(SPRING_SOURCE_PREFIX + ".json")) {
			targetSymbolicName = origSymbolicName
					.substring(SPRING_SOURCE_PREFIX.length() + 1);
			if (log.isDebugEnabled())
				log.debug(Constants.BUNDLE_SYMBOLICNAME + " to "
						+ targetSymbolicName + " \t\tfrom " + origSymbolicName);
			targetManifest.getMainAttributes().putValue(
					Constants.BUNDLE_SYMBOLICNAME, targetSymbolicName);
			manifestModified = true;
		} else {
			targetSymbolicName = origSymbolicName;
		}

		// check fragment host
		if (origManifest.getMainAttributes().containsKey(
				new Name(Constants.FRAGMENT_HOST))) {
			String origFragmentHost = origManifest.getMainAttributes()
					.getValue(Constants.FRAGMENT_HOST);
			if (origFragmentHost.startsWith(SPRING_SOURCE_PREFIX)
					&& !origFragmentHost.equals(SPRING_SOURCE_PREFIX + ".json")) {
				String targetFragmentHost = origFragmentHost
						.substring(SPRING_SOURCE_PREFIX.length() + 1);
				if (log.isDebugEnabled())
					log.debug(Constants.FRAGMENT_HOST + " to "
							+ targetFragmentHost + " from " + origFragmentHost);
				targetManifest.getMainAttributes().putValue(
						Constants.FRAGMENT_HOST, targetFragmentHost);
				manifestModified = true;
			}
		}

		// we assume there is no Require-Bundle in com.springsource.* bundles

		// javax with versions
		StringBuffer targetImportPackages = new StringBuffer("");
		NodeIterator origImportPackages = origJarNode.getNodes(SLC_
				+ Constants.IMPORT_PACKAGE);
		Boolean importPackagesModified = false;
		while (origImportPackages.hasNext()) {
			Node importPackage = origImportPackages.nextNode();
			String pkg = importPackage.getProperty(SLC_NAME).getString();
			targetImportPackages.append(pkg);
			if (importPackage.hasProperty(SLC_VERSION)) {
				String sourceVersion = importPackage.getProperty(SLC_VERSION)
						.getString();
				String targetVersion = sourceVersion;
				if (systemPackages.contains(pkg)) {
					if (!(sourceVersion.trim().equals("0") || sourceVersion
							.trim().equals("0.0.0"))) {
						targetVersion = "0";
						importPackagesModified = true;
						if (log.isDebugEnabled())
							log.debug(origSymbolicName
									+ ": Nullify version of " + pkg + " from "
									+ sourceVersion);
					}
				}
				targetImportPackages.append(";version=\"")
						.append(targetVersion).append("\"");
			}
			if (importPackage.hasProperty(SLC_OPTIONAL)) {
				Boolean optional = importPackage.getProperty(SLC_OPTIONAL)
						.getBoolean();
				if (optional)
					targetImportPackages.append(";resolution:=\"optional\"");

			}
			if (origImportPackages.hasNext())
				targetImportPackages.append(",");
		}

		if (importPackagesModified) {
			targetManifest.getMainAttributes().putValue(
					Constants.IMPORT_PACKAGE, targetImportPackages.toString());
			manifestModified = true;
		}

		if (!manifestModified && log.isTraceEnabled()) {
			log.trace("MANIFEST of " + origSymbolicName + " was not modified");
		}

		// target coordinates
		final String targetGroupId;
		if (origArtifact.getGroupId().startsWith("org.eclipse"))
			targetGroupId = "org.argeo.tp.eclipse";
		else
			targetGroupId = "org.argeo.tp";

		String targetArtifactId = targetSymbolicName.split(";")[0];
		Artifact targetArtifact = new DefaultArtifact(targetGroupId,
				targetArtifactId, "jar", origArtifact.getVersion());
		String targetParentPath = MavenConventionsUtils.artifactParentPath(
				artifactBasePath, targetArtifact);
		String targetFileName = MavenConventionsUtils
				.artifactFileName(targetArtifact);
		String targetJarPath = targetParentPath + '/' + targetFileName;

		// copy
		Node targetParentNode = JcrUtils.mkfolders(targetSession,
				targetParentPath);
		targetSession.save();
		if (manifestModified) {
			Binary origBinary = origJarNode.getNode(Node.JCR_CONTENT)
					.getProperty(Property.JCR_DATA).getBinary();
			byte[] targetJarBytes = RepoUtils.modifyManifest(
					origBinary.getStream(), targetManifest);
			JcrUtils.copyBytesAsFile(targetParentNode, targetFileName,
					targetJarBytes);
			JcrUtils.closeQuietly(origBinary);
		} else {// just copy
			targetSession.getWorkspace().copy(sourceWorkspace,
					origJarNode.getPath(), targetJarPath);
		}
		targetSession.save();

		// reindex
		Node targetJarNode = targetSession.getNode(targetJarPath);
		artifactIndexer.index(targetJarNode);
		jarFileIndexer.index(targetJarNode);

		targetSession.save();

		// sources
		Artifact origSourceArtifact = new DefaultArtifact(
				origArtifact.getGroupId(), origArtifact.getArtifactId()
						+ ".source", "jar", origArtifact.getVersion());
		String origSourcePath = MavenConventionsUtils.artifactPath(
				artifactBasePath, origSourceArtifact);
		if (origSession.itemExists(origSourcePath)) {
			Node origSourceJarNode = origSession.getNode(origSourcePath);

			Artifact targetSourceArtifact = new DefaultArtifact(targetGroupId,
					targetArtifactId + ".source", "jar",
					origArtifact.getVersion());
			String targetSourceParentPath = MavenConventionsUtils
					.artifactParentPath(artifactBasePath, targetSourceArtifact);
			String targetSourceFileName = MavenConventionsUtils
					.artifactFileName(targetSourceArtifact);
			String targetSourceJarPath = targetSourceParentPath + '/'
					+ targetSourceFileName;

			Node targetSourceParentNode = JcrUtils.mkfolders(targetSession,
					targetSourceParentPath);
			targetSession.save();

			if (!targetSymbolicName.equals(origSymbolicName)) {
				Binary origBinary = origSourceJarNode.getNode(Node.JCR_CONTENT)
						.getProperty(Property.JCR_DATA).getBinary();
				NameVersion targetNameVersion = RepoUtils
						.readNameVersion(targetManifest);
				byte[] targetJarBytes = RepoUtils.packageAsPdeSource(
						origBinary.getStream(), targetNameVersion);
				JcrUtils.copyBytesAsFile(targetSourceParentNode,
						targetSourceFileName, targetJarBytes);
				JcrUtils.closeQuietly(origBinary);
			} else {// just copy
				targetSession.getWorkspace().copy(sourceWorkspace,
						origSourceJarNode.getPath(), targetSourceJarPath);
			}
			targetSession.save();

			// reindex
			Node targetSourceJarNode = targetSession
					.getNode(targetSourceJarPath);
			artifactIndexer.index(targetSourceJarNode);
			jarFileIndexer.index(targetSourceJarNode);

			targetSession.save();
		}
	}

	/*
	 * UTILITIES
	 */

	static NodeIterator listArtifactVersions(Session session)
			throws RepositoryException {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		QueryObjectModelFactory factory = queryManager.getQOMFactory();

		final String artifactVersionsSelector = "artifactVersions";
		Selector source = factory.selector(SlcTypes.SLC_ARTIFACT_VERSION_BASE,
				artifactVersionsSelector);

		Ordering orderByArtifactId = factory.ascending(factory.propertyValue(
				artifactVersionsSelector, SlcNames.SLC_ARTIFACT_ID));
		Ordering[] orderings = { orderByArtifactId };

		QueryObjectModel query = factory.createQuery(source, null, orderings,
				null);

		QueryResult result = query.execute();
		return result.getNodes();
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setSourceWorkspace(String sourceWorkspace) {
		this.sourceWorkspace = sourceWorkspace;
	}

	public void setTargetWorkspace(String targetWorkspace) {
		this.targetWorkspace = targetWorkspace;
	}
}
