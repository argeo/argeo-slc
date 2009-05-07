package org.argeo.slc.core.execution.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.attachment.AttachmentUploader;
import org.argeo.slc.core.attachment.AttachmentsEnabled;
import org.springframework.core.io.Resource;

public class UploadAttachments implements Runnable {
	private AttachmentUploader attachmentUploader;
	private Map<Attachment, Resource> attachments = new HashMap<Attachment, Resource>();
	private List<AttachmentsEnabled> attachTo = new ArrayList<AttachmentsEnabled>();

	public void run() {
		for (Attachment attachment : attachments.keySet()) {
			Resource resource = attachments.get(attachment);
			attachmentUploader.upload(attachment, resource);
			for (AttachmentsEnabled attachmentsEnabled : attachTo) {
				attachmentsEnabled.addAttachment(attachment);
			}
		}

	}

	public void setAttachmentUploader(AttachmentUploader attachmentUploader) {
		this.attachmentUploader = attachmentUploader;
	}

	public void setAttachments(Map<Attachment, Resource> attachments) {
		this.attachments = attachments;
	}

	public void setAttachTo(List<AttachmentsEnabled> attachTo) {
		this.attachTo = attachTo;
	}

	
}
