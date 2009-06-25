package org.argeo.slc.core.attachment;

public interface Attachment {
	public String getUuid();

	public void setUuid(String uuid);

	public String getName();

	public String getContentType();
}
