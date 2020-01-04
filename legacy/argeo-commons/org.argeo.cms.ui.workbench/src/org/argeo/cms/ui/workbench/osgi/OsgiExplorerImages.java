/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
