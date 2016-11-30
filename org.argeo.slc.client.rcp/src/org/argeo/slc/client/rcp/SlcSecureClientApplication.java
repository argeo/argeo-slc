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
package org.argeo.slc.client.rcp;

import org.argeo.cms.ui.workbench.rcp.SecureRcp;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.window.Window.IExceptionHandler;
import org.eclipse.ui.application.WorkbenchAdvisor;

/**
 * This class controls all aspects of the application's execution
 */
public class SlcSecureClientApplication extends SecureRcp {

	@Override
	protected WorkbenchAdvisor createWorkbenchAdvisor(String username) {
		Window.setExceptionHandler(new IExceptionHandler() {

			public void handleException(Throwable t) {
				System.err.println("Unexpected SLC UI exception: " + t);

			}
		});

		return new SlcSecureWorkbenchAdvisor(username);
	}

}
