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
package org.argeo.slc.client.ui.dist;

import org.argeo.slc.client.ui.dist.views.AnonymousDistributionsView;
import org.argeo.slc.client.ui.dist.views.HelpView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AnonymousDistributionPerspective implements IPerspectiveFactory {

	public final static String ID = DistPlugin.ID
			+ ".anonymousDistributionPerspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		IFolderLayout main = layout.createFolder("main", IPageLayout.LEFT,
				0.3f, editorArea);
		main.addView(AnonymousDistributionsView.ID);
		main.addView(HelpView.ID);
	}
}