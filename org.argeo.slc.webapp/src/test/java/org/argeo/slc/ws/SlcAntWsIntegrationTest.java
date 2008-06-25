package org.argeo.slc.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.ant.unit.MinimalAntClasspathTestCase;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class SlcAntWsIntegrationTest extends MinimalAntClasspathTestCase {
	public void testSimpleRun() {
		execute("/org/argeo/slc/ant/build.xml");
	}

}
