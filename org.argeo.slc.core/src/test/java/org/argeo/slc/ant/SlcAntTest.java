package org.argeo.slc.ant;

import junit.framework.TestCase;

public class SlcAntTest extends TestCase {

	public void testSimpleRun() {
		AntRegistryUtil.runAll(getClass().getResource(
				"/org/argeo/slc/ant/build.xml"), "test", null);
	}

}
