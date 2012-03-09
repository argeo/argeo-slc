/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.msg.process;

import java.io.Serializable;

@Deprecated
public class SlcExecutionStatusRequest implements Serializable {
	private static final long serialVersionUID = -6495004680978575999L;
	private String slcExecutionUuid;
	private String newStatus;

	public SlcExecutionStatusRequest() {
	}

	public SlcExecutionStatusRequest(String slcExecutionUuid, String newStatus) {
		this.slcExecutionUuid = slcExecutionUuid;
		this.newStatus = newStatus;
	}

	public String getSlcExecutionUuid() {
		return slcExecutionUuid;
	}

	public void setSlcExecutionUuid(String slcExecutionUuid) {
		this.slcExecutionUuid = slcExecutionUuid;
	}

	public String getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + slcExecutionUuid;
	}
}
