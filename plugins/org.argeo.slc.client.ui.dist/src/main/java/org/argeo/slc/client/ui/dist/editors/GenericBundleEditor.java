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

import org.argeo.ArgeoException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * 
 * Exposes a bundle and enable its management
 * 
 */
public class GenericBundleEditor extends FormEditor {

	// private final static Log log =
	// LogFactory.getLog(GenericNodeEditor.class);
	public final static String ID = DistPlugin.ID + ".genericBundleEditor";

	// business objects
	private Node bundleNode;

	// This Editor widgets
	private BundleRawPage bundleRawPage;
	private BundleDetailsPage bundleDetailsPage;

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		GenericBundleEditorInput gaei = (GenericBundleEditorInput) getEditorInput();
		bundleNode = gaei.getArtifactNode();
		this.setPartName(gaei.getArtifactId());
	}

	@Override
	protected void addPages() {
		try {
			bundleDetailsPage = new BundleDetailsPage(this, "Main", bundleNode);
			addPage(bundleDetailsPage);
			bundleRawPage = new BundleRawPage(this, "Raw info", bundleNode);
			addPage(bundleRawPage);
		} catch (PartInitException e) {
			throw new ArgeoException("Not able to add an empty page ", e);
		}
	}

	@Override
	public void doSaveAs() {
		// unused compulsory method
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			// Automatically commit all pages of the editor
			commitPages(true);
			firePropertyChange(PROP_DIRTY);
		} catch (Exception e) {
			throw new ArgeoException("Error while saving node", e);
		}

	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
}
