package org.argeo.slc.process;

import java.util.HashMap;
import java.util.Map;

public class SlcExecutionSpec {
	private Long tid;
	private Map<String, SlcExecutionSpecField> executionSpecFields = new HashMap<String, SlcExecutionSpecField>();

	public Long getTid() {
		return tid;
	}

	public void setTid(Long tid) {
		this.tid = tid;
	}

	public Map<String, SlcExecutionSpecField> getExecutionSpecFields() {
		return executionSpecFields;
	}

	public void setExecutionSpecFields(
			Map<String, SlcExecutionSpecField> executionSpecFields) {
		this.executionSpecFields = executionSpecFields;
	}

}
