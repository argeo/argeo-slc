package org.argeo.slc.ant;

import org.argeo.slc.ant.unit.MinimalAntClasspathTestCase;

public class SlcAntTest extends MinimalAntClasspathTestCase {
	// private Log log = LogFactory.getLog(getClass());

	public void testSimpleRun() {
		execute("/org/argeo/slc/ant/build.xml");
	}
}
