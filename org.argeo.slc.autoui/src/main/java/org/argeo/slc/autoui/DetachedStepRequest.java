package org.argeo.slc.autoui;

import java.io.Serializable;
import java.util.Properties;

public class DetachedStepRequest implements Serializable {
	private String uuid;
	private Properties inputParameters;
	private String stepRef;
	private String path;

	public Properties getInputParameters() {
		return inputParameters;
	}

	public void setInputParameters(Properties inputParameters) {
		this.inputParameters = inputParameters;
	}

	public String getStepRef() {
		return stepRef;
	}

	public void setStepRef(String stepRef) {
		this.stepRef = stepRef;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
