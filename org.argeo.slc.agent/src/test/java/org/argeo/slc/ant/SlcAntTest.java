package org.argeo.slc.ant;

import java.io.File;

import org.argeo.slc.ant.unit.MinimalAntClasspathTestCase;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.springframework.core.io.FileSystemResource;

public class SlcAntTest extends MinimalAntClasspathTestCase {
	// private Log log = LogFactory.getLog(getClass());

	public void testSimpleRun() {
		execute("/org/argeo/slc/ant/build.xml");
	}
}
