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

package org.argeo.slc.execution;

/** Variables or references attached to an execution (typically thread bounded).*/
public interface ExecutionContext {
	public final static String VAR_EXECUTION_CONTEXT_ID = "slcVar.executionContext.id";
	public final static String VAR_EXECUTION_CONTEXT_CREATION_DATE = "slcVar.executionContext.creationDate";
	public final static String VAR_FLOW_ID = "slcVar.flow.id";
	public final static String VAR_FLOW_NAME = "slcVar.flow.name";

	public String getUuid();

	/** @return the variable value, or <code>null</code> if not found. */
	public Object getVariable(String key);

	public void setVariable(String key, Object value);
}
