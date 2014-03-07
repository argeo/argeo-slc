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
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.util.security.Keyring;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * Base editor to manage an artifact in a multiple repository environment
 */
public class ArtifactVersionEditor extends FormEditor implements SlcNames {
	// private final static Log log =
	// LogFactory.getLog(ArtifactEditor.class);
	public final static String ID = DistPlugin.ID + ".artifactVersionEditor";

	private ModuleEditorInput editorInput;

	/* DEPENDENCY INJECTION */
	private RepositoryFactory repositoryFactory;
	private Repository localRepository;
	private Keyring keyring;

	// Business objects
	private Node repoNode;
	// Session that provides the node in the home of the local repository
	private Session localSession = null;
	// The business Session on an optionally remote repository
	private Session businessSession;
	private Node artifact;

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		editorInput = (ModuleEditorInput) input;
		try {
			localSession = localRepository.login();
			if (editorInput.getRepoNodePath() != null
					&& localSession.nodeExists(editorInput.getRepoNodePath()))
				repoNode = localSession.getNode(editorInput.getRepoNodePath());
			businessSession = RepoUtils.getCorrespondingSession(
					repositoryFactory, keyring, repoNode, editorInput.getUri(),
					editorInput.getWorkspaceName());
			artifact = businessSession.getNode(editorInput.getModulePath());
		} catch (RepositoryException e) {
			throw new PartInitException(
					"Unable to initialise editor for artifact "
							+ editorInput.getModulePath() + " in workspace "
							+ editorInput.getWorkspaceName()
							+ " of repository " + editorInput.getUri(), e);
		}
		setPartName(getFormattedName());
		super.init(site, input);
	}

	/** Override to provide a specific part name */
	protected String getFormattedName() {
		try {
			String partName = artifact.getProperty(SLC_ARTIFACT_ID).getString();

			if (partName.length() > 10) {
				partName = "..." + partName.substring(partName.length() - 10);
			}
			return partName;
		} catch (RepositoryException re) {
			throw new SlcException(
					"unable to get slc:artifactId Property for node "
							+ artifact, re);
		}
	}

	@Override
	protected void addPages() {
		try {
			addPage(new BundleDetailsPage(this, "Details ", artifact));
			addPage(new BundleRawPage(this, "Raw Meta-Data ", artifact));
		} catch (PartInitException e) {
			throw new ArgeoException("Cannot add distribution editor pages", e);
		}
	}

	@Override
	public void doSave(IProgressMonitor arg0) {
	}

	@Override
	public void dispose() {
		JcrUtils.logoutQuietly(businessSession);
		JcrUtils.logoutQuietly(localSession);
		super.dispose();
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	protected Node getRepoNode() {
		return repoNode;
	}

	protected Node getArtifact() {
		return artifact;
	}

	/* DEPENDENCY INJECTION */
	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}

	public void setLocalRepository(Repository localRepository) {
		this.localRepository = localRepository;
	}
}