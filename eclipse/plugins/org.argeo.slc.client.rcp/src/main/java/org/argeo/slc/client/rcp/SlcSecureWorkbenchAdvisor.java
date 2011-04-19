package org.argeo.slc.client.rcp;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * implements initial perspective and saveAndRestore status of the workbench.
 * 
 * @author bsinou
 * 
 */
public class SlcSecureWorkbenchAdvisor extends WorkbenchAdvisor {
	// private static final String PERSPECTIVE_ID =
	// "org.argeo.slc.client.ui.perspectives.slcExecution";
	static final String DEFAULT_PERSPECTIVE_ID = "org.argeo.security.ui.securityPerspective"; //$NON-NLS-1$

	public final static String INITIAL_PERSPECTIVE_PROPERTY = "org.argeo.security.ui.initialPerspective";
	private String initialPerspective = System.getProperty(
			INITIAL_PERSPECTIVE_PROPERTY, DEFAULT_PERSPECTIVE_ID);
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
		// return PERSPECTIVE_ID;
		return initialPerspective;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		// To remember the user's layout and window size for the next time he
		// starts the application
		// configurer.setSaveAndRestore(true);
	}
	
	
}
