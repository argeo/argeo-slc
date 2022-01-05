package org.argeo.cms.ui.workbench.internal.useradmin;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.argeo.api.cms.CmsConstants;
import org.argeo.cms.CmsException;
import org.argeo.osgi.transaction.WorkTransaction;
import org.argeo.osgi.useradmin.UserAdminConf;
import org.osgi.framework.ServiceReference;
import org.osgi.service.useradmin.UserAdmin;
import org.osgi.service.useradmin.UserAdminEvent;
import org.osgi.service.useradmin.UserAdminListener;

/** Centralise interaction with the UserAdmin in this bundle */
public class UserAdminWrapper {

	private UserAdmin userAdmin;
	private ServiceReference<UserAdmin> userAdminServiceReference;
	private WorkTransaction userTransaction;

	// First effort to simplify UX while managing users and groups
	public final static boolean COMMIT_ON_SAVE = true;

	// Registered listeners
	List<UserAdminListener> listeners = new ArrayList<UserAdminListener>();

	/**
	 * Starts a transaction if necessary. Should always been called together
	 * with {@link UserAdminWrapper#commitOrNotifyTransactionStateChange()} once
	 * the security model changes have been performed.
	 */
	public WorkTransaction beginTransactionIfNeeded() {
		try {
			// UserTransaction userTransaction = getUserTransaction();
			if (userTransaction.isNoTransactionStatus()) {
				userTransaction.begin();
				// UiAdminUtils.notifyTransactionStateChange(userTransaction);
			}
			return userTransaction;
		} catch (Exception e) {
			throw new CmsException("Unable to begin transaction", e);
		}
	}

	/**
	 * Depending on the current application configuration, it will either commit
	 * the current transaction or throw a notification that the transaction
	 * state has changed (In the later case, it must be called from the UI
	 * thread).
	 */
	public void commitOrNotifyTransactionStateChange() {
		try {
			// UserTransaction userTransaction = getUserTransaction();
			if (userTransaction.isNoTransactionStatus())
				return;

			if (UserAdminWrapper.COMMIT_ON_SAVE)
				userTransaction.commit();
			else
				UiAdminUtils.notifyTransactionStateChange(userTransaction);
		} catch (Exception e) {
			throw new CmsException("Unable to clean transaction", e);
		}
	}

	// TODO implement safer mechanism
	public void addListener(UserAdminListener userAdminListener) {
		if (!listeners.contains(userAdminListener))
			listeners.add(userAdminListener);
	}

	public void removeListener(UserAdminListener userAdminListener) {
		if (listeners.contains(userAdminListener))
			listeners.remove(userAdminListener);
	}

	public void notifyListeners(UserAdminEvent event) {
		for (UserAdminListener listener : listeners)
			listener.roleChanged(event);
	}

	public Map<String, String> getKnownBaseDns(boolean onlyWritable) {
		Map<String, String> dns = new HashMap<String, String>();
		for (String uri : userAdminServiceReference.getPropertyKeys()) {
			if (!uri.startsWith("/"))
				continue;
			Dictionary<String, ?> props = UserAdminConf.uriAsProperties(uri);
			String readOnly = UserAdminConf.readOnly.getValue(props);
			String baseDn = UserAdminConf.baseDn.getValue(props);

			if (onlyWritable && "true".equals(readOnly))
				continue;
			if (baseDn.equalsIgnoreCase(CmsConstants.ROLES_BASEDN))
				continue;
			dns.put(baseDn, uri);
		}
		return dns;
	}

	public UserAdmin getUserAdmin() {
		return userAdmin;
	}

	public WorkTransaction getUserTransaction() {
		return userTransaction;
	}

	/* DEPENDENCY INJECTION */
	public void setUserAdmin(UserAdmin userAdmin) {
		this.userAdmin = userAdmin;
	}

	public void setUserTransaction(WorkTransaction userTransaction) {
		this.userTransaction = userTransaction;
	}

	public void setUserAdminServiceReference(
			ServiceReference<UserAdmin> userAdminServiceReference) {
		this.userAdminServiceReference = userAdminServiceReference;
	}
}
