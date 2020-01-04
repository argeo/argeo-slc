/*
 * Argeo Connect - Data management and communications
 * Copyright (C) 2012 Argeo GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with software covered by the terms of the Eclipse Public License, the
 * licensors of this Program grant you additional permission to convey the
 * resulting work. Corresponding Source for a non-source form of such a
 * combination shall include the source code for the parts of such software
 * which are used as well as that of the covered work.
 */
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
