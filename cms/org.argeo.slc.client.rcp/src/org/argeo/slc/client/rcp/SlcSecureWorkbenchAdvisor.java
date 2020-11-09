package org.argeo.slc.client.rcp;

import org.argeo.cms.ui.workbench.rcp.SecureWorkbenchAdvisor;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Custom {@link SecureWorkbenchAdvisor} in order to create a
 * {@link SlcSecureWorkbenchWindowAdvisor}.
 */
public class SlcSecureWorkbenchAdvisor extends SecureWorkbenchAdvisor {
	public SlcSecureWorkbenchAdvisor(String username) {
		super(username);
	}

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new SlcSecureWorkbenchWindowAdvisor(configurer, getUsername());
	}
}
