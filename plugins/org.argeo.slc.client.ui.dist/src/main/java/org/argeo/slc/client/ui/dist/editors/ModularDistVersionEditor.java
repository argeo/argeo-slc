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

import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/**
 * Manage a modular distribution version contained in a specific workspace of a
 * repository
 */
public class ModularDistVersionEditor extends ArtifactVersionEditor {
	// private final static Log log =
	// LogFactory.getLog(ModularDistVersionEditor.class);
	public final static String ID = DistPlugin.ID + ".modularDistVersionEditor";

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		// setPartName("Editing distrib");
	}

	@Override
	protected void addPages() {
		try {
			addPage(new ModuleListPage(this, "Modules ", getArtifact()));
			addPage(new RunInOsgiPage(this, "Run as OSGi ", getArtifact()));
			super.addPages();
		} catch (PartInitException e) {
			throw new SlcException("Cannot add distribution editor pages", e);
			// } catch (RepositoryException e) {
			// throw new SlcException("Cannot get artifact session", e);
		}
	}
}