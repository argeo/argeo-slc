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

import javax.jcr.Credentials;
import javax.jcr.Repository;

import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.repo.RepoConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * An editor input pointing to a distribution
 * */
public class DistributionEditorInput implements IEditorInput, SlcNames {

	private String repositoryName;
	private Repository repository;
	private String workspaceName;
	private String artifactsBase = RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH;
	private Credentials credentials;

	public DistributionEditorInput(String repositoryName,
			Repository repository, String workspaceName, String artifactsBase,
			Credentials credentials) {
		super();
		this.repository = repository;
		this.repositoryName = repositoryName;
		this.workspaceName = workspaceName;
		this.artifactsBase = artifactsBase;
		this.credentials = credentials;
	}

	public DistributionEditorInput(String repositoryName,
			Repository repository, String workspaceName, Credentials credentials) {
		this(repositoryName, repository, workspaceName,
				RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH, credentials);
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

	public String getRepositoryName() {
		return repositoryName;
	}

	public Credentials getCredentials() {
		return credentials;
	}
}