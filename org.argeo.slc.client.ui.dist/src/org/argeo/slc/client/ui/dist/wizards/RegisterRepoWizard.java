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
package org.argeo.slc.client.ui.dist.wizards;

import java.net.URI;
import java.util.Hashtable;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeType;

import org.argeo.eclipse.ui.dialogs.ErrorFeedback;
import org.argeo.jcr.ArgeoJcrConstants;
import org.argeo.jcr.ArgeoJcrUtils;
import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.ArgeoTypes;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.UserJcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.util.security.Keyring;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Registers a new remote repository in the current Node.
 * 
 */
public class RegisterRepoWizard extends Wizard {

	// Business objects
	private Keyring keyring;
	private RepositoryFactory repositoryFactory;
	private Repository nodeRepository;

	// Pages
	private DefineModelPage page;

	// Widgets of model page
	private Text name;
	private Text uri;
	private Text username;
	private Text password;
	private Button saveInKeyring;

	// Default values
	private final static String DEFAULT_NAME = "Argeo Public Repository";
	private final static String DEFAULT_URI = "http://repo.argeo.org/data/pub/java";
	private final static String DEFAULT_USER_NAME = "anonymous";
	private final static boolean DEFAULT_ANONYMOUS = true;

	public RegisterRepoWizard(Keyring keyring,
			RepositoryFactory repositoryFactory, Repository nodeRepository) {
		super();
		this.keyring = keyring;
		this.repositoryFactory = repositoryFactory;
		this.nodeRepository = nodeRepository;
	}

	@Override
	public void addPages() {
		try {
			page = new DefineModelPage();
			addPage(page);
			setWindowTitle("Register a new remote repository");
		} catch (Exception e) {
			throw new SlcException("Cannot add page to wizard ", e);
		}
	}

	@Override
	public boolean performFinish() {
		if (!canFinish())
			return false;

		Session nodeSession = null;
		try {
			nodeSession = nodeRepository.login();
			String reposPath = UserJcrUtils.getUserHome(nodeSession).getPath()
					+ RepoConstants.REPOSITORIES_BASE_PATH;

			Node repos = nodeSession.getNode(reposPath);
			String repoNodeName = JcrUtils.replaceInvalidChars(name.getText());
			if (repos.hasNode(repoNodeName))
				throw new SlcException(
						"There is already a remote repository named "
								+ repoNodeName);

			// check if the same URI has already been registered
			NodeIterator ni = repos.getNodes();
			while (ni.hasNext()) {
				Node node = ni.nextNode();
				if (node.isNodeType(ArgeoTypes.ARGEO_REMOTE_REPOSITORY)
						&& node.hasProperty(ArgeoNames.ARGEO_URI)
						&& node.getProperty(ArgeoNames.ARGEO_URI).getString()
								.equals(uri.getText()))
					throw new SlcException(
							"This URI "
									+ uri.getText()
									+ " is already registered, "
									+ "for the time being, only one instance of a single "
									+ "repository at a time is implemented.");
			}

			Node repoNode = repos.addNode(repoNodeName,
					ArgeoTypes.ARGEO_REMOTE_REPOSITORY);
			repoNode.setProperty(ArgeoNames.ARGEO_URI, uri.getText());
			repoNode.setProperty(ArgeoNames.ARGEO_USER_ID, username.getText());
			repoNode.addMixin(NodeType.MIX_TITLE);
			repoNode.setProperty(Property.JCR_TITLE, name.getText());
			nodeSession.save();
			if (saveInKeyring.getSelection()) {
				String pwdPath = repoNode.getPath() + '/'
						+ ArgeoNames.ARGEO_PASSWORD;
				keyring.set(pwdPath, password.getText().toCharArray());
				nodeSession.save();
			}
			MessageDialog.openInformation(getShell(), "Repository Added",
					"Remote repository " + uri.getText() + "' added");
		} catch (Exception e) {
			ErrorFeedback.show("Cannot add remote repository", e);
		} finally {
			JcrUtils.logoutQuietly(nodeSession);
		}
		return true;
	}

