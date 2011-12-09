package org.argeo.slc.client.rcp;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/** Implements initial perspective and saveAndRestore status of the workbench. */
public class SlcSecureWorkbenchAdvisor extends WorkbenchAdvisor {
	public final static String INITIAL_PERSPECTIVE_PROPERTY = "org.argeo.security.ui.initialPerspective";
	private String initialPerspective = System
			.getProperty(INITIAL_PERSPECTIVE_PROPERTY);
	private String username;

	public SlcSecureWorkbenchAdvisor(String username) {
		super();
		this.username = username;
	}

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new SlcSecureWorkbenchWindowAdvisor(configurer, username);
	}

	public String getInitialWindowPerspectiveId() {
		return initialPerspective;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		// To remember the user's layout and window size for the next time he
		// starts the application
		//configurer.setSaveAndRestore(true);
	}

}
