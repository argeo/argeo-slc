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
package org.argeo.slc.test;

import org.argeo.slc.SlcException;

/**
 * Exception to throw when a test definition cannot interpret the provided test
 * data.
 */
public class IncompatibleTestDataException extends SlcException {
	static final long serialVersionUID = 1l;

	public IncompatibleTestDataException(TestData testData,
			TestDefinition testDefinition) {
		super("TestData " + testData.getClass()
				+ " is not compatible with TestDefinition "
				+ testDefinition.getClass());
	}

	public IncompatibleTestDataException(TestRun testRun) {
		super("TestData " + ((TestData) testRun.getTestData()).getClass()
				+ " is not compatible with TestDefinition "
				+ ((TestDefinition) testRun.getTestDefinition()).getClass());
	}
}
