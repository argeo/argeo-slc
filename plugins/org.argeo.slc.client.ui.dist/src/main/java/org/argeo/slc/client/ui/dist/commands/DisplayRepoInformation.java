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

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.model.RepoElem;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Create a new empty workspace in the current repository.
 */

public class DisplayRepoInformation extends AbstractHandler {
	public final static String ID = DistPlugin.ID + ".displayRepoInformation";
	public final static String DEFAULT_LABEL = "Repository infos...";
	public final static String DEFAULT_ICON_PATH = "icons/help.gif";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		IStructuredSelection iss = (IStructuredSelection) HandlerUtil
				.getActiveSite(event).getSelectionProvider().getSelection();

		if (iss.getFirstElement() instanceof RepoElem) {
			RepoElem re = (RepoElem) iss.getFirstElement();
			InformationDialog inputDialog = new InformationDialog(HandlerUtil
					.getActiveSite(event).getShell());
			inputDialog.create();
			Session session = null;
			try {
				session = re.getRepository().login(re.getCredentials());
				inputDialog.loginTxt.setText(session.getUserID());
				inputDialog.nameTxt.setText(re.getLabel());
				inputDialog.uriTxt.setText(re.getUri());
				inputDialog.readOnlyBtn.setSelection(re.isReadOnly());
			} catch (RepositoryException e) {
				throw new SlcException("Unexpected error while "
						+ "getting repository infos.", e);
			} finally {
				JcrUtils.logoutQuietly(session);
			}
			inputDialog.open();
		}
		return null;
	}

	public class InformationDialog extends Dialog {
		Text nameTxt;
		Text uriTxt;
		Text loginTxt;
		Button readOnlyBtn;

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			// No Cancel button
			createButton(parent, IDialogConstants.OK_ID, "OK", true);
		}

		public InformationDialog(Shell parentShell) {
			super(parentShell);
		}

		protected Point getInitialSize() {
			return new Point(500, 250);
		}

		protected Control createDialogArea(Composite parent) {

			Composite dialogarea = (Composite) super.createDialogArea(parent);
			dialogarea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					true));
			Composite composite = new Composite(dialogarea, SWT.NONE);
			GridLayout layout = new GridLayout(2, false);
			layout.horizontalSpacing = 15;
			composite.setLayout(layout);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			composite.setLayoutData(gd);

			nameTxt = createLT(composite, "Name");
			uriTxt = createLT(composite, "URI");
			loginTxt = createLT(composite, "Logged as");
			readOnlyBtn = createLC(composite, "Read only");
			parent.pack();
			return composite;
		}

		/** Creates label and text. */
		protected Text createLT(Composite parent, String label) {
			new Label(parent, SWT.NONE).setText(label);
			Text text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.NONE);
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			text.setEditable(false);
			return text;
		}

		/** Creates label and check. */
		protected Button createLC(Composite parent, String label) {
			new Label(parent, SWT.NONE).setText(label);
			Button check = new Button(parent, SWT.CHECK);
			check.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			check.setEnabled(false);
			return check;
		}

		protected void configureShell(Shell shell) {
			super.configureShell(shell);
			shell.setText("Repository information");
		}
	}
}