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

package org.argeo.slc.core.execution;

import java.io.File;

public class FileExecutionResourcesSpringTest extends
		AbstractExecutionFlowTestCase {
	private String basePath = FileExecutionResources.DEFAULT_EXECUTION_RESOURCES_TMP_PATH;

	public void testSimple() throws Exception {
		File file = getFile("subdir/writeTo");
		try {
			assertFalse(file.exists());
			configureAndExecuteSlcFlow("executionResources.xml",
					"executionResources.simple");
			assertTrue(file.exists());
		} finally {
			file.deleteOnExit();
		}
	}

	public void testPlaceholderPass() throws Exception {
		File file = getFile("subdir/60");
		try {
			assertFalse(file.exists());
			configureAndExecuteSlcFlow("executionResources.xml",
					"executionResources.placeholderPass");
			assertTrue(file.exists());
		} finally {
			file.deleteOnExit();
		}
	}

	/**
	 * Test that it generate the wrong file because of issue when using
	 * execution placeholder in contructor-arg
	 */
	public void testPlaceholderFail() throws Exception {
		File file = getFile("subdir/@{var}");
		try {
			assertFalse(file.exists());
			configureAndExecuteSlcFlow("executionResources.xml",
					"executionResources.placeholderFail");
			assertTrue(file.exists());
		} finally {
			file.deleteOnExit();
		}
	}

	protected File getFile(String relativePath) {
		return new File(basePath + File.separator
				+ relativePath.replace('/', File.separatorChar));
	}
}
