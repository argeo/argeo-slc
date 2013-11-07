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
package org.argeo.slc.akb.ui.providers;

import org.argeo.eclipse.ui.TreeParent;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** Basic content provider for a tree of AKB environment templates */
public class TemplatesTreeContentProvider implements ITreeContentProvider {

	/**
	 * @param parent
	 *            Pass current user home as parameter
	 * 
	 */
	public Object[] getElements(Object parent) {
		if (parent instanceof Object[])
			return (Object[]) parent;
		else
			return null;
	}

	public Object getParent(Object child) {
		return ((TreeParent) child).getParent();
	}

	public Object[] getChildren(Object parent) {
		return ((TreeParent) parent).getChildren();
	}

	public boolean hasChildren(Object parent) {
		return ((TreeParent) parent).hasChildren();
	}

	public void dispose() {
		// FIXME implement if needed
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}