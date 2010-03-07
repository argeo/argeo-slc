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
