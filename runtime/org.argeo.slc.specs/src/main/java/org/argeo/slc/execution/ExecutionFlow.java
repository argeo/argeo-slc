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

public interface ExecutionFlow extends Runnable {
	public Object getParameter(String key);

	public Boolean isSetAsParameter(String key);

	public ExecutionSpec getExecutionSpec();

	public String getName();

	public String getPath();

	/**
	 * Actually performs the execution of the Runnable. This method should never
	 * be called directly. The implementation should provide a reasonable
	 * default, but it is meant to be intercepted either to analyze what is run
	 * or to override the default behavior.
	 */
	public void doExecuteRunnable(Runnable runnable);
}
