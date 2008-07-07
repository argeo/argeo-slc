package org.argeo.slc.example.appli;

import org.argeo.slc.ant.unit.AntSlcApplicationTestCase;
import org.argeo.slc.cli.SlcMain;

public class SlcMainTest extends AntSlcApplicationTestCase {
	public void testSimpleRunFromMain() {
		String[] args = { "--mode", "single", "--script",
				getAbsoluteScript("/Category1/SubCategory2/build.xml"),
				"--runtime", "default" };
		SlcMain.main(args);
	}

}
