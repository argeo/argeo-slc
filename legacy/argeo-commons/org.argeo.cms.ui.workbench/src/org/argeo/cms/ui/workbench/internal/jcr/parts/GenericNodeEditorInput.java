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
package org.argeo.cms.ui.workbench.internal.jcr.parts;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.EclipseUiException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/** Editor input for {@link Node} editors */
public class GenericNodeEditorInput implements IEditorInput {
	private final Node currentNode;

	// Caches key properties at creation time to avoid Exception at recovering
	// time when the session has been closed
	private String path;
	private String uid;
	private String name;

	public GenericNodeEditorInput(Node currentNode) {
		this.currentNode = currentNode;
		try {
			name = currentNode.getName();
			uid = currentNode.getIdentifier();
			path = currentNode.getPath();
		} catch (RepositoryException re) {
			throw new EclipseUiException("Cannot cache the key properties for " + currentNode, re);
		}
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return name;
	}

	public String getUid() {
		return uid;
	}

	public String getToolTipText() {
		return path;
	}

	public String getPath() {
		return path;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * Equals method based on UID that is unique within a workspace and path of
	 * the node, thus 2 shared node that have same UID as defined in the spec
	 * but 2 different paths will open two distinct editors.
	 * 
	 * TODO enhance this method to support multi repository and multi workspace
	 * environments
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		GenericNodeEditorInput other = (GenericNodeEditorInput) obj;
		if (!getUid().equals(other.getUid()))
			return false;
		if (!getPath().equals(other.getPath()))
			return false;
		return true;
	}
}
