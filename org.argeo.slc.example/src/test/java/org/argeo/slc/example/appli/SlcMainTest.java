package org.argeo.slc.example.appli;

import org.argeo.slc.ant.unit.SlcAntAppliTestCase;
import org.argeo.slc.cli.SlcMain;

import junit.framework.TestCase;

public class SlcMainTest extends SlcAntAppliTestCase {
	public void testSimpleRunFromMain() {
		String[] args = { "--mode", "single", "--script",
				getAbsoluteScript("/Category1/SubCategory2/build.xml"),
				"--runtime", "default" };
		SlcMain.main(args);
	}

}
