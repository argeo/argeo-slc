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

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.jcr.SlcNames;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * Editor to browse, analyze and modify an OSGi distribution
 */
public class DistributionEditor extends FormEditor implements SlcNames {
	// private final static Log log =
	// LogFactory.getLog(DistributionEditor.class);
	public final static String ID = DistPlugin.ID + ".distributionEditor";

	private Session session;

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		DistributionEditorInput dei = (DistributionEditorInput) input;
		try {
			session = dei.getRepository().login(dei.getWorkspaceName());
		} catch (RepositoryException e) {
			throw new PartInitException("Cannot log to workspace "
					+ dei.getWorkspaceName(), e);
		}
		setPartName(dei.getWorkspaceName());
		super.init(site, input);
	}

	@Override
	protected void addPages() {
		try {
			addPage(new DistributionOverviewPage(this, "Overview", session));
			addPage(new ArtifactsBrowserPage(this, "Browser", session));
		} catch (PartInitException e) {
			throw new ArgeoException("Cannot add distribution editor pages", e);
		}
	}

	@Override
	public void doSave(IProgressMonitor arg0) {
	}

	@Override
	public void dispose() {
		JcrUtils.logoutQuietly(session);
		super.dispose();
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
