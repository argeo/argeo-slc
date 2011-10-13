package org.argeo.slc.repo;

import javax.jcr.Node;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.aether.AetherUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.sonatype.aether.artifact.Artifact;

/**
 * Add {@link Artifact} properties to a {@link Node}. Does nothing if the node
 * name doesn't start with the artifact id (in order to skip Maven metadata XML
 * files and other non artifact files).
 */
public class ArtifactIndexer implements NodeIndexer {
	private Log log = LogFactory.getLog(ArtifactIndexer.class);

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
			if (!fileNode.isNodeType(NodeType.NT_FILE))
				return;

			String relativePath = getRelativePath(fileNode.getPath());
			if (relativePath == null)
				return;
			artifact = AetherUtils.convertPathToArtifact(relativePath, null);
			// support() guarantees that artifact won't be null, no NPE check
			fileNode.addMixin(SlcTypes.SLC_ARTIFACT);
			fileNode.setProperty(SlcNames.SLC_ARTIFACT_ID,
					artifact.getArtifactId());
			fileNode.setProperty(SlcNames.SLC_GROUP_ID, artifact.getGroupId());
			fileNode.setProperty(SlcNames.SLC_ARTIFACT_VERSION,
					artifact.getVersion());
			fileNode.setProperty(SlcNames.SLC_ARTIFACT_EXTENSION,
					artifact.getExtension());
			// can be null but ok for JCR API
			fileNode.setProperty(SlcNames.SLC_ARTIFACT_CLASSIFIER,
					artifact.getClassifier());
			JcrUtils.updateLastModified(fileNode);

			// set higher levels
			Node artifactVersionBase = fileNode.getParent();
			if (!artifactVersionBase
					.isNodeType(SlcTypes.SLC_ARTIFACT_VERSION_BASE)) {
				artifactVersionBase
						.addMixin(SlcTypes.SLC_ARTIFACT_VERSION_BASE);
				artifactVersionBase.setProperty(SlcNames.SLC_ARTIFACT_VERSION,
						artifact.getBaseVersion());
				artifactVersionBase.setProperty(SlcNames.SLC_ARTIFACT_ID,
						artifact.getArtifactId());
				artifactVersionBase.setProperty(SlcNames.SLC_GROUP_ID,
						artifact.getGroupId());
				JcrUtils.updateLastModified(artifactVersionBase);
			}
			Node artifactBase = artifactVersionBase.getParent();
			if (!artifactBase.isNodeType(SlcTypes.SLC_ARTIFACT_BASE)) {
				artifactBase.addMixin(SlcTypes.SLC_ARTIFACT_BASE);
				artifactBase.setProperty(SlcNames.SLC_ARTIFACT_ID,
						artifact.getArtifactId());
				artifactBase.setProperty(SlcNames.SLC_GROUP_ID,
						artifact.getGroupId());
				JcrUtils.updateLastModified(artifactBase);
			}
			
			// TODO do we really need group base?
			Node groupBase = artifactBase.getParent();
			if (!groupBase.isNodeType(SlcTypes.SLC_GROUP_BASE)) {
				// if (groupBase.isNodeType(SlcTypes.SLC_ARTIFACT_BASE)) {
				// log.warn("Group base " + groupBase.getPath()
				// + " is also artifact base");
				// }
				groupBase.addMixin(SlcTypes.SLC_GROUP_BASE);
				groupBase.setProperty(SlcNames.SLC_GROUP_BASE_ID,
						artifact.getGroupId());
				JcrUtils.updateLastModified(groupBase);
			}

			if (log.isTraceEnabled())
				log.trace("Indexed artifact " + artifact + " on " + fileNode);
		} catch (Exception e) {
			throw new SlcException("Cannot index artifact " + artifact
					+ " metadata on node " + fileNode, e);
		}
	}

	private String getRelativePath(String nodePath) {
		String basePath = RepoConstants.ARTIFACTS_BASE_PATH;
		if (!nodePath.startsWith(basePath))
			return null;
		String relativePath = nodePath.substring(basePath.length());
		return relativePath;
	}
}
