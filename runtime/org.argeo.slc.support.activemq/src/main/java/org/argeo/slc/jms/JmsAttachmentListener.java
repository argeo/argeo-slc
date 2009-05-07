package org.argeo.slc.jms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.AttachmentsStorage;
import org.argeo.slc.core.attachment.SimpleAttachment;

public class JmsAttachmentListener implements MessageListener {
	private AttachmentsStorage attachmentsStorage;

	public void onMessage(Message msg) {
		BytesMessage message = (BytesMessage) msg;

		InputStream in = null;
		try {
			SimpleAttachment attachment = new SimpleAttachment();
			attachment.setUuid(msg
					.getStringProperty(JmsAttachmentUploader.ATTACHMENT_ID));
			attachment.setName(msg
					.getStringProperty(JmsAttachmentUploader.ATTACHMENT_NAME));
			attachment
					.setContentType(msg
							.getStringProperty(JmsAttachmentUploader.ATTACHMENT_CONTENT_TYPE));

			// Check body length
			Long bodyLength = message.getBodyLength();
			if (bodyLength > Integer.MAX_VALUE)
				throw new SlcException("Attachment cannot be bigger than "
						+ Integer.MAX_VALUE
						+ " bytes with this transport. Use another transport.");

			byte[] buffer = new byte[bodyLength.intValue()];
			message.readBytes(buffer);
			in = new ByteArrayInputStream(buffer);
			attachmentsStorage.storeAttachment(attachment, in);
		} catch (JMSException e) {
			throw new SlcException("Could not process attachment message "
					+ msg, e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public void setAttachmentsStorage(AttachmentsStorage attachmentsStorage) {
		this.attachmentsStorage = attachmentsStorage;
	}

}
