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

import javax.servlet.http.HttpServletRequest;

import org.argeo.slc.web.ajaxplorer.AjxpAnswer;

public class FileMkdirAction<T extends FileDriver> extends FileAction {

	public AjxpAnswer execute(FileDriver driver,
			HttpServletRequest request) {
		String dir = request.getParameter("dir");
		String dirName = request.getParameter("dirname");

		File newDir = driver.getFile(dir, dirName);
		newDir.mkdirs();

		postProcess((T)driver,newDir);

		return AjxpAnswer.DO_NOTHING;
	}

	protected void postProcess(T driver,File newDir) {

	}
}