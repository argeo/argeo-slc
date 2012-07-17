/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
