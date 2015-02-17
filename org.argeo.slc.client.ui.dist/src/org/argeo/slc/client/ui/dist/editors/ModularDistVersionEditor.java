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

import javax.jcr.RepositoryException;

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
	private static final long serialVersionUID = -2223542780164288554L;

	// private final static Log log =
	// LogFactory.getLog(ModularDistVersionEditor.class);
	public final static String ID = DistPlugin.PLUGIN_ID + ".modularDistVersionEditor";

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
	}

	@Override
	protected void addPages() {
		setPartName(getFormattedName());
		try {
			addPage(new ModularDistVersionOverviewPage(this, "Modules ",
					getArtifact()));
			addPage(new RunInOsgiPage(this, "Run as OSGi ", getArtifact()));
			addPage(new ModularDistVersionDetailPage(this, "Details",
					getArtifact()));
		} catch (PartInitException e) {
			throw new SlcException("Cannot add distribution editor pages", e);
		}
	}

	protected String getFormattedName() {
		try {
			String partName = null;
			if (getArtifact().hasProperty(SLC_NAME))
				partName = getArtifact().getProperty(SLC_NAME).getString();
			else
				partName = getArtifact().getName();

			if (partName.length() > 10) {
				partName = "..." + partName.substring(partName.length() - 10);
			}
			return partName;
		} catch (RepositoryException re) {
			throw new SlcException("unable to get slc:name property for node "
					+ getArtifact(), re);
		}
	}

}