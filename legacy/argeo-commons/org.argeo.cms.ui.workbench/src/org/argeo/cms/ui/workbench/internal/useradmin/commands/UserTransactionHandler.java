package org.argeo.cms.ui.workbench.internal.useradmin.commands;

import org.argeo.cms.CmsException;
import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.cms.ui.workbench.internal.useradmin.UiAdminUtils;
import org.argeo.cms.ui.workbench.internal.useradmin.UserAdminWrapper;
import org.argeo.osgi.transaction.WorkTransaction;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.osgi.service.useradmin.UserAdminEvent;

/** Manage the transaction that is bound to the current perspective */
public class UserTransactionHandler extends AbstractHandler {
	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID + ".userTransactionHandler";

	public final static String PARAM_COMMAND_ID = "param.commandId";

	public final static String TRANSACTION_BEGIN = "transaction.begin";
	public final static String TRANSACTION_COMMIT = "transaction.commit";
	public final static String TRANSACTION_ROLLBACK = "transaction.rollback";

	/* DEPENDENCY INJECTION */
	private UserAdminWrapper userAdminWrapper;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String commandId = event.getParameter(PARAM_COMMAND_ID);
		final WorkTransaction userTransaction = userAdminWrapper.getUserTransaction();
		try {
			if (TRANSACTION_BEGIN.equals(commandId)) {
				if (!userTransaction.isNoTransactionStatus())
					throw new CmsException("A transaction already exists");
				else
					userTransaction.begin();
			} else if (TRANSACTION_COMMIT.equals(commandId)) {
				if (userTransaction.isNoTransactionStatus())
					throw new CmsException("No transaction.");
				else
					userTransaction.commit();
			} else if (TRANSACTION_ROLLBACK.equals(commandId)) {
				if (userTransaction.isNoTransactionStatus())
					throw new CmsException("No transaction to rollback.");
				else {
					userTransaction.rollback();
					userAdminWrapper.notifyListeners(new UserAdminEvent(null, UserAdminEvent.ROLE_CHANGED, null));
				}
			}

			UiAdminUtils.notifyTransactionStateChange(userTransaction);
			// Try to remove invalid thread access errors when managing users.
			// HandlerUtil.getActivePart(event).getSite().getShell().getDisplay()
			// .asyncExec(new Runnable() {
			// @Override
			// public void run() {
			// UiAdminUtils
			// .notifyTransactionStateChange(userTransaction);
			// }
			// });

		} catch (CmsException e) {
			throw e;
		} catch (Exception e) {
			throw new CmsException("Unable to call " + commandId + " on " + userTransaction, e);
		}
		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setUserAdminWrapper(UserAdminWrapper userAdminWrapper) {
		this.userAdminWrapper = userAdminWrapper;
	}
}
