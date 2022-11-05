package org.argeo.cms.e4.users;

import org.argeo.api.cms.transaction.WorkTransaction;

/** First effort to centralize back end methods used by the user admin UI */
public class UiAdminUtils {
	/*
	 * INTERNAL METHODS: Below methods are meant to stay here and are not part
	 * of a potential generic backend to manage the useradmin
	 */
	/** Easily notify the ActiveWindow that the transaction had a state change */
	public final static void notifyTransactionStateChange(
			WorkTransaction userTransaction) {
//		try {
//			IWorkbenchWindow aww = PlatformUI.getWorkbench()
//					.getActiveWorkbenchWindow();
//			ISourceProviderService sourceProviderService = (ISourceProviderService) aww
//					.getService(ISourceProviderService.class);
//			UserTransactionProvider esp = (UserTransactionProvider) sourceProviderService
//					.getSourceProvider(UserTransactionProvider.TRANSACTION_STATE);
//			esp.fireTransactionStateChange();
//		} catch (Exception e) {
//			throw new CmsException("Unable to begin transaction", e);
//		}
	}

	/**
	 * Email addresses must match this regexp pattern ({@value #EMAIL_PATTERN}.
	 * Thanks to <a href=
	 * "http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/"
	 * >this tip</a>.
	 */
	public final static String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
}
