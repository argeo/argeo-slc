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
