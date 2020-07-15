package org.argeo.cms.ui.workbench.internal.jcr.commands;

import java.net.URI;
import java.util.Hashtable;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.argeo.api.NodeConstants;
import org.argeo.api.NodeUtils;
import org.argeo.api.security.Keyring;
import org.argeo.cms.ArgeoNames;
import org.argeo.cms.ArgeoTypes;
import org.argeo.cms.ui.workbench.internal.WorkbenchConstants;
import org.argeo.cms.ui.workbench.util.CommandUtils;
import org.argeo.eclipse.ui.EclipseUiException;
import org.argeo.eclipse.ui.dialogs.ErrorFeedback;
import org.argeo.jcr.JcrUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Connect to a remote repository and, if successful publish it as an OSGi
 * service.
 */
public class AddRemoteRepository extends AbstractHandler implements WorkbenchConstants, ArgeoNames {

	private RepositoryFactory repositoryFactory;
	private Repository nodeRepository;
	private Keyring keyring;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		RemoteRepositoryLoginDialog dlg = new RemoteRepositoryLoginDialog(Display.getDefault().getActiveShell());
		if (dlg.open() == Dialog.OK) {
			CommandUtils.callCommand(Refresh.ID);
		}
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

	class RemoteRepositoryLoginDialog extends TitleAreaDialog {
		private static final long serialVersionUID = 2234006887750103399L;
		private Text name;
		private Text uri;
		private Text username;
		private Text password;
		private Button saveInKeyring;

		public RemoteRepositoryLoginDialog(Shell parentShell) {
			super(parentShell);
		}

		protected Point getInitialSize() {
			return new Point(600, 400);
		}

		protected Control createDialogArea(Composite parent) {
			Composite dialogarea = (Composite) super.createDialogArea(parent);
			dialogarea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			Composite composite = new Composite(dialogarea, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			setMessage("Login to remote repository", IMessageProvider.NONE);
			name = createLT(composite, "Name", "remoteRepository");
			uri = createLT(composite, "URI", "http://localhost:7070/jcr/node");
			username = createLT(composite, "User", "");
			password = createLP(composite, "Password");

			saveInKeyring = createLC(composite, "Remember password", false);
			parent.pack();
			return composite;
		}

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			Button test = createButton(parent, 2, "Test", false);
			test.addSelectionListener(new SelectionAdapter() {
				private static final long serialVersionUID = -1829962269440419560L;

				public void widgetSelected(SelectionEvent arg0) {
					testConnection();
				}
			});
		}

		void testConnection() {
			Session session = null;
			try {
				URI checkedUri = new URI(uri.getText());
				String checkedUriStr = checkedUri.toString();

				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put(NodeConstants.LABELED_URI, checkedUriStr);
				Repository repository = repositoryFactory.getRepository(params);
				if (username.getText().trim().equals("")) {// anonymous
					// FIXME make it more generic
					session = repository.login("main");
				} else {
					// FIXME use getTextChars() when upgrading to 3.7
					// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=297412
					char[] pwd = password.getText().toCharArray();
					SimpleCredentials sc = new SimpleCredentials(username.getText(), pwd);
					session = repository.login(sc, "main");
					MessageDialog.openInformation(getParentShell(), "Success",
							"Connection to '" + uri.getText() + "' successful");
				}
			} catch (Exception e) {
				ErrorFeedback.show("Connection test failed for " + uri.getText(), e);
			} finally {
				JcrUtils.logoutQuietly(session);
			}
		}

		@Override
		protected void okPressed() {
			Session nodeSession = null;
			try {
				nodeSession = nodeRepository.login();
				Node home = NodeUtils.getUserHome(nodeSession);

				Node remote = home.hasNode(ARGEO_REMOTE) ? home.getNode(ARGEO_REMOTE) : home.addNode(ARGEO_REMOTE);
				if (remote.hasNode(name.getText()))
					throw new EclipseUiException("There is already a remote repository named " + name.getText());
				Node remoteRepository = remote.addNode(name.getText(), ArgeoTypes.ARGEO_REMOTE_REPOSITORY);
				remoteRepository.setProperty(ARGEO_URI, uri.getText());
				remoteRepository.setProperty(ARGEO_USER_ID, username.getText());
				nodeSession.save();
				if (saveInKeyring.getSelection()) {
					String pwdPath = remoteRepository.getPath() + '/' + ARGEO_PASSWORD;
					keyring.set(pwdPath, password.getText().toCharArray());
				}
				nodeSession.save();
				MessageDialog.openInformation(getParentShell(), "Repository Added",
						"Remote repository '" + username.getText() + "@" + uri.getText() + "' added");

				super.okPressed();
			} catch (Exception e) {
				ErrorFeedback.show("Cannot add remote repository", e);
			} finally {
				JcrUtils.logoutQuietly(nodeSession);
			}
		}

		/** Creates label and text. */
		protected Text createLT(Composite parent, String label, String initial) {
			new Label(parent, SWT.NONE).setText(label);
			Text text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			text.setText(initial);
			return text;
		}

		/** Creates label and check. */
		protected Button createLC(Composite parent, String label, Boolean initial) {
			new Label(parent, SWT.NONE).setText(label);
			Button check = new Button(parent, SWT.CHECK);
			check.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			check.setSelection(initial);
			return check;
		}

		protected Text createLP(Composite parent, String label) {
			new Label(parent, SWT.NONE).setText(label);
			Text text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER | SWT.PASSWORD);
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			return text;
		}
	}
}
