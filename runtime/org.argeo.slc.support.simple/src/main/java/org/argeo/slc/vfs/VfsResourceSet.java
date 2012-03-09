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
package org.argeo.slc.vfs;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.deploy.ResourceSet;
import org.springframework.core.io.Resource;

public class VfsResourceSet implements ResourceSet {
	private String base;

	public Map<String, Resource> listResources() {
		try {
			FileSystemManager fileSystemManager = VFS.getManager();
			FileObject fileObject = fileSystemManager.resolveFile(base);
			Map<String, Resource> map = new HashMap<String, Resource>();
			addToMap(map, "", fileObject);

			// TODO: add filters
			return map;
		} catch (FileSystemException e) {
			throw new SlcException("Cannot list VFS resources from " + base, e);
		}
	}

	/** recursive */
	protected void addToMap(Map<String, Resource> map, String parentPath,
			FileObject fileObject) {
		try {
			String newParentPath = parentPath
					+ fileObject.getName().getBaseName() + '/';
			if (fileObject.getType().hasChildren()) {
				for (FileObject child : fileObject.getChildren()) {
					addToMap(map, newParentPath, child);
				}
			} else {
				map.put(parentPath + fileObject.getName().getBaseName(),
						new VfsResource(fileObject));
			}
		} catch (FileSystemException e) {
			throw new SlcException("Cannot add children from " + parentPath, e);
		}
	}

	public void setBase(String base) {
		this.base = base;
	}

}
