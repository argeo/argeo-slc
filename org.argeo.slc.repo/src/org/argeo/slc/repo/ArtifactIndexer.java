package org.argeo.slc.repo;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.repo.maven.AetherUtils;
import org.eclipse.aether.artifact.Artifact;
import org.osgi.framework.Constants;

/**
 * Add {@link Artifact} properties to a {@link Node}. Does nothing if the node
 * name doesn't start with the artifact id (in order to skip Maven metadata XML
 * files and other non artifact files).
 */
public class ArtifactIndexer implements NodeIndexer, SlcNames {
	private CmsLog log = CmsLog.getLog(ArtifactIndexer.class);
	private Boolean force = false;

	public Boolean support(String path) {
		String relativePath = getRelativePath(path);
		if (relativePath == null)
			return false;
		Artifact artifact = null;
		try {
			artifact = AetherUtils.convertPathToArtifact(relativePath, null);
		} catch (Exception e) {
			if (log.isTraceEnabled())
				log.trace("Malformed path " + path + ", skipping silently", e);
		}
		return artifact != null;
	}

	public void index(Node fileNode) {
		Artifact artifact = null;
		try {
			if (!support(fileNode.getPath()))
				return;

			// Already indexed
			if (!force && fileNode.isNodeType(SlcTypes.SLC_ARTIFACT))
				return;

			if (!fileNode.isNodeType(NodeType.NT_FILE))
				return;

			String relativePath = getRelativePath(fileNode.getPath());
			if (relativePath == null)
				return;
			artifact = AetherUtils.convertPathToArtifact(relativePath, null);
			// support() guarantees that artifact won't be null, no NPE check
			fileNode.addMixin(SlcTypes.SLC_ARTIFACT);
			fileNode.setProperty(SlcNames.SLC_ARTIFACT_ID, artifact.getArtifactId());
			fileNode.setProperty(SlcNames.SLC_GROUP_ID, artifact.getGroupId());
			fileNode.setProperty(SlcNames.SLC_ARTIFACT_VERSION, artifact.getVersion());
			fileNode.setProperty(SlcNames.SLC_ARTIFACT_EXTENSION, artifact.getExtension());
			// can be null but ok for JCR API
			fileNode.setProperty(SlcNames.SLC_ARTIFACT_CLASSIFIER, artifact.getClassifier());
			JcrUtils.updateLastModified(fileNode);

			// make sure there are checksums
			String shaNodeName = fileNode.getName() + ".sha1";
			if (!fileNode.getParent().hasNode(shaNodeName)) {
				String sha = JcrUtils.checksumFile(fileNode, "SHA-1");
				JcrUtils.copyBytesAsFile(fileNode.getParent(), shaNodeName, sha.getBytes());
			}
			String md5NodeName = fileNode.getName() + ".md5";
			if (!fileNode.getParent().hasNode(md5NodeName)) {
				String md5 = JcrUtils.checksumFile(fileNode, "MD5");
				JcrUtils.copyBytesAsFile(fileNode.getParent(), md5NodeName, md5.getBytes());
			}

			// Create a default pom if none already exist
			String fileNodeName = fileNode.getName();
			String pomName = null;
			if (fileNodeName.endsWith(".jar"))
				pomName = fileNodeName.substring(0, fileNodeName.length() - ".jar".length()) + ".pom";

			if (pomName != null && !fileNode.getParent().hasNode(pomName)) {
				String pom = generatePomForBundle(fileNode);
				Node pomNode = JcrUtils.copyBytesAsFile(fileNode.getParent(), pomName, pom.getBytes());
				// corresponding check sums
				String sha = JcrUtils.checksumFile(pomNode, "SHA-1");
				JcrUtils.copyBytesAsFile(fileNode.getParent(), pomName + ".sha1", sha.getBytes());
				String md5 = JcrUtils.checksumFile(fileNode, "MD5");
				JcrUtils.copyBytesAsFile(fileNode.getParent(), pomName + ".md5", md5.getBytes());
			}

			// set higher levels
			Node artifactVersionBase = fileNode.getParent();
			if (!artifactVersionBase.isNodeType(SlcTypes.SLC_ARTIFACT_VERSION_BASE)) {
				artifactVersionBase.addMixin(SlcTypes.SLC_ARTIFACT_VERSION_BASE);
				artifactVersionBase.setProperty(SlcNames.SLC_ARTIFACT_VERSION, artifact.getBaseVersion());
				artifactVersionBase.setProperty(SlcNames.SLC_ARTIFACT_ID, artifact.getArtifactId());
				artifactVersionBase.setProperty(SlcNames.SLC_GROUP_ID, artifact.getGroupId());
			}
			JcrUtils.updateLastModified(artifactVersionBase);

			// pom
			if (artifact.getExtension().equals("pom")) {
				// TODO read to make it a distribution
			}

			Node artifactBase = artifactVersionBase.getParent();
			if (!artifactBase.isNodeType(SlcTypes.SLC_ARTIFACT_BASE)) {
				artifactBase.addMixin(SlcTypes.SLC_ARTIFACT_BASE);
				artifactBase.setProperty(SlcNames.SLC_ARTIFACT_ID, artifact.getArtifactId());
				artifactBase.setProperty(SlcNames.SLC_GROUP_ID, artifact.getGroupId());
			}
			JcrUtils.updateLastModified(artifactBase);

			Node groupBase = artifactBase.getParent();
			if (!groupBase.isNodeType(SlcTypes.SLC_GROUP_BASE)) {
				// if (groupBase.isNodeType(SlcTypes.SLC_ARTIFACT_BASE)) {
				// log.warn("Group base " + groupBase.getPath()
				// + " is also artifact base");
				// }
				groupBase.addMixin(SlcTypes.SLC_GROUP_BASE);
				groupBase.setProperty(SlcNames.SLC_GROUP_BASE_ID, artifact.getGroupId());
			}
			JcrUtils.updateLastModifiedAndParents(groupBase, RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH);

			if (log.isTraceEnabled())
				log.trace("Indexed artifact " + artifact + " on " + fileNode);
		} catch (Exception e) {
			throw new SlcException("Cannot index artifact " + artifact + " metadata on node " + fileNode, e);
		}
	}

