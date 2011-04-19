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

package org.argeo.slc.xml.process;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionNotifier;
import org.argeo.slc.process.SlcExecutionStep;
import org.springframework.oxm.Marshaller;

/** @deprecated Probably not even working anymore */
public class FileSlcExecutionNotifier implements SlcExecutionNotifier {
//	private final static SimpleDateFormat sdf = new SimpleDateFormat(
//			"yyyyMMdd-HHmmss");
//
//	private String basePath;
	private Marshaller marshaller;

	private Map<String, String> uuidToDir = new HashMap<String, String>();

	public void addSteps(ExecutionProcess slcExecution,
			List<SlcExecutionStep> additionalSteps) {
		writeSlcExecution(slcExecution);
	}

	public void updateStatus(ExecutionProcess slcExecution, String oldStatus,
			String newStatus) {
		writeSlcExecution(slcExecution);
	}

	protected void writeSlcExecution(ExecutionProcess process) {
		if (!(process instanceof SlcExecution))
			throw new SlcException("Unsupported process type "
					+ process.getClass());
		SlcExecution slcExecution = (SlcExecution) process;
		FileWriter out = null;
		try {
			out = new FileWriter(getFilePath(slcExecution));
			marshaller.marshal(slcExecution, new StreamResult(out));
		} catch (Exception e) {
			throw new SlcException("Cannot marshall SlcExecution to "
					+ getFilePath(slcExecution), e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	protected String getFileName(SlcExecution slcExecution) {
		return "SlcExecution-" + slcExecution.getUuid() + ".xml";
	}

	protected String getFilePath(SlcExecution slcExecution) {
		String dirPath = uuidToDir.get(slcExecution.getUuid());
		return dirPath + File.separator + "SlcExecution-"
				+ slcExecution.getUuid() + ".xml";
	}

	public void setBasePath(String basePath) {
		//this.basePath = basePath;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

}
