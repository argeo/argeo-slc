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
package org.argeo.slc.client.rap;

import org.argeo.security.ui.rap.RapWindowAdvisor;
import org.argeo.security.ui.rap.RapWorkbenchAdvisor;
import org.argeo.security.ui.rap.SecureEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * This class controls all aspects of the application's execution and is
 * contributed through the plugin.xml.
 */
public class SlcSecureRap extends SecureEntryPoint {

	@Override
	protected RapWorkbenchAdvisor createRapWorkbenchAdvisor(
			final String username) {
		return new RapWorkbenchAdvisor(username) {
			@Override
			public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
					IWorkbenchWindowConfigurer configurer) {
				return new SlcRapWorkbenchWindowAdvisor(configurer, username);
			}

		};
	}

	/** Workaround for resize issue */
	class SlcRapWorkbenchWindowAdvisor extends RapWindowAdvisor {

		public SlcRapWorkbenchWindowAdvisor(
				IWorkbenchWindowConfigurer configurer, String username) {
			super(configurer, username);
		}

		public void preWindowOpen() {
			IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
			configurer.setShowCoolBar(true);
			configurer.setShowMenuBar(false);
			configurer.setShowStatusLine(false);
			configurer.setShowPerspectiveBar(true);
			configurer.setTitle("Argeo Secure UI"); //$NON-NLS-1$
			// Full screen, see
			// http://dev.eclipse.org/newslists/news.eclipse.technology.rap/msg02697.html
			configurer.setShellStyle(SWT.NO_TRIM);
			Rectangle bounds = Display.getCurrent().getBounds();
			configurer.setInitialSize(new Point(bounds.width, bounds.height));
		}

		@Override
		public void postWindowCreate() {
			Shell shell = getWindowConfigurer().getWindow().getShell();
			shell.setMaximized(true);
		}

		@Override
		public void postWindowOpen() {
			String defaultPerspective = getWindowConfigurer()
					.getWorkbenchConfigurer().getWorkbench()
					.getPerspectiveRegistry().getDefaultPerspective();
			if (defaultPerspective == null) {
				IWorkbenchWindow window = getWindowConfigurer().getWindow();
				if (window == null)
					return;

				IWorkbenchAction openPerspectiveDialogAction = ActionFactory.OPEN_PERSPECTIVE_DIALOG
						.create(window);
				openPerspectiveDialogAction.run();
			}
		}

	}
}
