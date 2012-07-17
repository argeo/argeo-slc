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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.argeo.slc.web.ajaxplorer.AjxpAnswer;

public class FileDeleteAction<T extends FileDriver> extends FileAction {

	public AjxpAnswer execute(FileDriver driver, HttpServletRequest request) {
		Map<Object, Object> params = request.getParameterMap();
		for (Object paramKey : params.keySet()) {
			String param = paramKey.toString();
			log.debug("param=" + param + " (" + params.get(paramKey));
			if (param.length() < 4)
				continue;
			else {

				if (param.substring(0, 4).equals("file")) {
					String[] values = (String[]) params.get(paramKey);
					for (String path : values) {
						File file = driver.getFile(path);
						executeDelete((T) driver, file);
					}
				}
			}
		}

		return AjxpAnswer.DO_NOTHING;
	}

	protected void executeDelete(T driver, File file) {
		log.debug("Delete file " + file);
		file.delete();
	}

}
