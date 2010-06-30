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

package org.argeo.slc.dao.test;

import java.util.Date;
import java.util.List;

import org.argeo.slc.test.TestResult;

/**
 * The dao for <code>TestResult</code>.
 * 
 * @see TestResult
 */
public interface TestResultDao<T extends TestResult> {
	/** Gets a test result based on its id. */
	public T getTestResult(String uuid);

	/** Persists a new test result. */
	public void create(TestResult testResult);

	/** Updates an already persisted test result. */
	public void update(TestResult testResult);

	/** Lists all test results. */
	public List<T> listTestResults();

	public void close(String id, Date closeDate);
}
