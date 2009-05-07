package org.argeo.slc.msg.test.tree;

import org.argeo.slc.core.attachment.SimpleAttachment;

public class AddTreeTestResultAttachmentRequest {
	private String resultUuid;
	private SimpleAttachment attachment;

	public String getResultUuid() {
		return resultUuid;
	}

	public void setResultUuid(String resultUuid) {
		this.resultUuid = resultUuid;
	}

	public SimpleAttachment getAttachment() {
		return attachment;
	}

	public void setAttachment(SimpleAttachment attachment) {
		this.attachment = attachment;
	}

}