	private String getRelativePath(String nodePath) {
		String basePath = RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH;
		if (!nodePath.startsWith(basePath))
			return null;
		String relativePath = nodePath.substring(basePath.length());
		return relativePath;
	}

	public void setForce(Boolean force) {
		this.force = force;
	}

	private String generatePomForBundle(Node n) throws RepositoryException {
		StringBuffer p = new StringBuffer();
		p.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		p.append(
				"<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n");
		p.append("<modelVersion>4.0.0</modelVersion>");

		// Categorized name version
		p.append("<groupId>").append(JcrUtils.get(n, SLC_GROUP_ID)).append("</groupId>\n");
		p.append("<artifactId>").append(JcrUtils.get(n, SLC_ARTIFACT_ID)).append("</artifactId>\n");
		p.append("<version>").append(JcrUtils.get(n, SLC_ARTIFACT_VERSION)).append("</version>\n");
		// TODO make it more generic
		p.append("<packaging>jar</packaging>\n");
		if (n.hasProperty(SLC_ + Constants.BUNDLE_NAME))
			p.append("<name>").append(JcrUtils.get(n, SLC_ + Constants.BUNDLE_NAME)).append("</name>\n");
		if (n.hasProperty(SLC_ + Constants.BUNDLE_DESCRIPTION))
			p.append("<description>").append(JcrUtils.get(n, SLC_ + Constants.BUNDLE_DESCRIPTION))
					.append("</description>\n");

		// Dependencies in case of a distribution
		if (n.isNodeType(SlcTypes.SLC_MODULAR_DISTRIBUTION)) {
			p.append(getDependenciesSnippet(n.getNode(SlcNames.SLC_MODULES).getNodes()));
			p.append(getDependencyManagementSnippet(n.getNode(SlcNames.SLC_MODULES).getNodes()));
		}
		p.append("</project>\n");
		return p.toString();
	}

	private String getDependenciesSnippet(NodeIterator nit) throws RepositoryException {
		StringBuilder b = new StringBuilder();
		b.append("<dependencies>\n");
		while (nit.hasNext()) {
			Node currModule = nit.nextNode();
			if (currModule.isNodeType(SlcTypes.SLC_MODULE_COORDINATES)) {
				b.append(getDependencySnippet(currModule.getProperty(SlcNames.SLC_CATEGORY).getString(),
						currModule.getProperty(SlcNames.SLC_NAME).getString(), null));
			}
		}
		b.append("</dependencies>\n");
		return b.toString();
	}

	private String getDependencyManagementSnippet(NodeIterator nit) throws RepositoryException {
		StringBuilder b = new StringBuilder();
		b.append("<dependencyManagement>\n");
		b.append("<dependencies>\n");
		while (nit.hasNext()) {
			Node currModule = nit.nextNode();
			if (currModule.isNodeType(SlcTypes.SLC_MODULE_COORDINATES)) {
				b.append(getDependencySnippet(currModule.getProperty(SlcNames.SLC_CATEGORY).getString(),
						currModule.getProperty(SlcNames.SLC_NAME).getString(),
						currModule.getProperty(SlcNames.SLC_VERSION).getString()));
			}
		}
		b.append("</dependencies>\n");
		b.append("</dependencyManagement>\n");
		return b.toString();
	}

	private String getDependencySnippet(String category, String name, String version) {
		StringBuilder b = new StringBuilder();
		b.append("<dependency>\n");
		b.append("\t<groupId>").append(category).append("</groupId>\n");
		b.append("\t<artifactId>").append(name).append("</artifactId>\n");
		if (version != null)
			b.append("\t<version>").append(version).append("</version>\n");
		b.append("</dependency>\n");
		return b.toString();
	}
}