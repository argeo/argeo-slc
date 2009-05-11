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
