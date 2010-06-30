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

package org.argeo.slc.test;

/**
 * Simple statuses.
 * <p>
 * <ul>
 * <li>{@link #PASSED}: the test succeeded</li>
 * <li>{@link #FAILED}: the test could run, but did not reach the expected
 * result</li>
 * <li>{@link #ERROR}: an error during the test run prevented to get a
 * significant information on the tested system.</li>
 * </ul>
 * </p>
 */
public interface TestStatus {
	/** The flag for a passed test: 0 */
	public final static Integer PASSED = 0;
	/** The flag for a failed test: 1 */
	public final static Integer FAILED = 1;
	/**
	 * The flag for a test which could not properly run because of an error
	 * (there is no feedback on the behavior of the tested component): 2
	 */
	public final static Integer ERROR = 2;
	public final static String STATUSSTR_PASSED = "PASSED";
	public final static String STATUSSTR_FAILED = "FAILED";
	public final static String STATUSSTR_ERROR = "ERROR";

}
