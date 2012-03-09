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
package org.argeo.slc.test;

/** Listener to the operations on a test result. */
public interface TestResultListener<T extends TestResult> {
	/** Notified when a part was added to a test result. */
	public void resultPartAdded(T testResult, TestResultPart testResultPart);

	/** Stops listening and release the related resources. */
	public void close(T testResult);
}
