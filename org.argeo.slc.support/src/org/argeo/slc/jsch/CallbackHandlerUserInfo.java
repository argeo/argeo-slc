package org.argeo.slc.jsch;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

import org.argeo.slc.SlcException;

/** Retrieve a password or a passphrase using a standard callback handler. */
public final class CallbackHandlerUserInfo extends SimpleUserInfo {
	private CallbackHandler callbackHandler;

	private Boolean alwaysPrompt = false;

	public boolean promptPassphrase(String message) {
		if (passphrase != null)
			return true;

		if (!alwaysPrompt && passphraseSafe != null)
			return true;

		reset();
		PasswordCallback passwordCb = new PasswordCallback("SSH Passphrase",
				false);
		Callback[] dialogCbs = new Callback[] { passwordCb };
		try {
			callbackHandler.handle(dialogCbs);
			passphraseSafe = passwordCb.getPassword();
			return passphraseSafe != null;
		} catch (Exception e) {
			throw new SlcException("Cannot ask for a password", e);
		}
	}

	public boolean promptPassword(String message) {
		if (password != null)
			return true;

		if (!alwaysPrompt && passwordSafe != null)
			return true;

		reset();
		PasswordCallback passwordCb = new PasswordCallback("SSH Password",
				false);
		Callback[] dialogCbs = new Callback[] { passwordCb };
		try {
			callbackHandler.handle(dialogCbs);
			passwordSafe = passwordCb.getPassword();
			return passwordSafe != null;
		} catch (Exception e) {
			throw new SlcException("Cannot ask for a password", e);
		}
	}

	public void setAlwaysPrompt(Boolean alwaysPrompt) {
		this.alwaysPrompt = alwaysPrompt;
	}

	public void setCallbackHandler(CallbackHandler defaultCallbackHandler) {
		this.callbackHandler = defaultCallbackHandler;
	}

}
