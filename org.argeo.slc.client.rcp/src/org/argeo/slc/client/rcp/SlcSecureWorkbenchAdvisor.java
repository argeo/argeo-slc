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

import org.argeo.cms.ui.workbench.rcp.SecureWorkbenchAdvisor;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Custom {@link SecureWorkbenchAdvisor} in order to create a
 * {@link SlcSecureWorkbenchWindowAdvisor}.
 */
public class SlcSecureWorkbenchAdvisor extends SecureWorkbenchAdvisor {
	public SlcSecureWorkbenchAdvisor(String username) {
		super(username);
	}

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new SlcSecureWorkbenchWindowAdvisor(configurer, getUsername());
	}
}
