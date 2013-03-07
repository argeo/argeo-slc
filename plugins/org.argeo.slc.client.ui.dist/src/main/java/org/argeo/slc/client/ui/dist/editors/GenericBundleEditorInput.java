/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

public class GenericBundleEditorInput implements IEditorInput, SlcNames {

	private final Node artifactNode;
	// cache key properties at creation time to avoid Exception at recovering time
	// when the session has been closed
	private String artifactId;
	private String groupId;
	private String version;

	public GenericBundleEditorInput(Node artifactNode) {
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
		// return artifactId + ":" + groupId + ":" + version;
		return groupId;
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

		GenericBundleEditorInput other = (GenericBundleEditorInput) obj;
		if (!getGroupId().equals(other.getGroupId()))
			return false;
		if (!getArtifactId().equals(other.getArtifactId()))
			return false;
		if (!getVersion().equals(other.getVersion()))
			return false;
		return true;
	}
}