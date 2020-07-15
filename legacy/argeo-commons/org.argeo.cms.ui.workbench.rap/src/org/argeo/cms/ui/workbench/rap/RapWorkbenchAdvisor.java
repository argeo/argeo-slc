package org.argeo.cms.ui.workbench.rap;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/** Eclipse RAP specific workbench advisor */
public class RapWorkbenchAdvisor extends WorkbenchAdvisor {
	public final static String INITIAL_PERSPECTIVE_PROPERTY = "org.argeo.security.ui.initialPerspective";
	public final static String SAVE_AND_RESTORE_PROPERTY = "org.argeo.security.ui.saveAndRestore";

	private String initialPerspective = System.getProperty(
			INITIAL_PERSPECTIVE_PROPERTY, null);

	private String username;

	public RapWorkbenchAdvisor(String username) {
		this.username = username;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		Boolean saveAndRestore = Boolean.parseBoolean(System.getProperty(
				SAVE_AND_RESTORE_PROPERTY, "false"));
		configurer.setSaveAndRestore(saveAndRestore);
	}

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new RapWindowAdvisor(configurer, username);
	}

	public String getInitialWindowPerspectiveId() {
		if (initialPerspective != null) {
			// check whether this user can see the declared perspective
			// (typically the perspective won't be listed if this user doesn't
			// have the right to see it)
			IPerspectiveDescriptor pd = getWorkbenchConfigurer().getWorkbench()
					.getPerspectiveRegistry()
					.findPerspectiveWithId(initialPerspective);
			if (pd == null)
				return null;
		}
		return initialPerspective;
	}
}
