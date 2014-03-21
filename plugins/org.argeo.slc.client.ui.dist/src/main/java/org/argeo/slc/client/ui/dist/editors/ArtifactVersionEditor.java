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
import javax.jcr.Session;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.RepoService;
import org.argeo.slc.jcr.SlcNames;
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

	/* DEPENDENCY INJECTION */
	private RepoService repoService;

	// Business Objects
	private Session businessSession;
	private Node artifact;

	private ModuleEditorInput editorInput;

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		editorInput = (ModuleEditorInput) input;
		businessSession = repoService.getRemoteSession(
				editorInput.getRepoNodePath(), editorInput.getUri(),
				editorInput.getWorkspaceName());
		try {
			artifact = businessSession.getNode(editorInput.getModulePath());
		} catch (RepositoryException e) {
			throw new PartInitException(
					"Unable to initialise editor for artifact "
							+ editorInput.getModulePath() + " in workspace "
							+ editorInput.getWorkspaceName(), e);
		}
		super.init(site, input);
	}

	/** Override to provide a specific part name */
	protected String getFormattedName() {
		try {
			String partName = null;
			if (artifact.hasProperty(SLC_ARTIFACT_ID))
				partName = artifact.getProperty(SLC_ARTIFACT_ID).getString();
			else
				partName = artifact.getName();

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
		setPartName(getFormattedName());

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
		super.dispose();
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	protected RepoService getRepoService() {
		return repoService;
	}

	protected Node getArtifact() {
		return artifact;
	}

	/* DEPENDENCY INJECTION */
	public void setRepoService(RepoService repoService) {
		this.repoService = repoService;
	}
}