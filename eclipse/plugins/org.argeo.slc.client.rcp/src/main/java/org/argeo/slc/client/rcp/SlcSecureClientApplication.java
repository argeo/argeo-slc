package org.argeo.slc.client.rcp;

import org.argeo.security.ui.application.SecureRcp;
import org.eclipse.ui.application.WorkbenchAdvisor;

/**
 * This class controls all aspects of the application's execution
 */
public class SlcSecureClientApplication extends SecureRcp {

	@Override
	protected WorkbenchAdvisor createWorkbenchAdvisor() {
		return new SlcSecureWorkbenchAdvisor();
	}

}
