package org.argeo.slc.repo.maven;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Manifest;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.osgi.framework.Constants;

/**
 * Migrate the distribution from 1.2 to 1.4 by cleaning naming and dependencies.
 * The dependency to the SpringSource Enterprise Bundle repository is removed as
 * well as theire naming convention. All third party are move to org.argeo.tp
 * group IDs. Maven dependency for Eclipse artifacts don't use version ranges
 * anymore. Verison constraints on javax.* packages are removed (since they lead
 * to "use package conflicts" when Eclipse and Spring Security are used
 * together).
 */
public class Migration_01_03 implements Runnable, SlcNames {
	private final static Log log = LogFactory.getLog(Migration_01_03.class);

	private Repository repository;
	private String sourceWorkspace;
	private String targetWorkspace;

	private Session sourceSession;
	private Session targetSession;

	private List<String> systemPackages;

	public void init() throws RepositoryException {
		sourceSession = JcrUtils.loginOrCreateWorkspace(repository,
				sourceWorkspace);
		targetSession = JcrUtils.loginOrCreateWorkspace(repository,
				targetWorkspace);

		// works only in OSGi!!
		systemPackages = Arrays.asList(System.getProperty(
				"org.osgi.framework.system.packages").split(","));
	}

	public void destroy() {
		JcrUtils.logoutQuietly(sourceSession);
		JcrUtils.logoutQuietly(targetSession);
	}

	public void run() {
		log.debug(System.getProperty("org.osgi.framework.system.packages"));
		try {
			NodeIterator sourceArtifacts = listArtifactVersions(sourceSession);
			while (sourceArtifacts.hasNext()) {
				Node sourceArtifactNode = sourceArtifacts.nextNode();
				if (log.isTraceEnabled())
					log.trace(sourceArtifactNode);

				processSourceArtifactVersion(sourceArtifactNode);
			}
		} catch (Exception e) {
			throw new SlcException("Cannot perform v1.3 migration from "
					+ sourceWorkspace + " to " + targetWorkspace, e);
		}
	}

	protected void processSourceArtifactVersion(Node sourceArtifactNode)
			throws RepositoryException, IOException {
		// find jar node
		String sourceJarNodeName = sourceArtifactNode.getProperty(
				SLC_ARTIFACT_ID).getString()
				+ "-"
				+ sourceArtifactNode.getProperty(SLC_ARTIFACT_VERSION)
						.getString() + ".jar";
		if (!sourceArtifactNode.hasNode(sourceJarNodeName))
			throw new SlcException("Cannot find jar node for "
					+ sourceArtifactNode);
		Node sourceJarNode = sourceArtifactNode.getNode(sourceJarNodeName);

		// read MANIFEST
		Binary manifestBinary = sourceJarNode.getProperty(SLC_MANIFEST)
				.getBinary();
		Manifest sourceManifest = new Manifest(manifestBinary.getStream());
		JcrUtils.closeQuietly(manifestBinary);

		Boolean manifestModified = false;
		Manifest targetManifest = new Manifest(sourceManifest);

		// transform symbolic name
		String sourceSymbolicName = sourceManifest.getMainAttributes()
				.getValue(Constants.BUNDLE_SYMBOLICNAME);
		final String SPRING_SOURCE_PREFIX = "com.springsource";
		if (sourceSymbolicName.startsWith(SPRING_SOURCE_PREFIX)
				&& !sourceSymbolicName.equals(SPRING_SOURCE_PREFIX + ".json")) {
			String targetSymbolicName = sourceSymbolicName
					.substring(SPRING_SOURCE_PREFIX.length() + 1);
			if (log.isDebugEnabled())
				log.debug(Constants.BUNDLE_SYMBOLICNAME + " to "
						+ targetSymbolicName + " \t\tfrom "
						+ sourceSymbolicName);
			targetManifest.getMainAttributes().putValue(
					Constants.BUNDLE_SYMBOLICNAME, targetSymbolicName);
			manifestModified = true;
		}

		// check fragment host
		if (sourceManifest.getMainAttributes().containsKey(
				Constants.FRAGMENT_HOST)) {
			String fragmentHost = sourceManifest.getMainAttributes().getValue(
					Constants.FRAGMENT_HOST);
			if (fragmentHost.startsWith(SPRING_SOURCE_PREFIX)
					&& !fragmentHost.equals(SPRING_SOURCE_PREFIX + ".json")) {
				String targetFragmentHost = fragmentHost
						.substring(SPRING_SOURCE_PREFIX.length() + 1);
				if (log.isDebugEnabled())
					log.debug(Constants.FRAGMENT_HOST + " to "
							+ targetFragmentHost + " from " + fragmentHost);
				targetManifest.getMainAttributes().putValue(
						Constants.FRAGMENT_HOST, targetFragmentHost);
				manifestModified = true;
			}
		}

		// javax with versions
		StringBuffer targetImportPackages = new StringBuffer("");
		NodeIterator sourceImportPackages = sourceJarNode.getNodes(SLC_
				+ Constants.IMPORT_PACKAGE);
		Boolean importPackagesModified = false;
		while (sourceImportPackages.hasNext()) {
			Node importPackage = sourceImportPackages.nextNode();
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
							log.debug(sourceSymbolicName
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
			if (sourceImportPackages.hasNext())
				targetImportPackages.append(",");
		}

		if (importPackagesModified) {
			targetManifest.getMainAttributes().putValue(
					Constants.IMPORT_PACKAGE, targetImportPackages.toString());
			manifestModified = true;
		}

		if (!manifestModified && log.isTraceEnabled()) {
			log.trace("MANIFEST of " + sourceSymbolicName + " was not modified");
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
