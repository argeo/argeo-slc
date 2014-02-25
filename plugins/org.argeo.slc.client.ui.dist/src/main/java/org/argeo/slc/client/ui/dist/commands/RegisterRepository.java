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

import javax.jcr.Repository;
import javax.jcr.RepositoryFactory;

import org.argeo.jcr.ArgeoNames;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.client.ui.dist.wizards.RegisterRepoWizard;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.util.security.Keyring;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Connect to a remote repository.
 */
public class RegisterRepository extends AbstractHandler implements ArgeoNames,
		SlcNames {

	public final static String ID = DistPlugin.ID + ".registerRepository";
	public final static String DEFAULT_LABEL = "Register a repository...";
	// public final static String DEFAULT_ICON_PATH = "icons/addRepo.gif";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/addRepo.gif");


	/* DEPENDENCY INJECTION */
	private RepositoryFactory repositoryFactory;
	private Repository nodeRepository;
	private Keyring keyring;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		RegisterRepoWizard wizard = new RegisterRepoWizard(keyring,
				repositoryFactory, nodeRepository);
		WizardDialog dialog = new WizardDialog(
				HandlerUtil.getActiveShell(event), wizard);
		int result = dialog.open();
		if (result == Dialog.OK)
			CommandHelpers.callCommand(RefreshDistributionsView.ID);

		// RemoteRepositoryLoginDialog dlg = new RemoteRepositoryLoginDialog(
		// Display.getDefault().getActiveShell());
		// if (dlg.open() == Dialog.OK) {
		// }
		return null;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}

	public void setNodeRepository(Repository nodeRepository) {
		this.nodeRepository = nodeRepository;
	}

	// class RemoteRepositoryLoginDialog extends TitleAreaDialog {
	// private Text name;
	// private Text uri;
	// private Text username;
	// private Text password;
	// private Button saveInKeyring;
	//
	// public RemoteRepositoryLoginDialog(Shell parentShell) {
	// super(parentShell);
	// }
	//
	// protected Point getInitialSize() {
	// return new Point(600, 400);
	// }
	//
	// protected Control createDialogArea(Composite parent) {
	// Composite dialogarea = (Composite) super.createDialogArea(parent);
	// dialogarea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
	// true));
	// Composite composite = new Composite(dialogarea, SWT.NONE);
	// composite.setLayout(new GridLayout(2, false));
	// composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
	// false));
	// setMessage("Login to remote repository", IMessageProvider.NONE);
	// name = createLT(composite, "Name", "Example Java Repository");
	// uri = createLT(composite, "URI",
	// "https://example.com/data/jcr/java");
	// username = createLT(composite, "User", "");
	// password = createLP(composite, "Password");
	//
	// saveInKeyring = createLC(composite, "Remember password", false);
	// parent.pack();
	// return composite;
	// }
	//
	// @Override
	// protected void createButtonsForButtonBar(Composite parent) {
	// super.createButtonsForButtonBar(parent);
	// Button test = createButton(parent, 2, "Test", false);
	// test.addSelectionListener(new SelectionAdapter() {
	// public void widgetSelected(SelectionEvent arg0) {
	// testConnection();
	// }
	// });
	// }
	//
	// void testConnection() {
	// Session session = null;
	// try {
	// if (uri.getText().startsWith("http")) {// http, https
	// URI checkedUri = new URI(uri.getText());
	// String checkedUriStr = checkedUri.toString();
	// Hashtable<String, String> params = new Hashtable<String, String>();
	// params.put(ArgeoJcrConstants.JCR_REPOSITORY_URI,
	// checkedUriStr);
	// Repository repository = ArgeoJcrUtils.getRepositoryByUri(
	// repositoryFactory, checkedUriStr);
	// if (username.getText().trim().equals("")) {// anonymous
	// session = repository.login();
	// } else {
	// // FIXME use getTextChars() when upgrading to 3.7
	// // see
	// // https://bugs.eclipse.org/bugs/show_bug.cgi?id=297412
	// char[] pwd = password.getText().toCharArray();
	// SimpleCredentials sc = new SimpleCredentials(
	// username.getText(), pwd);
	// session = repository.login(sc);
	// }
	// } else {// alias
	// Repository repository = ArgeoJcrUtils.getRepositoryByAlias(
	// repositoryFactory, uri.getText());
	// session = repository.login();
	// }
	// MessageDialog.openInformation(getParentShell(), "Success",
	// "Connection to '" + uri.getText() + "' successful");
	// } catch (Exception e) {
	// ErrorFeedback.show(
	// "Connection test failed for " + uri.getText(), e);
	// } finally {
	// JcrUtils.logoutQuietly(session);
	// }
	// }
	//
	// @Override
	// protected void okPressed() {
	// Session nodeSession = null;
	// try {
	// nodeSession = nodeRepository.login();
	// String reposPath = UserJcrUtils.getUserHome(nodeSession)
	// .getPath() + RepoConstants.REPOSITORIES_BASE_PATH;
	//
	// Node repos = nodeSession.getNode(reposPath);
	// String repoNodeName = JcrUtils.replaceInvalidChars(name
	// .getText());
	// if (repos.hasNode(repoNodeName))
	// throw new ArgeoException(
	// "There is already a remote repository named "
	// + repoNodeName);
	// Node repoNode = repos.addNode(repoNodeName,
	// ArgeoTypes.ARGEO_REMOTE_REPOSITORY);
	// repoNode.setProperty(ARGEO_URI, uri.getText());
	// repoNode.setProperty(ARGEO_USER_ID, username.getText());
	// repoNode.addMixin(NodeType.MIX_TITLE);
	// repoNode.setProperty(Property.JCR_TITLE, name.getText());
	// nodeSession.save();
	// if (saveInKeyring.getSelection()) {
	// String pwdPath = repoNode.getPath() + '/' + ARGEO_PASSWORD;
	// keyring.set(pwdPath, password.getText().toCharArray());
	// nodeSession.save();
	// }
	// MessageDialog.openInformation(getParentShell(),
	// "Repository Added",
	// "Remote repository " + uri.getText() + "' added");
	//
	// super.okPressed();
	// } catch (Exception e) {
	// ErrorFeedback.show("Cannot add remote repository", e);
	// } finally {
	// JcrUtils.logoutQuietly(nodeSession);
	// }
	// }
	//
	// /** Creates label and text. */
	// protected Text createLT(Composite parent, String label, String initial) {
	// new Label(parent, SWT.NONE).setText(label);
	// Text text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
	// text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	// text.setText(initial);
	// return text;
	// }
	//
	// /** Creates label and check. */
	// protected Button createLC(Composite parent, String label,
	// Boolean initial) {
	// new Label(parent, SWT.NONE).setText(label);
	// Button check = new Button(parent, SWT.CHECK);
	// check.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	// check.setSelection(initial);
	// return check;
	// }
	//
	// protected Text createLP(Composite parent, String label) {
	// new Label(parent, SWT.NONE).setText(label);
	// Text text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER
	// | SWT.PASSWORD);
	// text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	// return text;
	// }
	// }
}
