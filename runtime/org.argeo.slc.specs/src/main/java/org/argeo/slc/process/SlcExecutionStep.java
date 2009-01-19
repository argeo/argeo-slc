package org.argeo.slc.process;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

public class SlcExecutionStep {
	public final static String TYPE_LOG = "LOG";

	private String uuid;
	private String type;
	private Date begin;
	private List<String> logLines = new ArrayList<String>();

	/** Empty constructor */
	public SlcExecutionStep() {
	}

	public SlcExecutionStep(String log) {
		this.type = TYPE_LOG;
		this.begin = new Date();
		this.uuid = UUID.randomUUID().toString();
		addLog(log);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public List<String> getLogLines() {
		return logLines;
	}

	public void setLogLines(List<String> logLines) {
		this.logLines = logLines;
	}

	public String logAsString() {
		StringWriter writer = new StringWriter();
		String log = writer.toString();
		IOUtils.closeQuietly(writer);
		return log;
	}

	public void addLog(String log) {
		if (log == null)
			return;

		try {
			List<String> lines = IOUtils.readLines(new StringReader(log));
			logLines.addAll(lines);
		} catch (IOException e) {
			throw new RuntimeException("Cannot add log", e);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + uuid;
	}

}
