package org.argeo.slc.core.attachment;

import java.io.Serializable;
import java.util.UUID;

import org.argeo.slc.attachment.Attachment;

public class SimpleAttachment implements Attachment, Serializable {
	private static final long serialVersionUID = 6615155908800610606L;
	private String uuid = UUID.randomUUID().toString();
	private String name;
	private String contentType = "";

	public SimpleAttachment() {
	}

	public SimpleAttachment(String uuid, String name, String contentType) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.contentType = contentType;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String toString() {
		return "Attachment #" + uuid + "(" + name + ", " + contentType + ")";
	}

	public boolean equals(Object obj) {
		if (obj instanceof Attachment) {
			Attachment attachment = (Attachment) obj;
			if (uuid != null && attachment.getUuid() != null)
				return uuid.equals(attachment.getUuid());

			if (name != null && attachment.getName() != null)
				return name.equals(attachment.getName());

			return hashCode() == attachment.hashCode();
		}
		return false;
	}
}
