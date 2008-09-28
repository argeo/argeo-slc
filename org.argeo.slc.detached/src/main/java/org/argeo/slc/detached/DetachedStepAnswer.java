package org.argeo.slc.detached;

import java.util.Properties;

public class DetachedStepAnswer {
	public static int PROCESSED = 0;
	public static int ERROR = 1;
	public static int SKIPPED = 2;

	private Properties outputParameters;
	private int outputStatus;
	private String log;

	public Properties getOutputParameters() {
		return outputParameters;
	}

	public void setOutputParameters(Properties outputParameters) {
		this.outputParameters = outputParameters;
	}

	public int getOutputStatus() {
		return outputStatus;
	}

	public void setOutputStatus(int outputStatus) {
		this.outputStatus = outputStatus;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

}
