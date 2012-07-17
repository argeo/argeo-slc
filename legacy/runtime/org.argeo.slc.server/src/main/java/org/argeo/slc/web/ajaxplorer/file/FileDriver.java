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
package org.argeo.slc.web.ajaxplorer.file;

import java.io.File;

import org.argeo.slc.web.ajaxplorer.SimpleAjxpDriver;

public class FileDriver extends SimpleAjxpDriver{
	private String basePath;
	private String encoding = "UTF-8";

	public String getBasePath() {
		return basePath;
	}
	
	public File getBaseDir(){
		return new File(getBasePath());
	}

	public void setBasePath(String basePath) {
		if (basePath.charAt(basePath.length() - 1) != File.separatorChar)
			basePath = basePath + File.separatorChar;
		this.basePath = basePath;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public File getFile(String relpath) {
		return new File(getBasePath() + relpath).getAbsoluteFile();
	}

	public File getFile(String dir, String fileName) {
		return getFile(dir + File.separator + fileName);
	}
}
