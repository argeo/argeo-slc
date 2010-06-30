/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.detached.ui;

import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;

public abstract class UiPart {
	private boolean initialized = false;

	public synchronized final void init(DetachedContext context,
			DetachedRequest request) {
		initUi(context, request);
		initialized = true;
	}

	public synchronized final void reset(DetachedContext context,
			DetachedRequest request) {
		resetUi(context, request);
		initialized = false;
	}

	protected abstract void initUi(DetachedContext context,
			DetachedRequest request);

	protected void resetUi(DetachedContext context, DetachedRequest request) {

	}

	public synchronized boolean isInitialized() {
		return initialized;
	}

}