	private class DefineModelPage extends WizardPage {
		private static final long serialVersionUID = 874386824101995060L;

		public DefineModelPage() {
			super("Main");
			setTitle("Fill information to register a repository");
		}

		public void createControl(Composite parent) {

			// main layout
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					false));

			// Create various fields
			// setMessage("Login to remote repository", IMessageProvider.NONE);
			name = createLT(composite, "Name", DEFAULT_NAME);
			uri = createLT(composite, "URI", DEFAULT_URI);

			final Button anonymousLogin = createLC(composite,
					"Log as anonymous", true);
			anonymousLogin.addSelectionListener(new SelectionListener() {
				private static final long serialVersionUID = 4874716406036981039L;

				public void widgetSelected(SelectionEvent e) {
					if (anonymousLogin.getSelection()) {
						username.setText(DEFAULT_USER_NAME);
						password.setText("");
						username.setEnabled(false);
						password.setEnabled(false);
					} else {
						username.setText("");
						password.setText("");
						username.setEnabled(true);
						password.setEnabled(true);
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			username = createLT(composite, "User", DEFAULT_USER_NAME);
			password = createLP(composite, "Password");
			saveInKeyring = createLC(composite, "Remember password", false);

			if (DEFAULT_ANONYMOUS) {
				username.setEnabled(false);
				password.setEnabled(false);
			}

			Button test = createButton(composite, "Test");
			GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2,
					1);
			gd.widthHint = 140;
			test.setLayoutData(gd);

			test.addSelectionListener(new SelectionAdapter() {
				private static final long serialVersionUID = -4034851916548656293L;

				public void widgetSelected(SelectionEvent arg0) {
					testConnection();
				}
			});

			// Compulsory
			setControl(composite);
		}

		/** Creates label and text. */
		protected Text createLT(Composite parent, String label, String initial) {
			new Label(parent, SWT.RIGHT).setText(label);
			Text text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
			text.setText(initial);
			return text;
		}

		/** Creates label and check. */
		protected Button createLC(Composite parent, String label,
				Boolean initial) {
			new Label(parent, SWT.RIGHT).setText(label);
			Button check = new Button(parent, SWT.CHECK);
			check.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			check.setSelection(initial);
			return check;
		}

		/** Creates a button with a label. */
		protected Button createButton(Composite parent, String label) {
			Button button = new Button(parent, SWT.PUSH);
			button.setText(label);
			button.setFont(JFaceResources.getDialogFont());
			setButtonLayoutData(button);
			return button;
		}

		/** Creates label and password field */
		protected Text createLP(Composite parent, String label) {
			new Label(parent, SWT.NONE).setText(label);
			Text text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER
					| SWT.PASSWORD);
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			return text;
		}

	}

	void testConnection() {
		Session session = null;
		try {
			if (uri.getText().startsWith("http")) {// http, https
				URI checkedUri = new URI(uri.getText());
				String checkedUriStr = checkedUri.toString();
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put(ArgeoJcrConstants.JCR_REPOSITORY_URI, checkedUriStr);
				Repository repository = ArgeoJcrUtils.getRepositoryByUri(
						repositoryFactory, checkedUriStr);
				if (username.getText().trim().equals("")) {// anonymous
					session = repository.login();
				} else {
					// FIXME use getTextChars() when upgrading to 3.7
					// see
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=297412
					char[] pwd = password.getText().toCharArray();
					SimpleCredentials sc = new SimpleCredentials(
							username.getText(), pwd);
					session = repository.login(sc);
				}
			} else {// alias
				Repository repository = ArgeoJcrUtils.getRepositoryByAlias(
						repositoryFactory, uri.getText());
				session = repository.login();
			}
			MessageDialog.openInformation(getShell(), "Success",
					"Connection to '" + uri.getText() + "' successful");
		} catch (Exception e) {
			ErrorFeedback
					.show("Connection test failed for " + uri.getText(), e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}
}