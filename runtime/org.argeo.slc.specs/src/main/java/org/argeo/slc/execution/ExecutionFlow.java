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
package org.argeo.slc.execution;

/** Abstraction of an execution that can be identified and configured. */
public interface ExecutionFlow extends Runnable {
	/** Retrieve an immutable parameter */
	public Object getParameter(String key);

	/** Whether this immutable parameter is set */
	public Boolean isSetAsParameter(String key);

	/** The specifications of the execution flow. */
	public ExecutionSpec getExecutionSpec();

	/**
	 * The name of this execution flow. Can contains '/' which will be
	 * interpreted by UIs as a hierarchy;
	 */
	public String getName();

	/**
	 * @deprecated will be removed in SLC 2.0, the path should be the part of
	 *             the name with '/'
	 */
	public String getPath();
}
