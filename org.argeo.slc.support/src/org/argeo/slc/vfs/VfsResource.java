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
package org.argeo.slc.vfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.NameScope;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;

public class VfsResource implements Resource {
	private FileObject fileObject;

	public VfsResource(FileObject fileObject) {
		this.fileObject = fileObject;
	}

	public Resource createRelative(String relativePath) throws IOException {
		return new VfsResource(fileObject.resolveFile(relativePath,
				NameScope.DESCENDENT_OR_SELF));
	}

	public boolean exists() {
		try {
			return fileObject.exists();
		} catch (FileSystemException e) {
			throw new SlcException("Cannot find out whether " + fileObject
					+ " exists", e);
		}
	}

	public String getDescription() {
		return "VFS resource " + fileObject;
	}

	public File getFile() throws IOException {
		throw new IOException("Cannot access " + getDescription()
				+ " as a local file");
		// TODO: access local files
		// if(fileObject instanceof LocalFile){
		// ((LocalFile)fileObject).
		// }
		// return null;
	}

	public String getFilename() {
		return fileObject.getName().getBaseName();
	}

	public URI getURI() throws IOException {
		try {
			return new URI(fileObject.getName().getURI());
		} catch (URISyntaxException e) {
			throw new IOExceptionWithCause(e);
		}
	}

	public URL getURL() throws IOException {
		return fileObject.getURL();
	}

	public boolean isOpen() {
		return fileObject.isContentOpen();
	}

	public boolean isReadable() {
		try {
			return fileObject.isReadable();
		} catch (FileSystemException e) {
			throw new SlcException("Cannot find out whether " + fileObject
					+ " is readable", e);
		}
	}

	public long lastModified() throws IOException {
		return fileObject.getContent().getLastModifiedTime();
	}

	public InputStream getInputStream() throws IOException {
		return fileObject.getContent().getInputStream();
	}

	public FileObject getFileObject() {
		return fileObject;
	}

	public long contentLength(){
		return -1;
	}
}
