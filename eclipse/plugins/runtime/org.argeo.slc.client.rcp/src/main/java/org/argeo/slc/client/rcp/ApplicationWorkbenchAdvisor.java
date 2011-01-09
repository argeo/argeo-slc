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
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "org.argeo.slc.client.ui.perspectives.slcExecution";
//	private static final String PERSPECTIVE_ID = "org.argeo.slc.client.ui.dist.distributionPerspective";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);

		// To remember the user's layout and window size for the next time he
		// starts the application
		// configurer.setSaveAndRestore(true);
	}

}
