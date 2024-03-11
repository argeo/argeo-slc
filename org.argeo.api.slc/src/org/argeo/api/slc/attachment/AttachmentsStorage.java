package org.argeo.api.slc.attachment;

import java.io.InputStream;
import java.io.OutputStream;

public interface AttachmentsStorage {
	public void retrieveAttachment(Attachment attachment,
			OutputStream outputStream);

	/** Does NOT close the provided input stream. */
	public void storeAttachment(Attachment attachment, InputStream inputStream);
}
