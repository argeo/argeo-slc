package org.argeo.slc.client.ui.dist.editors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.ArgeoException;
import org.argeo.slc.jcr.SlcNames;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * An editor input based the JCR node object.
 * */

public class GenericArtifactEditorInput implements IEditorInput, SlcNames {

	private final Node artifactNode;
	// cache key properties at creation time to avoid Exception at recoring time
	// when the session has been closed
	private String artifactId;
	private String groupId;
	private String version;

	public GenericArtifactEditorInput(Node artifactNode) {
		this.artifactNode = artifactNode;
		try {
			artifactId = artifactNode.getProperty(SLC_ARTIFACT_ID).getString();
			groupId = artifactNode.getProperty(SLC_GROUP_ID).getString();
			version = artifactNode.getProperty(SLC_ARTIFACT_VERSION)
					.getString();
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"unexpected error while getting node key values at creation time",
					re);
		}
	}

	public Node getArtifactNode() {
		return artifactNode;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getVersion() {
		return version;
	}

	// Dummy compulsory methods
	public String getToolTipText() {
		return artifactId + ":" + groupId + ":" + version;
	}

	public String getName() {
		return artifactId + ":" + groupId + ":" + version;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * equals method based on coordinates
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		GenericArtifactEditorInput other = (GenericArtifactEditorInput) obj;
		if (!getGroupId().equals(other.getGroupId()))
			return false;
		if (!getArtifactId().equals(other.getArtifactId()))
			return false;
		if (!getVersion().equals(other.getVersion()))
			return false;
		return true;
	}
}
