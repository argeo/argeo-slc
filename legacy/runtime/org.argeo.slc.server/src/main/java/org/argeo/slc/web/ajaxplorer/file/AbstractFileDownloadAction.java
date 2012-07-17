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
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.argeo.slc.web.ajaxplorer.AjxpAnswer;
import org.argeo.slc.web.ajaxplorer.AjxpDriverException;

public abstract class AbstractFileDownloadAction extends FileAction {
	public AjxpAnswer execute(FileDriver driver, HttpServletRequest request) {
		String fileStr = request.getParameter(getFileParameter());
		if (fileStr == null) {
			throw new AjxpDriverException(
					"A  file to download needs to be provided.");
		}
		File file = new File(driver.getBasePath() + fileStr);
		return new AxpBasicDownloadAnswer(file);
	}

	/** Return 'file' by default. */
	protected String getFileParameter() {
		return "file";
	}

	/** To be overridden. Do nothing by default. */
	protected void setHeaders(HttpServletResponse response, File file) {
		// do nothing
	}

	protected class AxpBasicDownloadAnswer implements AjxpAnswer {
		private final File file;

		public AxpBasicDownloadAnswer(File file) {
			this.file = file;
		}

		public void updateResponse(HttpServletResponse response) {
			InputStream in = null;
			OutputStream out = null;
			try {
				setHeaders(response, file);

				if (log.isDebugEnabled())
					log.debug("Download file " + file);
				in = FileUtils.openInputStream(file);
				out = response.getOutputStream();

				copyFile(in, out);
				out.flush();

			} catch (Exception e) {
				e.printStackTrace();
				throw new AjxpDriverException("Cannot download file " + file, e);
			} finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}

		}

		protected void copyFile(InputStream in, OutputStream out)
				throws Exception {
			IOUtils.copy(in, out);
		}

	}
}
