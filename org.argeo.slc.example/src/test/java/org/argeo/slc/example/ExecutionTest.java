package org.argeo.slc.example;

import org.argeo.slc.cli.SlcMain;

import junit.framework.TestCase;

public class ExecutionTest extends TestCase {
	public void testSimpleRun() {
		String[] args = { "--mode", "single", "--script",
				"exampleSlcAppli/root/Category1/SubCategory2/build.xml",
				"--runtime", "default" };
		SlcMain.main(args);
	}
}
