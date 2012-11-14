/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
package org.argeo.slc.jcr.execution;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.ExecutionStep;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;

/** Execution process implementation based on a JCR node. */
public class JcrExecutionProcess implements ExecutionProcess, SlcNames {
	private Log log = LogFactory.getLog(JcrExecutionProcess.class);
	private final Node node;

	private Long nextLogLine = 1l;

	public JcrExecutionProcess(Node node) {
		this.node = node;
	}

	public String getUuid() {
		try {
			return node.getProperty(SLC_UUID).getString();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot get uuid for " + node, e);
		}
	}

	public String getStatus() {
		try {
			return node.getProperty(SLC_STATUS).getString();
		} catch (RepositoryException e) {
			log.error("Cannot get status: " + e);
			// we should re-throw exception because this information can
			// probably used for monitoring in case there are already unexpected
			// exceptions
			return UNKOWN;
		}
	}

	public void setStatus(String status) {
		try {
			node.setProperty(SLC_STATUS, status);
			// last modified properties needs to be manually updated
			// see https://issues.apache.org/jira/browse/JCR-2233
			JcrUtils.updateLastModified(node);
			node.getSession().save();
		} catch (RepositoryException e) {
			JcrUtils.discardUnderlyingSessionQuietly(node);
			// we should re-throw exception because this information can
			// probably used for monitoring in case there are already unexpected
			// exceptions
			log.error("Cannot set status " + status + ": " + e);
		}
	}

	/**
	 * Synchronized in order to make sure that there is no concurrent
	 * modification of {@link #nextLogLine}.
	 */
	public synchronized void addSteps(List<ExecutionStep> steps) {
		try {
			steps: for (ExecutionStep step : steps) {
				String type;
				if (step.getType().equals(ExecutionStep.TRACE))
					type = SlcTypes.SLC_LOG_TRACE;
				else if (step.getType().equals(ExecutionStep.DEBUG))
					type = SlcTypes.SLC_LOG_DEBUG;
				else if (step.getType().equals(ExecutionStep.INFO))
					type = SlcTypes.SLC_LOG_INFO;
				else if (step.getType().equals(ExecutionStep.WARNING))
					type = SlcTypes.SLC_LOG_WARNING;
				else if (step.getType().equals(ExecutionStep.ERROR))
					type = SlcTypes.SLC_LOG_ERROR;
				else
					// skip
					continue steps;

				String relPath = SLC_LOG + '/'
						+ step.getThread().replace('/', '_') + '/'
						+ step.getLocation().replace('.', '/');
				String path = node.getPath() + '/' + relPath;
				// clean special character
				// TODO factorize in JcrUtils
				path = path.replace('@', '_');

				Node location = JcrUtils.mkdirs(node.getSession(), path);
				Node logEntry = location.addNode(Long.toString(nextLogLine),
						type);
				logEntry.setProperty(SLC_MESSAGE, step.getLog());
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(step.getTimestamp());
				logEntry.setProperty(SLC_TIMESTAMP, calendar);

				// System.out.println("Logged " + logEntry.getPath());

				nextLogLine++;
			}

			// last modified properties needs to be manually updated
			// see https://issues.apache.org/jira/browse/JCR-2233
			JcrUtils.updateLastModified(node);

			node.getSession().save();
		} catch (Exception e) {
			JcrUtils.discardUnderlyingSessionQuietly(node);
			e.printStackTrace();
		}
	}

	public Node getNode() {
		return node;
	}

}
