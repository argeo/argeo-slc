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
package org.argeo.slc.client.ui.model;

import org.argeo.eclipse.ui.TreeParent;

/**
 * Common base UI object to build result Tree.
 */

public abstract class ResultParent extends TreeParent {

	public ResultParent(String name) {
		super(name);
	}

	private boolean isPassed = true;

	protected synchronized void setPassed(boolean isPassed) {
		this.isPassed = isPassed;
	}

	public boolean isPassed() {
		return isPassed;
	}

	@Override
	public synchronized boolean hasChildren() {
		// only initialize when needed : correctly called by the jface framework
		if (!isLoaded())
			initialize();
		return super.hasChildren();
	}

	public void forceFullRefresh() {
		// if (isDisposed)
		// return;
		if (hasChildren())
			clearChildren();
		initialize();
	}

	public synchronized void dispose() {
		super.dispose();
	}

	protected abstract void initialize();
}
