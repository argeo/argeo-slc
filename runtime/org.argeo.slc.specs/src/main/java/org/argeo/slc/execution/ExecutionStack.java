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

public interface ExecutionStack {
	/**
	 * @param name
	 * @return null if no object is found
	 */
	public Object findScopedObject(String name);

	public void addScopedObject(String name, Object obj);

	public void enterFlow(ExecutionFlow executionFlow);

	/** @return internal stack level UUID. */
	public String getCurrentStackLevelUuid();

	public Integer getStackSize();

	public void leaveFlow(ExecutionFlow executionFlow);

	Object findLocalVariable(String key);
}
