package org.argeo.slc.jcr.execution;

import javax.jcr.Session;

import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.attachment.AttachmentUploader;
import org.springframework.core.io.Resource;

/** JCR based attachment uploader */
public class JcrAttachmentUploader implements AttachmentUploader {
	private Session session;

	public void upload(Attachment attachment, Resource resource) {
		session.toString();
		// not yet implemented, need to review the interface
	}

	public void setSession(Session session) {
		this.session = session;
	}

}
