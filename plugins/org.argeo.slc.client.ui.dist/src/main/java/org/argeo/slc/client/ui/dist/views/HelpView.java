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
package org.argeo.slc.client.ui.dist.views;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * Displays some info about the distribution
 * 
 */
public class HelpView extends ViewPart {
	public final static String ID = DistPlugin.ID + ".helpView";

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		Browser browser = new Browser(parent, SWT.NONE);
		browser.setUrl("http://www.argeo.org");
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	}

	/**
	 * Force refresh of the whole view
	 */
	public void refresh() {
	}

	@Override
	public void setFocus() {
	}

}