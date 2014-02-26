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
package org.argeo.slc.client.ui.dist.commands;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.editors.DistributionEditor;
import org.argeo.slc.client.ui.dist.editors.DistributionOverviewPage;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * Force refresh of the Distribution overview page of the corresponding editor
 */
public class RefreshDistributionOverviewPage extends AbstractHandler {
	// private static final Log log = LogFactory
	// .getLog(RefreshDistributionOverviewPage.class);

	public final static String ID = DistPlugin.ID
			+ ".refreshDistributionOverviewPage";
	public final static String DEFAULT_LABEL = "Refresh the distribution overview";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/refresh.png");

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (activePart instanceof DistributionEditor) {
			IFormPage ifp = ((DistributionEditor) activePart)
					.getActivePageInstance();
			if (ifp instanceof DistributionOverviewPage)
				((DistributionOverviewPage) ifp).refresh();
		}
		return null;
	}
}