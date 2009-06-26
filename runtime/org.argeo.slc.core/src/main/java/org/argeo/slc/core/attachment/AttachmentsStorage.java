package org.argeo.slc.core.attachment;

import java.io.InputStream;
import java.io.OutputStream;


public interface AttachmentsStorage {
	public void retrieveAttachment(Attachment attachment,
			OutputStream outputStream);

	public void storeAttachment(Attachment attachment, InputStream inputStream);
}