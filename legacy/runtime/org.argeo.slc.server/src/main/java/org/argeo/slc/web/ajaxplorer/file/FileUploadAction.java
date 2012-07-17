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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.web.ajaxplorer.AjxpAnswer;
import org.argeo.slc.web.ajaxplorer.AjxpDriverException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public class FileUploadAction<T extends FileDriver> extends FileAction {

	public AjxpAnswer execute(FileDriver driver, HttpServletRequest request) {
		if (!(request instanceof MultipartHttpServletRequest)) {
			throw new AjxpDriverException(
					"Cann only deal with MultipartHttpServletRequest");
		}
		MultipartHttpServletRequest mpr = (MultipartHttpServletRequest) request;
		String dir = mpr.getParameter("dir");
		String fileName = mpr.getParameter("Filename");

		InputStream in = null;
		OutputStream out = null;
		File file = null;
		try {
			MultipartFile mpfile = mpr.getFile("Filedata");
			in = mpfile.getInputStream();
			file = driver.getFile(dir, fileName);
			out = new FileOutputStream(file);
			IOUtils.copy(in, out);
		} catch (IOException e) {
			throw new AjxpDriverException("Cannot upload file.", e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		postProcess((T)driver, file);

		return AjxpAnswer.DO_NOTHING;
	}

	protected void postProcess(T driver,File file) {

	}

}
