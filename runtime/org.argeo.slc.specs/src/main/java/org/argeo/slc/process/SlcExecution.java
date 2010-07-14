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
import java.util.Map;
import java.util.TreeMap;

public class SlcExecution implements Serializable {
	private static final long serialVersionUID = -7607457971382118466L;
	public final static String STATUS_NONE = "DEFAULT";
	public final static String STATUS_SCHEDULED = "SCHEDULED";
	public final static String STATUS_RUNNING = "RUNNING";
	public final static String STATUS_FINISHED = "FINISHED";
	public final static String STATUS_ERROR = "ERROR";
	public final static String STATUS_CLEANED = "CLEANED";

	public final static String UNKOWN_HOST = "UNKOWN_HOST";

	private String uuid;
	private String host;
	private String user;
	private String type;
	private String status = STATUS_NONE;
	private Map<String, String> attributes = new TreeMap<String, String>();

	/** TODO: Synchronize */
	private List<SlcExecutionStep> steps = new ArrayList<SlcExecutionStep>();
	private List<RealizedFlow> realizedFlows = new ArrayList<RealizedFlow>();

	/** Attachment uuid. */
	private String realizedFlowsXml = null;

	public List<RealizedFlow> getRealizedFlows() {
		return realizedFlows;
	}

	public void setRealizedFlows(List<RealizedFlow> realizedFlows) {
		this.realizedFlows = realizedFlows;
	}

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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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
		synchronized (steps) {
			if (steps.size() > 0)
				return steps.get(steps.size() - 1);
			else
				return null;
		}
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

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer(getClass().getSimpleName());
		buf.append('#').append(uuid);
		buf.append(" status=").append(status);
		buf.append(" attributes=").append(attributes);
		return buf.toString();
	}

	public Date getStartDate() {
		synchronized (steps) {
			if (steps.size() == 0)
				return null;
			else
				return steps.get(0).getTimestamp();
		}
	}

	public Date getEndDate() {
		if (!status.equals(STATUS_FINISHED) && !status.equals(STATUS_ERROR))
			return null;

		synchronized (steps) {
			if (steps.size() == 0)
				return null;
			else
				return steps.get(steps.size() - 1).getTimestamp();
		}
	}

	/**
	 * Not (yet) a stable API, should not be relied upon!
	 * 
	 * @return an id or an url allowing to retrieve the XML, not the XML itself!
	 */
	public String getRealizedFlowsXml() {
		return realizedFlowsXml;
	}

	/** Not (yet) a stable API, should not be relied upon! */
	public void setRealizedFlowsXml(String realizedFlowsXml) {
		this.realizedFlowsXml = realizedFlowsXml;
	}

}
