package org.argeo.slc.gpsbabel;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.tasks.SystemCall;
import org.springframework.beans.factory.InitializingBean;

public class GpsBabelCall extends SystemCall implements InitializingBean {
	private String executable = "/usr/bin/gpsbabel";
	private String inputFormat;
	private String inputFile;
	private String outputFormat;
	private String outputFile;

	public GpsBabelCall() {
		super();
	}

	public GpsBabelCall(String inputFormat, String inputFile,
			String outputFormat, String outputFile) {
		super();
		this.inputFormat = inputFormat;
		this.inputFile = inputFile;
		this.outputFormat = outputFormat;
		this.outputFile = outputFile;
		try {
			afterPropertiesSet();
		} catch (Exception e) {
			throw new SlcException("Cannot configure gpsbabel call", e);
		}
	}

	public void afterPropertiesSet() throws Exception {
		List<Object> command = new ArrayList<Object>();
		command.add(executable);
		command.add("-i");
		command.add(inputFormat);
		command.add("-f");
		command.add(inputFile);
		command.add("-o");
		command.add(outputFormat);
		command.add("-F");
		command.add(outputFile);
		setCommand(command);

		setStdOutLogLevel(LOG_STDOUT);
	}

	public final static void main(String[] args) {
		String output = new GpsBabelCall("garmin,get_posn", "usb:", "csv", "-")
				.function();
		System.out.println("output='" + output + "'");
	}
}
