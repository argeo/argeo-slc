package org.argeo.slc.example.junit;

import java.io.File;

import org.argeo.slc.ant.AntRegistryUtil;

import junit.framework.TestCase;

public class TestAntBuild extends TestCase {
	public void testAllRunSimple() {
		File slcBaseDir = new File("./src/test/slc").getAbsoluteFile();
		System.setProperty("log4j.configuration", "file:///"+slcBaseDir.getPath()
				+ "/conf/log4j.properties");

		File antFile = new File(slcBaseDir.getPath()
				+ "/root/Category1/SubCategory2/build.xml");
		AntRegistryUtil.runAll(antFile);
	}
}
