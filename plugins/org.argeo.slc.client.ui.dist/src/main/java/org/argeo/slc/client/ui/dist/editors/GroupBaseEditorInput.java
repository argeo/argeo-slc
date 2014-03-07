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
package org.argeo.slc.client.ui.dist.editors;

import org.argeo.slc.SlcException;

/**
 * An editor input pointing to a given group base in a distribution workspace
 */
public class GroupBaseEditorInput extends DistWkspEditorInput {

	private final String groupBaseId;

	/** uri, workspace name and group base Id cannot be null */
	public GroupBaseEditorInput(String repoNodePath, String uri,
			String workspaceName, String groupBaseId) {
		super(repoNodePath, uri, workspaceName);
		if (groupBaseId == null)
			throw new SlcException("Group base ID cannot be null");
		this.groupBaseId = groupBaseId;
	}

	public String getToolTipText() {
		return "Editor for group base of ID " + groupBaseId + " in workspace "
				+ getWorkspaceName() + " in " + getUri();
	}

	public String getName() {
		return groupBaseId;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GroupBaseEditorInput))
			return false;

		GroupBaseEditorInput other = (GroupBaseEditorInput) obj;

		if (groupBaseId.equals(other.getGroupBaseId()))
			return super.equals(obj);
		else
			return false;
	}

	public String getGroupBaseId() {
		return groupBaseId;
	}

}