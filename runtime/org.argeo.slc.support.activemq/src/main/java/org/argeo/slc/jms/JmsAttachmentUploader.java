package org.argeo.slc.jms;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.attachment.AttachmentUploader;
import org.springframework.core.io.Resource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JmsAttachmentUploader implements AttachmentUploader {
	public final static String ATTACHMENT_ID = "slc_attachmentId";
	public final static String ATTACHMENT_NAME = "slc_attachmentName";
	public final static String ATTACHMENT_CONTENT_TYPE = "slc_attachmentContentType";

	private JmsTemplate jmsTemplate;
	private Destination destination;

	public void upload(final Attachment attachment, final Resource resource) {
		jmsTemplate.send(destination, new MessageCreator() {

			public Message createMessage(Session session) throws JMSException {
				BytesMessage message = session.createBytesMessage();
				message.setStringProperty(ATTACHMENT_ID, attachment.getUuid());
				message
						.setStringProperty(ATTACHMENT_NAME, attachment
								.getName());
				message.setStringProperty(ATTACHMENT_CONTENT_TYPE, attachment
						.getContentType());

				try {
					BufferedInputStream in = new BufferedInputStream(resource
							.getInputStream());
					byte[] buffer = new byte[1024 * 1024];
					while (in.read(buffer) > 0) {
						message.writeBytes(buffer);
					}
				} catch (IOException e) {
					throw new SlcException(
							"Cannot write into byte message for attachment "
									+ attachment + " and resource " + resource,
							e);
				}
				return message;
			}
		});

	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

}
