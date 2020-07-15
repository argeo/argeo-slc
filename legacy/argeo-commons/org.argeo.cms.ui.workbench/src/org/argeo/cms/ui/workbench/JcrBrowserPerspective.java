package org.argeo.cms.ui.workbench;

import org.argeo.cms.ui.workbench.jcr.JcrBrowserView;
import org.argeo.cms.ui.workbench.jcr.NodeFsBrowserView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** Base perspective for the Jcr browser */
public class JcrBrowserPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		IFolderLayout upperLeft = layout.createFolder(WorkbenchUiPlugin.PLUGIN_ID + ".upperLeft", IPageLayout.LEFT,
				0.4f, layout.getEditorArea());
		upperLeft.addView(JcrBrowserView.ID);
		upperLeft.addView(NodeFsBrowserView.ID);
	}
}
