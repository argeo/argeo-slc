package org.argeo.slc.example.appli;

import org.argeo.slc.cli.SlcMain;

import junit.framework.TestCase;

public class SlcMainTest extends TestCase {
	public void testSimpleRunFromMain() {
		String[] args = { "--mode", "single", "--script",
				"exampleSlcAppli/root/Category1/SubCategory2/build.xml",
				"--runtime", "default" };
		SlcMain.main(args);
	}

}
