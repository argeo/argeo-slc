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

/**
 * UI Tree component. Virtual folder to list either other folders and/or a list
 * of results. Keeps a reference to its parent that might be null if the .
 */
public class VirtualFolder extends ResultParent {
	ResultParent[] children;

	public VirtualFolder(VirtualFolder parent, ResultParent[] children,
			String name) {
		super(name);
		setParent(parent);
		this.children = children;
	}

	@Override
	public synchronized void dispose() {
		super.dispose();
	}

	@Override
	protected void initialize() {
		if (children != null)
			for (ResultParent child : children)
				addChild(child);
	}

	public void resetChildren(ResultParent[] children) {
		clearChildren();
		this.children = children;
		initialize();
	}
}