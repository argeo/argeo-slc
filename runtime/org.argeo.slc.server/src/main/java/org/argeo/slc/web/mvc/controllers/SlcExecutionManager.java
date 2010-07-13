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

package org.argeo.slc.web.mvc.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.attachment.AttachmentsStorage;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.process.SlcExecution;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;

public class SlcExecutionManager {
	private final static Log log = LogFactory.getLog(SlcExecutionManager.class);

	private Unmarshaller unmarshaller;
	private Marshaller marshaller;
	private AttachmentsStorage attachmentsStorage;

	public SlcExecutionManager(Unmarshaller unmarshaller,
			Marshaller marshaller, AttachmentsStorage attachmentsStorage) {

		this.unmarshaller = unmarshaller;
		this.marshaller = marshaller;
		this.attachmentsStorage = attachmentsStorage;
	}

	void storeRealizedFlows(SlcExecution slcExecution) {

		Attachment attachment = realizedFlowsAttachment(UUID.randomUUID()
				.toString(), slcExecution);
		InputStream in = null;
		try {
			ObjectList ol = new ObjectList(slcExecution.getRealizedFlows());
			StringResult result = new StringResult();
			marshaller.marshal(ol, result);
			in = new ByteArrayInputStream(result.toString().getBytes());
			attachmentsStorage.storeAttachment(attachment, in);

			slcExecution.setRealizedFlowsXml(attachment.getUuid());
		} catch (Exception e) {
			log.error("Could not store realized flows as attachment #"
					+ attachment.getUuid(), e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	void retrieveRealizedFlows(SlcExecution slcExecution) {
		Attachment attachment = realizedFlowsAttachment(slcExecution
				.getRealizedFlowsXml(), slcExecution);

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		try {
			// TODO: optimize with piped streams
			out = new ByteArrayOutputStream();
			attachmentsStorage.retrieveAttachment(attachment, out);

			byte[] arr = out.toByteArray();
			in = new ByteArrayInputStream(arr);
			StreamSource source = new StreamSource(in);
			ObjectList ol = (ObjectList) unmarshaller.unmarshal(source);
			ol.fill(slcExecution.getRealizedFlows());
		} catch (Exception e) {
			log.error("Could not retrieve realized flows from attachment #"
					+ attachment.getUuid(), e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	/** Unify labeling in the package */
	static Attachment realizedFlowsAttachment(String attachmentUuid,
			SlcExecution slcExecution) {
		return new SimpleAttachment(attachmentUuid,
				"RealizedFlows of SlcExecution #" + slcExecution.getUuid(),
				"text/xml");
	}
}
