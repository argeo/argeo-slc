package org.argeo.slc.web.mvc.attachment;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
			contentType = FORCE_DOWNLOAD;
		}

		if (contentType.trim().equals(FORCE_DOWNLOAD)) {
			if (name != null) {
				contentType = contentType + ";name=\"" + name + "\"";
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + name + "\"");
			}
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "no-cache, must-revalidate");
			response.setHeader("Pragma", "no-cache");
		}

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
