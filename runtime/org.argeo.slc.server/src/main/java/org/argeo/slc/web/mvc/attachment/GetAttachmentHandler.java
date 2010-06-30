/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.web.mvc.attachment;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.argeo.slc.core.attachment.AttachmentsStorage;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.springframework.web.HttpRequestHandler;

/** Returns one single result. */
public class GetAttachmentHandler implements HttpRequestHandler {
	protected final String FORCE_DOWNLOAD = "Content-Type: application/force-download";

	private AttachmentsStorage attachmentsStorage;

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uuid = request.getParameter("uuid");
		String contentType = request.getParameter("contentType");
		String name = request.getParameter("name");
		if (contentType == null || "".equals(contentType.trim())) {
			if (name != null) {
				contentType = FORCE_DOWNLOAD;
				String ext = FilenameUtils.getExtension(name);
				// cf. http://en.wikipedia.org/wiki/Internet_media_type
				if ("csv".equals(ext))
					contentType = "text/csv";
				else if ("pdf".equals(ext))
					contentType = "application/pdf";
				else if ("zip".equals(ext))
					contentType = "application/zip";
				else if ("html".equals(ext))
					contentType = "application/html";
				else if ("txt".equals(ext))
					contentType = "text/plain";
				else if ("doc".equals(ext) || "docx".equals(ext))
					contentType = "application/msword";
				else if ("xls".equals(ext) || "xlsx".equals(ext))
					contentType = "application/vnd.ms-excel";
				else if ("xml".equals(ext))
					contentType = "text/xml";
			}
		}

		if (name != null) {
			contentType = contentType + ";name=\"" + name + "\"";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ name + "\"");
		}
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");

		SimpleAttachment resourceDescriptor = new SimpleAttachment();
		resourceDescriptor.setUuid(uuid);
		resourceDescriptor.setContentType(contentType);

		response.setContentType(contentType);
		ServletOutputStream outputStream = response.getOutputStream();
		attachmentsStorage.retrieveAttachment(resourceDescriptor, outputStream);
	}

	public void setAttachmentsStorage(AttachmentsStorage attachmentsStorage) {
		this.attachmentsStorage = attachmentsStorage;
	}

}
