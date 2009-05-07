package org.argeo.slc.core.execution.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.attachment.AttachmentUploader;
import org.argeo.slc.core.attachment.AttachmentsEnabled;
import org.springframework.core.io.Resource;

public class UploadAttachments implements Runnable {
	private AttachmentUploader attachmentUploader;
	private Attachment attachment = null;
	private Resource resource = null;
	private Map<Attachment, Resource> attachments = new HashMap<Attachment, Resource>();
	private List<AttachmentsEnabled> attachTo = new ArrayList<AttachmentsEnabled>();
	private Boolean newUuidPerExecution = true;

	public void run() {
		if (attachment != null) {
			if (resource == null)
				throw new SlcException("A resource must be specified.");
			uploadAndAdd(attachment, resource);
		}

		for (Attachment attachmentT : attachments.keySet()) {
			Resource resourceT = attachments.get(attachmentT);
			uploadAndAdd(attachmentT, resourceT);
		}

	}

	protected void uploadAndAdd(Attachment attachment, Resource resource) {
		if (newUuidPerExecution)
			attachment.setUuid(UUID.randomUUID().toString());
		attachmentUploader.upload(attachment, resource);
		for (AttachmentsEnabled attachmentsEnabled : attachTo) {
			attachmentsEnabled.addAttachment(attachment);
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

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setNewUuidPerExecution(Boolean newUuidPerExecution) {
		this.newUuidPerExecution = newUuidPerExecution;
	}

}
