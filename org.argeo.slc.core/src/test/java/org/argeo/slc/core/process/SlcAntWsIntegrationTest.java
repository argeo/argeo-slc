package org.argeo.slc.core.process;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.ant.AntRunner;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class SlcAntWsIntegrationTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	public void testSimpleRun() {
		// AntRegistryUtil.runAll(getClass().getResource(
		// "/org/argeo/slc/ant/build.xml"), "test", null);

		URL url = getClass().getResource("/org/argeo/slc/ant/build.xml");
		log.info("Run Ant file from URL: " + url);
		AntRunner antRunner = new AntRunner(getContext(), url, "test");

		antRunner.run();
	}

}
