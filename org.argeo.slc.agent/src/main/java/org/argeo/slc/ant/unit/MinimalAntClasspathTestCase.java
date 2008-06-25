package org.argeo.slc.ant.unit;

import java.io.File;

import org.argeo.slc.ant.AntConstants;
import org.argeo.slc.ant.AntSlcApplication;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.springframework.core.io.FileSystemResource;

public class MinimalAntClasspathTestCase extends AbstractSpringTestCase {
	protected void execute(String scriptPath) {
		AntSlcApplication slcApp = new AntSlcApplication();
		slcApp.setRootDir(new FileSystemResource(new File("src/test/resources")
				.getAbsolutePath()
				+ File.separator));
		slcApp.setWorkDir(new File(System.getProperty("java.io.tmpdir")));
		slcApp.setParentContext(getContext());

		SlcExecution slcExecution = new SlcExecution();
		slcExecution.getAttributes().put(AntConstants.EXECATTR_ANT_FILE,
				scriptPath);

		slcApp.execute(slcExecution, null, null, null);
	}

}
