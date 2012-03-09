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

import java.util.Date;
import java.util.Map;

/** The result of a test */
public interface TestResult extends TestStatus, TestRunAware {
	public String getUuid();

	/** Adds a part of the result. */
	public void addResultPart(TestResultPart part);

	/**
	 * Marks that the collection of test results is completed and free the
	 * related resources (also closing listeners).
	 */
	public void close();

	/**
	 * The date when this test result was closed. Can be null, which means the
	 * result is not closed.
	 */
	public Date getCloseDate();

	/** Additional arbitrary meta data */
	public Map<String, String> getAttributes();
}
