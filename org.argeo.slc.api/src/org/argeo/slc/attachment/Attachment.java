package org.argeo.slc.attachment;

public interface Attachment {
	public String getUuid();

	public void setUuid(String uuid);

	public String getName();

	public String getContentType();
}
