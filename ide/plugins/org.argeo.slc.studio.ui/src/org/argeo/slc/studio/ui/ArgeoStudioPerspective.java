package org.argeo.slc.studio.ui;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

/**
 * The default perspective when opening ARgeo Studio, loosely based on PDE.
 */
public class ArgeoStudioPerspective implements IPerspectiveFactory {
	@Override
	public void createInitialLayout(IPageLayout factory) {
		IFolderLayout topLeft = factory.createFolder("topLeft", IPageLayout.LEFT, 0.25f, factory.getEditorArea());
		topLeft.addView(ProjectExplorer.VIEW_ID);

		IFolderLayout bottom = factory.createFolder("bottomRight", IPageLayout.BOTTOM, 0.75f, factory.getEditorArea());
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);

		IFolderLayout topRight = factory.createFolder("topRight", IPageLayout.RIGHT, 0.75f, factory.getEditorArea());
		topRight.addView(IPageLayout.ID_OUTLINE);
		topRight.addPlaceholder(JavaUI.ID_TYPE_HIERARCHY);

		factory.addNewWizardShortcut("org.eclipse.pde.ui.NewProjectWizard");
		factory.addNewWizardShortcut("org.eclipse.pde.ui.NewFeatureProjectWizard");
	}
}
