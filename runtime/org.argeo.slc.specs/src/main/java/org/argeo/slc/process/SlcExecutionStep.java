package org.argeo.slc.process;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

public class SlcExecutionStep {
	public final static String TYPE_START = "START";
	public final static String TYPE_END = "END";
	public final static String TYPE_PHASE_START = "PHASE_START";
	public final static String TYPE_PHASE_END = "PHASE_END";
	public final static String TYPE_LOG = "LOG";

	private String uuid = UUID.randomUUID().toString();
	private String type;
	private Date begin = new Date();
	private List<String> logLines = new ArrayList<String>();

	/** Empty constructor */
	public SlcExecutionStep() {
	}

	/** Creates a step of type LOG. */
	public SlcExecutionStep(String log) {
		this(TYPE_LOG, log);
	}

	/** Creates a step of the given type. */
	public SlcExecutionStep(String type, String log) {
		this.type = type;
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

	@SuppressWarnings(value = { "unchecked" })
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
