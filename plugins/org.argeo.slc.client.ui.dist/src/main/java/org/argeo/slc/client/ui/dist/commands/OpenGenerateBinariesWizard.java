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

import java.util.Iterator;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.model.ModularDistBaseElem;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.client.ui.dist.wizards.GenerateBinariesWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Open a {@code GenerateBinariesWizard} wizard for the selected node
 */
public class OpenGenerateBinariesWizard extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(DeleteWorkspace.class);

	public final static String ID = DistPlugin.ID
			+ ".openGenerateBinariesWizard";
	public final static String DEFAULT_LABEL = "Generate Aether Index";
	public final static ImageDescriptor DEFAULT_ICON = null;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();

		if (activePart instanceof IViewPart) {
			ISelection selector = ((IViewPart) activePart).getViewSite()
					.getSelectionProvider().getSelection();
			if (selector != null && selector instanceof IStructuredSelection) {
				Iterator<?> it = ((IStructuredSelection) selector).iterator();
				Object element = it.next();
				if (element instanceof ModularDistBaseElem) {
					ModularDistBaseElem elem = (ModularDistBaseElem) element;

					GenerateBinariesWizard wizard = new GenerateBinariesWizard(
							elem.getCategoryBase());
					WizardDialog dialog = new WizardDialog(
							HandlerUtil.getActiveShell(event), wizard);
					int result = dialog.open();
					if (result == Dialog.OK)
						CommandHelpers.callCommand(RefreshDistributionsView.ID);
				}

			}
		}
		return null;
	}
}