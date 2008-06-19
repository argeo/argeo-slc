package org.argeo.slc.example;

import org.argeo.slc.cli.SlcMain;

import junit.framework.TestCase;

public class ExecutionTest extends TestCase {
	public void testSimpleRun() {
		String[] args = {
				"--mode",
				"single",
				"--script",
				"/home/mbaudier/workspace/org.argeo.slc.example/exampleSlcAppli/root/Category1/SubCategory2/build.xml" };
		SlcMain.main(args);
	}
}
