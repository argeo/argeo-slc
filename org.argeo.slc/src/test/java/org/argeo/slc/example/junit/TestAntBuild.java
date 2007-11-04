package org.argeo.slc.example.junit;

import java.io.File;

import junit.framework.TestCase;

import org.argeo.slc.ant.AntRegistryUtil;

public class TestAntBuild extends TestCase {
	public void testAllRunSimple() {
		File slcBaseDir = new File("./src/test/slc").getAbsoluteFile();

		File antFile = new File(slcBaseDir.getPath()
				+ "/root/Category1/SubCategory2/build.xml");
		AntRegistryUtil.runAll(antFile);

	}
}
