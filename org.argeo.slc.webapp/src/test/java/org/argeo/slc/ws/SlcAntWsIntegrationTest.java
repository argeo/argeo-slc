package org.argeo.slc.ws;

import org.argeo.slc.ant.unit.MinimalAntClasspathTestCase;

public class SlcAntWsIntegrationTest extends MinimalAntClasspathTestCase {
	public void testSimpleRun() {
		execute("/org/argeo/slc/ant/build.xml");
	}

}
