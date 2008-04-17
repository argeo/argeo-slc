package org.argeo.slc.core.process;

import java.util.Date;

public class SlcExecutionStep {
	private SlcExecution slcExecution;
	private String type;
	private Date begin;
	private String log;

	public SlcExecution getSlcExecution() {
		return slcExecution;
	}

	public void setSlcExecution(SlcExecution slcExecution) {
		this.slcExecution = slcExecution;
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

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

}
