package org.argeo.slc.jsch;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class UiUserInfo extends SimpleUserInfo {

	private Boolean alwaysPrompt = false;

	public boolean promptPassphrase(String message) {
		if (passphrase != null)
			return true;

		if (!alwaysPrompt && passphraseSafe != null)
			return true;

		PasswordDialog dialog = new PasswordDialog(message) {
			private static final long serialVersionUID = 3266299327166418364L;

			@Override
			protected void useCredentials(char[] password) {
				passphraseSafe = new char[password.length];
				System.arraycopy(password, 0, passphraseSafe, 0,
						password.length);
				// passphraseSafe = Arrays.copyOf(password, password.length);
			}
		};
		dialog.setVisible(true);
		return dialog.getWasProvided();
	}

	public boolean promptPassword(String message) {
		if (password != null)
			return true;

		if (!alwaysPrompt && passwordSafe != null)
			return true;

		PasswordDialog dialog = new PasswordDialog(message) {
			private static final long serialVersionUID = 3266299327166418364L;

			@Override
			protected void useCredentials(char[] password) {
				// passwordSafe = Arrays.copyOf(password, password.length);
				passwordSafe = new char[password.length];
				System.arraycopy(password, 0, passwordSafe, 0, password.length);
			}
		};
		dialog.setVisible(true);
		return dialog.getWasProvided();
	}

	public void setAlwaysPrompt(Boolean alwaysPrompt) {
		this.alwaysPrompt = alwaysPrompt;
	}

	protected static class PasswordDialog extends JDialog implements
			ActionListener {
		private static final long serialVersionUID = 3399155607980846207L;

		private static final String OK = "ok";

		private JPasswordField password = new JPasswordField("", 10);

		private JButton okButton;
		private JButton cancelButton;

		private Boolean wasProvided = false;

		public PasswordDialog(String title) {
			setTitle(title);
			setModal(true);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			JPanel p1 = new JPanel(new GridLayout(1, 2, 3, 3));
			p1.add(new JLabel("Password"));
			password.setActionCommand(OK);
			password.addActionListener(this);
			p1.add(password);
			add("Center", p1);

			Panel p2 = new Panel();
			okButton = addButton(p2, "OK");
			okButton.setActionCommand(OK);
			cancelButton = addButton(p2, "Cancel");
			add("South", p2);
			setSize(240, 120);

			pack();
		}

		/** To be overridden */
		protected void useCredentials(char[] password) {
			// does nothing
		}

		private JButton addButton(Container c, String name) {
			JButton button = new JButton(name);
			button.addActionListener(this);
			c.add(button);
			return button;
		}

		public final void actionPerformed(ActionEvent evt) {
			Object source = evt.getSource();
			if (source == okButton || evt.getActionCommand().equals(OK)) {
				char[] p = password.getPassword();
				useCredentials(p);
				wasProvided = true;
				Arrays.fill(p, '0');
				cleanUp();
			} else if (source == cancelButton)
				cleanUp();
		}

		private void cleanUp() {
			password.setText("");
			dispose();
		}

		public Boolean getWasProvided() {
			return wasProvided;
		}

	}

}
