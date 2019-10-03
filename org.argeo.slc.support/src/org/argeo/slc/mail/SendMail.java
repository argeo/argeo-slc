/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
package org.argeo.slc.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.tasks.SystemCall;

/** Sends a mail via JavaMail, local mail command or Google Mail. */
public class SendMail implements Runnable {
	// See:
	// http://java.sun.com/developer/onlineTraining/JavaMail/contents.html#JavaMailUsage
	// http://java.sun.com/products/javamail/FAQ.html#gmail

	private final static Log log = LogFactory.getLog(SendMail.class);

	private String host;
	private String from;
	private String to;
	private String subject;
	private String text;
	private String username;
	private String password;
	private Map<String, String> javaMailProperties = new HashMap<String, String>();

	public void run() {
		if ("local".equals(host))
			sendWithMailCommand();
		else if ("smtp.gmail.com".equals(host))
			sendWithGMail();
		else
			sendWithJavaMail();
	}

	protected void sendWithMailCommand() {
		SystemCall mail = new SystemCall("mail");
		mail.arg("-s", subject).arg(to);
		mail.run();
		if (log.isDebugEnabled())
			log.debug("Sent mail to " + to + " with OS mail command");
	}

	protected void sendWithJavaMail() {
		try {
			// Get system properties
			Properties props = System.getProperties();

			// Setup mail server
			props.put("mail.smtp.host", host);

			for (String key : javaMailProperties.keySet())
				props.put(key, javaMailProperties.get(key));

			// Get session
			Session session = Session.getDefaultInstance(props, null);

			// Define message
			MimeMessage message = new MimeMessage(session);
			buildJavaMailMessage(message);

			// Send message
			Transport.send(message);
			if (log.isDebugEnabled())
				log.debug("Sent mail to " + to + " with JavaMail");
		} catch (Exception e) {
			throw new SlcException("Cannot send message.", e);
		}
	}

	protected void sendWithGMail() {
		try {
			Properties props = new Properties();
			props.put("mail.smtps.auth", "true");
			props.put("mail.smtps.host", host);
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);
			buildJavaMailMessage(message);
			Transport t = session.getTransport("smtps");
			try {
				t.connect(host, username, password);
				t.sendMessage(message, message.getAllRecipients());
			} finally {
				t.close();
			}
			if (log.isDebugEnabled())
				log.debug("Sent mail to " + to + " with Google Mail");
		} catch (Exception e) {
			throw new SlcException("Cannot send message.", e);
		}
	}

	protected void buildJavaMailMessage(Message message) throws Exception {
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		message.setText(text);
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setJavaMailProperties(Map<String, String> javaMailProperties) {
		this.javaMailProperties = javaMailProperties;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
