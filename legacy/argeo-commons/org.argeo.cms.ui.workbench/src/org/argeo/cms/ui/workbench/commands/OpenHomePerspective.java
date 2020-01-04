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
package org.argeo.cms.ui.workbench.commands;

import org.argeo.cms.ui.workbench.UserHomePerspective;
import org.argeo.eclipse.ui.dialogs.ErrorFeedback;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

/** Default action of the user menu */
public class OpenHomePerspective extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		try {
			HandlerUtil.getActiveSite(event).getWorkbenchWindow()
					.openPage(UserHomePerspective.ID, null);
		} catch (WorkbenchException e) {
			ErrorFeedback.show("Cannot open home perspective", e);
		}
		return null;
	}
}
