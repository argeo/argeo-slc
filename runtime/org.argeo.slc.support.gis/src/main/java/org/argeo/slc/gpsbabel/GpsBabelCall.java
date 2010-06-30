/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
		setExceptionOnFailed(true);
	}

	public final static void main(String[] args) {
		String output = new GpsBabelCall("garmin,get_posn", "usb:", "csv", "-")
				.function();
		System.out.println("output='" + output + "'");
	}
}
