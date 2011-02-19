package org.argeo.slc.client.rap;

import org.argeo.security.ui.application.SecureRap;
import org.argeo.security.ui.application.SecureWorkbenchAdvisor;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * This class controls all aspects of the application's execution and is
 * contributed through the plugin.xml.
 */
public class SlcSecureRap extends SecureRap {


	@Override
	protected WorkbenchAdvisor createWorkbenchAdvisor() {
		return new SecureWorkbenchAdvisor() {
			public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
					IWorkbenchWindowConfigurer configurer) {
				return new SlcRapSecureWorkbenchWindowAdvisor(configurer);
			}

		};
	}
}