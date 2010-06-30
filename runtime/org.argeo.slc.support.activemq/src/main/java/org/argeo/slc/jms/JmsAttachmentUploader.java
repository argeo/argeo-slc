/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

import java.io.IOException;
import java.io.InputStream;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.attachment.AttachmentUploader;
import org.springframework.core.io.Resource;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JmsAttachmentUploader implements AttachmentUploader {
	private final static Log log = LogFactory
			.getLog(JmsAttachmentUploader.class);

	public final static String ATTACHMENT_ID = "slc_attachmentId";
	public final static String ATTACHMENT_NAME = "slc_attachmentName";
	public final static String ATTACHMENT_CONTENT_TYPE = "slc_attachmentContentType";

	private JmsTemplate jmsTemplate;
	private Destination destination;

	public void upload(final Attachment attachment, final Resource resource) {
		try {
			jmsTemplate.send(destination, new MessageCreator() {

				public Message createMessage(Session session)
						throws JMSException {
					BytesMessage message = session.createBytesMessage();
					message.setStringProperty(ATTACHMENT_ID, attachment
							.getUuid());
					message.setStringProperty(ATTACHMENT_NAME, attachment
							.getName());
					message.setStringProperty(ATTACHMENT_CONTENT_TYPE,
							attachment.getContentType());

					InputStream in = null;
					try {
						in = resource.getInputStream();
						byte[] buffer = new byte[1024 * 1024];
						int read = -1;
						while ((read = in.read(buffer)) > 0) {
							message.writeBytes(buffer, 0, read);
						}
					} catch (IOException e) {
						throw new SlcException(
								"Cannot write into byte message for attachment "
										+ attachment + " and resource "
										+ resource, e);
					} finally {
						IOUtils.closeQuietly(in);
					}
					return message;
				}
			});
		} catch (JmsException e) {
			if (log.isTraceEnabled())
				log.debug("Cannot upload", e);
			else if (log.isDebugEnabled())
				log.debug("Cannot upload: " + e.getMessage());
		}

	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

}
