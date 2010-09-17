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

package org.argeo.slc.core.attachment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

public class FileAttachmentsStorage implements AttachmentsStorage,
		AttachmentUploader, InitializingBean {
	private final static Log log = LogFactory
			.getLog(FileAttachmentsStorage.class);

	private File attachmentsDirectory;

	private String attachmentsTocFileName = "attachmentsToc.csv";

	private DateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
	private DateFormat dateFormatTime = new SimpleDateFormat("HH:mm:ss");

	public void afterPropertiesSet() {
		if (attachmentsDirectory == null) {

			String osgiInstanceArea = System.getProperty("osgi.instance.area");
			if (osgiInstanceArea != null) {
				if (osgiInstanceArea.startsWith("file:"))
					osgiInstanceArea = osgiInstanceArea.substring("file:"
							.length());
				attachmentsDirectory = new File(osgiInstanceArea
						+ File.separator + "slcAttachments");
			}

			if (attachmentsDirectory == null) {
				String tempDir = System.getProperty("java.io.tmpdir");
				attachmentsDirectory = new File(tempDir + File.separator
						+ "slcAttachments");
			}
		}
		if (!attachmentsDirectory.exists())
			attachmentsDirectory.mkdirs();
		if (log.isDebugEnabled())
			log.debug("File attachment storage initialized in directory "
					+ attachmentsDirectory);
	}

	public void retrieveAttachment(Attachment attachment,
			OutputStream outputStream) {
		File file = getFile(attachment);
		InputStream in = null;
		try {
			byte[] buffer = new byte[1024 * 1024];
			in = new FileInputStream(file);
			int read = -1;
			while ((read = in.read(buffer)) >= 0) {
				outputStream.write(buffer, 0, read);
			}
			if (log.isTraceEnabled())
				log.trace("Read " + attachment + " from " + file);
		} catch (IOException e) {
			throw new SlcException("Cannot write attachment " + attachment
					+ " to " + file, e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public void storeAttachment(Attachment attachment, InputStream inputStream) {
		File file = getFile(attachment);
		FileOutputStream out = null;
		try {
			byte[] buffer = new byte[1024 * 1024];
			out = new FileOutputStream(file);
			int read = -1;
			while ((read = inputStream.read(buffer)) >= 0) {
				out.write(buffer, 0, read);
			}
			if (log.isTraceEnabled())
				log.trace("Wrote " + attachment + " to " + file);
			updateAttachmentToc(attachment, file);
		} catch (IOException e) {
			throw new SlcException("Cannot write attachment " + attachment
					+ " to " + file, e);
		} finally {
			IOUtils.closeQuietly(out);
		}

	}

	public void upload(Attachment attachment, Resource resource) {
		try {
			storeAttachment(attachment, resource.getInputStream());
		} catch (IOException e) {
			throw new SlcException("Cannot upload attachment " + attachment, e);
		}
	}

	/** For monitoring purposes only */
	protected void updateAttachmentToc(Attachment attachment, File file) {
		Date date = new Date(file.lastModified());
		FileWriter writer = null;
		try {
			writer = new FileWriter(attachmentsDirectory + File.separator
					+ attachmentsTocFileName, true);
			writer.append(dateFormatDay.format(date));
			writer.append(',');
			writer.append(dateFormatTime.format(date));
			writer.append(',');
			writer.append(attachment.getUuid());
			writer.append(',');
			writer.append(attachment.getName());
			writer.append(',');
			writer.append(attachment.getContentType());
			writer.append(',');
			writer.append(Long.toString(file.length()));
			writer.append(',');
			writer.append(file.getCanonicalPath());
			writer.append('\n');
		} catch (IOException e) {
			log.warn("Could not update attachments TOC for " + attachment
					+ " and file " + file, e);
		} finally {
			IOUtils.closeQuietly(writer);
		}

	}

	protected File getFile(Attachment attachment) {
		File file = new File(attachmentsDirectory + File.separator
				+ attachment.getUuid());
		return file;
	}

	public void setAttachmentsDirectory(File attachmentsDirectory) {
		this.attachmentsDirectory = attachmentsDirectory;
	}

	public void setAttachmentsTocFileName(String attachmentsTocFileName) {
		this.attachmentsTocFileName = attachmentsTocFileName;
	}

	public void setDateFormatDay(DateFormat dateFormatDay) {
		this.dateFormatDay = dateFormatDay;
	}

	public void setDateFormatTime(DateFormat dateFormatTime) {
		this.dateFormatTime = dateFormatTime;
	}

}
