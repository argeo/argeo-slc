package org.argeo.slc.client.rcp;

import org.argeo.security.ui.rcp.SecureRcp;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.window.Window.IExceptionHandler;
import org.eclipse.ui.application.WorkbenchAdvisor;

/**
 * This class controls all aspects of the application's execution
 */
public class SlcSecureClientApplication extends SecureRcp {

	@Override
	protected WorkbenchAdvisor createWorkbenchAdvisor(String username) {
		Window.setExceptionHandler(new IExceptionHandler() {

			public void handleException(Throwable t) {
				System.err.println("Unexpected SLC UI exception: " + t);

			}
		});

		return new SlcSecureWorkbenchAdvisor(username);
	}

}
