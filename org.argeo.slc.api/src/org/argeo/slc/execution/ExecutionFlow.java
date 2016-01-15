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
package org.argeo.slc.execution;

import java.util.Iterator;

/** Abstraction of an execution that can be identified and configured. */
public interface ExecutionFlow extends Runnable {
	/** Retrieve an immutable parameter */
	public Object getParameter(String key);

	/** Whether this immutable parameter is set */
	public Boolean isSetAsParameter(String key);

	/** The specifications of the execution flow. */
	public ExecutionSpec getExecutionSpec();

	/**
	 * List sub-runnables that would be executed if run() method would be
	 * called.
	 */
	public Iterator<Runnable> runnables();

	/**
	 * If there is one and only one runnable wrapped return it, throw an
	 * exception otherwise.
	 */
	public Runnable getRunnable();

	/**
	 * The name of this execution flow. Can contains '/' which will be
	 * interpreted by UIs as a hierarchy;
	 */
	public String getName();
}