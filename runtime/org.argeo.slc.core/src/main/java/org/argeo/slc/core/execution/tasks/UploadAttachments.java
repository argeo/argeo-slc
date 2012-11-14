/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
