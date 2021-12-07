package org.argeo.cms.ui.workbench.internal.useradmin.providers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.cms.CmsException;
import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.osgi.transaction.WorkTransaction;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

/** Observe and notify UI on UserTransaction state changes */
public class UserTransactionProvider extends AbstractSourceProvider {
	private final static Log log = LogFactory
			.getLog(UserTransactionProvider.class);

	public final static String TRANSACTION_STATE = WorkbenchUiPlugin.PLUGIN_ID
			+ ".userTransactionState";
	public final static String STATUS_ACTIVE = "status.active";
	public final static String STATUS_NO_TRANSACTION = "status.noTransaction";

	/* DEPENDENCY INJECTION */
	private WorkTransaction userTransaction;

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { TRANSACTION_STATE };
	}

	@Override
	public Map<String, String> getCurrentState() {
		Map<String, String> currentState = new HashMap<String, String>(1);
		currentState.put(TRANSACTION_STATE, getInternalCurrentState());
		return currentState;
	}

	@Override
	public void dispose() {
	}

	private String getInternalCurrentState() {
		try {
			String transactionState;
			if (userTransaction.isNoTransactionStatus())
				transactionState = STATUS_NO_TRANSACTION;
			else
				// if (userTransaction.getStatus() == Status.STATUS_ACTIVE)
				transactionState = STATUS_ACTIVE;
			return transactionState;
		} catch (Exception e) {
			throw new CmsException("Unable to begin transaction", e);
		}
	}

	/** Publishes the ability to notify a state change */
	public void fireTransactionStateChange() {
		try {
			fireSourceChanged(ISources.WORKBENCH, TRANSACTION_STATE,
					getInternalCurrentState());
		} catch (Exception e) {
			log.warn("Cannot fire transaction state change event. Caught exception: "
					+ e.getClass().getCanonicalName() + " - " + e.getMessage());
		}
	}

	/* DEPENDENCY INJECTION */
	public void setUserTransaction(WorkTransaction userTransaction) {
		this.userTransaction = userTransaction;
	}
}
