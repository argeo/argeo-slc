package org.argeo.cms.ui.workbench.osgi;

import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.eclipse.swt.graphics.Image;

/** Shared icons. */
public class OsgiExplorerImages {
	public final static Image INSTALLED = WorkbenchUiPlugin.getImageDescriptor(
			"icons/installed.gif").createImage();
	public final static Image RESOLVED = WorkbenchUiPlugin.getImageDescriptor(
			"icons/resolved.gif").createImage();
	public final static Image STARTING = WorkbenchUiPlugin.getImageDescriptor(
			"icons/starting.gif").createImage();
	public final static Image ACTIVE = WorkbenchUiPlugin.getImageDescriptor(
			"icons/active.gif").createImage();
	public final static Image SERVICE_PUBLISHED = WorkbenchUiPlugin
			.getImageDescriptor("icons/service_published.gif").createImage();
	public final static Image SERVICE_REFERENCED = WorkbenchUiPlugin
			.getImageDescriptor("icons/service_referenced.gif").createImage();
}
