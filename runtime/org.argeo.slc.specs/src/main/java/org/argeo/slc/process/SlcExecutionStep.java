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

package org.argeo.slc.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import org.argeo.slc.execution.ExecutionStep;

/**
 * An atomic step to be notified in during an {@link SlcExecution}. Can be a log
 * or the start/end of a phase, etc.
 * 
 * @deprecated use {@link ExecutionStep} instead
 */
public class SlcExecutionStep extends ExecutionStep {
	private static final long serialVersionUID = -7308643628104726471L;

	private String uuid = UUID.randomUUID().toString();
	private List<String> logLines = new ArrayList<String>();

	/** Empty constructor */
	public SlcExecutionStep() {
	}

	/** Creates a step at the current date of type INFO */
	public SlcExecutionStep(String log) {
		this(new Date(), INFO, log);
	}

	/** Creates a step at the current date */
	public SlcExecutionStep(String type, String log) {
		this(new Date(), type, log);
	}

	/** Creates a step of the given type. */
	public SlcExecutionStep(Date timestamp, String type, String log) {
		this(timestamp, type, log, Thread.currentThread().getName());
	}

	public SlcExecutionStep(Date timestamp, String type, String log,
			String thread) {
		super(timestamp, type, log, thread);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTimestamp(Date begin) {
		this.timestamp = begin;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public List<String> getLogLines() {
		return logLines;
	}

	public void setLogLines(List<String> logLines) {
		this.logLines = logLines;
	}

	protected String addLog(String log) {
		if (logLines == null)
			logLines = new ArrayList<String>();

		if (log == null)
			return null;

		StringTokenizer st = new StringTokenizer(log, "\n");
		while (st.hasMoreTokens())
			logLines.add(removeNonXmlChars(st.nextToken()));
		return null;
	}

	/**
	 * Removes non XML compliant characters (from
	 * http://stackoverflow.com/questions
	 * /20762/how-do-you-remove-invalid-hexadecimal
	 * -characters-from-an-xml-based-data-source-pr)
	 */
	private static String removeNonXmlChars(String inString) {
		if (inString == null)
			return null;

		StringBuilder newString = new StringBuilder();
		char ch;

		for (int i = 0; i < inString.length(); i++) {

			ch = inString.charAt(i);
			// remove any characters outside the valid UTF-8 range as well as
			// all control characters
			// except tabs and new lines
			if ((ch < 0x00FD && ch > 0x001F) || ch == '\t' || ch == '\n'
					|| ch == '\r') {
				newString.append(ch);
			}
		}
		return newString.toString();

	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + uuid;
	}

}
