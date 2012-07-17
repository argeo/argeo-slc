/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.client.ui.dist.commands;

import org.argeo.slc.client.ui.dist.views.ArtifactsBrowser;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Change visible state of the ArtifactBrower size column
 */
public class RefreshArtifactBrowser extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ArtifactsBrowser view = (ArtifactsBrowser) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.findView(ArtifactsBrowser.ID);
		view.refresh(null);
		return null;
	}
}
