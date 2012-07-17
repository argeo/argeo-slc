/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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

import javax.jcr.Repository;

import org.argeo.slc.jcr.SlcNames;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * An editor input pointing to a distribution
 * */
public class DistributionEditorInput implements IEditorInput, SlcNames {

	private Repository repository;
	private String workspaceName;
	private String artifactsBase = "/";

	public DistributionEditorInput(Repository repository, String workspaceName,
			String artifactsBase) {
		super();
		this.repository = repository;
		this.workspaceName = workspaceName;
		this.artifactsBase = artifactsBase;
	}

	public DistributionEditorInput(Repository repository, String workspaceName) {
		this(repository, workspaceName, "/");
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

	// Dummy compulsory methods
	public String getToolTipText() {
		return workspaceName;
	}

	public String getName() {
		return workspaceName;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DistributionEditorInput))
			return false;

		DistributionEditorInput other = (DistributionEditorInput) obj;
		return getRepository().equals(other.getRepository())
				&& getWorkspaceName().equals(other.getWorkspaceName());
	}

	public Repository getRepository() {
		return repository;
	}

	public String getWorkspaceName() {
		return workspaceName;
	}

	public String getArtifactsBase() {
		return artifactsBase;
	}

}
