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
package org.argeo.slc.core.execution;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.argeo.slc.execution.ExecutionContext;
import org.springframework.core.io.Resource;

public class FileExecutionResourcesTest extends TestCase {
	public void testGetWritableFile() throws Exception {
		FileExecutionResources executionResources = new FileExecutionResources();
		ExecutionContext executionContext = new MapExecutionContext();
		executionResources.setExecutionContext(executionContext);

		String expected = "TEST";
		String reached = "";
		try {
			// Resource
			Resource resource = executionResources
					.getWritableResource("subdir1/textRes.txt");
			assertTrue(resource.getFile().getParentFile().exists());
			assertFalse(resource.getFile().exists());
			FileUtils.writeStringToFile(resource.getFile(), expected);
			reached = FileUtils.readFileToString(resource.getFile());
			assertEquals(expected, reached);

			// File
			File file = executionResources.getFile("subdir2/textFile.txt");
			assertFalse(file.getParentFile().exists());
			assertFalse(file.exists());
			FileUtils.writeStringToFile(file, expected);
			reached = FileUtils.readFileToString(file);
			assertEquals(expected, reached);
		} finally {
			if (executionResources.getBaseDir() != null
					&& executionResources.getBaseDir().exists())
				FileUtils.deleteDirectory(executionResources.getBaseDir());
		}

	}
}
