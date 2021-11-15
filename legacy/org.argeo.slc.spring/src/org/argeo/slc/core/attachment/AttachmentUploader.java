package org.argeo.slc.core.attachment;

import org.argeo.slc.attachment.Attachment;
import org.springframework.core.io.Resource;

public interface AttachmentUploader {
	public void upload(Attachment attachment, Resource resource);
}
