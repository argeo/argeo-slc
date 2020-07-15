package org.argeo.cms.ui.workbench.internal.useradmin;

import static org.argeo.cms.ui.workbench.WorkbenchUiPlugin.getImageDescriptor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/** Shared icons that must be declared programmatically . */
public class SecurityAdminImages {
	private final static String PREFIX = "icons/";

	public final static ImageDescriptor ICON_REMOVE_DESC = getImageDescriptor(PREFIX + "delete.png");
	public final static ImageDescriptor ICON_USER_DESC = getImageDescriptor(PREFIX + "person.png");

	public final static Image ICON_USER = ICON_USER_DESC.createImage();
	public final static Image ICON_GROUP = getImageDescriptor(PREFIX + "group.png").createImage();
	public final static Image ICON_WORKGROUP = getImageDescriptor(PREFIX + "workgroup.png").createImage();
	public final static Image ICON_ROLE = getImageDescriptor(PREFIX + "role.gif").createImage();

}
