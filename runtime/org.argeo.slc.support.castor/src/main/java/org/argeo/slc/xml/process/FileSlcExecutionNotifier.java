package org.argeo.slc.xml.process;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;
import org.springframework.oxm.Marshaller;

public class FileSlcExecutionNotifier implements SlcExecutionNotifier {
	private final static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMdd-HHmmss");

	private String basePath;
	private Marshaller marshaller;

	private Map<String, String> uuidToDir = new HashMap<String, String>();

	public void addSteps(SlcExecution slcExecution,
			List<SlcExecutionStep> additionalSteps) {
		writeSlcExecution(slcExecution);
	}

	public void newExecution(SlcExecution slcExecution) {
		String dirPath = basePath + File.separator + sdf.format(new Date())
				+ '-' + slcExecution.getUuid();
		File dir = new File(dirPath);
		dir.mkdirs();

		uuidToDir.put(slcExecution.getUuid(), dirPath);

		writeSlcExecution(slcExecution);
	}

	public void updateExecution(SlcExecution slcExecution) {
		writeSlcExecution(slcExecution);
	}

	public void updateStatus(SlcExecution slcExecution, String oldStatus,
			String newStatus) {
		writeSlcExecution(slcExecution);
	}

	protected void writeSlcExecution(SlcExecution slcExecution) {
		FileWriter out = null;
		try {
			out = new FileWriter(getFilePath(slcExecution));
			marshaller.marshal(slcExecution, new StreamResult(out));
		} catch (Exception e) {
			throw new SlcException("Cannot marshall SlcExecution to "
					+ getFilePath(slcExecution), e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	protected String getFileName(SlcExecution slcExecution) {
		return "SlcExecution-" + slcExecution.getUuid() + ".xml";
	}

	protected String getFilePath(SlcExecution slcExecution) {
		String dirPath = uuidToDir.get(slcExecution.getUuid());
		return dirPath + File.separator + "SlcExecution-"
				+ slcExecution.getUuid() + ".xml";
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

}
