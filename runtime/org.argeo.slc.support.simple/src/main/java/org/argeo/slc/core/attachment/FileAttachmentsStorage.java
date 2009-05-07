package org.argeo.slc.core.attachment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;

public class FileAttachmentsStorage implements AttachmentsStorage {
	private File attachmentsDirectory;

	public FileAttachmentsStorage() {
		String osgiInstanceArea = System.getProperty("osgi.instance.area");
		if (osgiInstanceArea != null) {
			if (osgiInstanceArea.startsWith("file:"))
				osgiInstanceArea = osgiInstanceArea.substring("file:".length());
			attachmentsDirectory = new File(osgiInstanceArea + File.separator
					+ "executionResources");
		}

		if (attachmentsDirectory == null) {
			String tempDir = System.getProperty("java.io.tmpdir");
			attachmentsDirectory = new File(tempDir + File.separator
					+ "slcAttachments");
		}
	}

	public void retrieveAttachment(Attachment attachment,
			OutputStream outputStream) {
		File file = getFile(attachment);
		InputStream in = null;
		try {
			byte[] buffer = new byte[1024 * 1024];
			in = new FileInputStream(file);
			while (in.read(buffer) >= 0) {
				outputStream.write(buffer);
			}
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
		} catch (IOException e) {
			throw new SlcException("Cannot write attachment " + attachment
					+ " to " + file, e);
		} finally {
			IOUtils.closeQuietly(out);
		}

	}

	protected File getFile(Attachment attachment) {
		if (!attachmentsDirectory.exists())
			attachmentsDirectory.mkdirs();
		File file = new File(attachmentsDirectory + File.separator
				+ attachment.getUuid());
		return file;
	}
}
