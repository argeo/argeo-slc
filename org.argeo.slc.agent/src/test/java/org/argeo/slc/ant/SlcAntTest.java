package org.argeo.slc.ant;

import java.io.File;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.runtime.SimpleSlcRuntime;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.springframework.core.io.FileSystemResource;

public class SlcAntTest extends AbstractSpringTestCase {
	private Log log = LogFactory.getLog(getClass());

	public void testSimpleRun() {
		// AntRegistryUtil.runAll(getClass().getResource(
		// "/org/argeo/slc/ant/build.xml"), "test", null);

		URL url = getClass().getResource("/org/argeo/slc/ant/build.xml");
		log.info("Run Ant file from URL: " + url);

		// AntRunner antRunner = new AntRunner(getContext(), url, "test");
		// antRunner.run();

		AntSlcApplication slcApp = new AntSlcApplication();
		slcApp.setSlcRuntime(new SimpleSlcRuntime(getContext()));
		slcApp.setRootDir(new FileSystemResource(new File("src/test/resources")
				.getAbsolutePath()
				+ File.separator));

		SlcExecution slcExecution = new SlcExecution();
		slcExecution.getAttributes().put(SlcAntConstants.EXECATTR_ANT_FILE,
				url.toString());

		slcApp.execute(slcExecution, null, null);
	}

}
