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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

public class SlcExecutionStep implements Serializable {
	private static final long serialVersionUID = -7308643628104726471L;

	public final static String START = "START";
	public final static String END = "END";
	public final static String PHASE_START = "PHASE_START";
	public final static String PHASE_END = "PHASE_END";
	public final static String ERROR = "ERROR";
	public final static String WARNING = "WARNING";
	public final static String INFO = "INFO";
	public final static String DEBUG = "DEBUG";
	public final static String TRACE = "TRACE";

	private String uuid = UUID.randomUUID().toString();
	private String type;
	private String thread;
	private Date timestamp = new Date();
	private List<String> logLines = new ArrayList<String>();

	/** Empty constructor */
	public SlcExecutionStep() {
		thread = Thread.currentThread().getName();
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
		this.type = type;
		this.timestamp = timestamp;
		this.thread = thread;
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date begin) {
		this.timestamp = begin;
	}

	public String getThread() {
		return thread;
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

	public void addLog(String log) {
		if (log == null)
			return;

		StringTokenizer st = new StringTokenizer(log, "\n");
		while (st.hasMoreTokens())
			logLines.add(removeNonXmlChars(st.nextToken()));
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
