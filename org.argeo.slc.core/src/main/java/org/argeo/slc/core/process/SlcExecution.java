package org.argeo.slc.core.process;

import java.util.List;
import java.util.Vector;

public class SlcExecution {
	public final static String STATUS_SCHEDULED = "SCHEDULED";
	public final static String STATUS_RUNNING = "RUNNING";
	public final static String STATUS_FINISHED = "FINISHED";
	public final static String STATUS_ERROR = "ERROR";
	public final static String STATUS_CLEANED = "CLEANED";

	public final static String UNKOWN_HOST = "UNKOWN_HOST";

	private String uuid;
	private String host;
	private String path;
	private String type;
	private String status;

	private List<SlcExecutionStep> steps = new Vector<SlcExecutionStep>();

	public List<SlcExecutionStep> getSteps() {
		return steps;
	}

	public void setSteps(List<SlcExecutionStep> steps) {
		this.steps = steps;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public SlcExecutionStep currentStep() {
		if (steps.size() > 0)
			return steps.get(steps.size() - 1);
		else
			return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SlcExecution) {
			return getUuid().equals(((SlcExecution) obj).getUuid());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getUuid().hashCode();
	}

}
