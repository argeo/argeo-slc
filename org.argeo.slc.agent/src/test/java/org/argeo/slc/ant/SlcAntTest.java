package org.argeo.slc.ant;

import java.io.File;

import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.springframework.core.io.FileSystemResource;

public class SlcAntTest extends AbstractSpringTestCase {
	// private Log log = LogFactory.getLog(getClass());

	public void testSimpleRun() {
		AntSlcApplication slcApp = new AntSlcApplication();
		slcApp.setRootDir(new FileSystemResource(new File("src/test/resources")
				.getAbsolutePath()
				+ File.separator));
		slcApp.setWorkDir(new File(System.getProperty("java.io.tmpdir")));
		slcApp.setRuntimeContext(getContext());

		SlcExecution slcExecution = new SlcExecution();
		slcExecution.getAttributes().put(SlcAntConstants.EXECATTR_ANT_FILE,
				"/org/argeo/slc/ant/build.xml");

		slcApp.execute(slcExecution, null, null);
	}

}
