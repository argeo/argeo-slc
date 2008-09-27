package org.argeo.slc.autoui;

import java.util.Properties;

public class DetachedStepRequest {
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
}
