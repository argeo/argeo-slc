package org.argeo.slc.ant.unit;

import java.io.File;
import java.util.UUID;

import org.argeo.slc.ant.AntConstants;
import org.argeo.slc.ant.AntExecutionContext;
import org.argeo.slc.ant.AntSlcApplication;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcExecutionOutput;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.springframework.core.io.FileSystemResource;

public class MinimalAntClasspathTestCase extends AbstractSpringTestCase
		implements SlcExecutionOutput<AntExecutionContext> {
	protected void execute(String scriptPath) {
		AntSlcApplication slcApp = new AntSlcApplication();
		slcApp.setRootDir(new FileSystemResource(new File("src/test/resources")
				.getAbsolutePath()
				+ File.separator));
		slcApp.setWorkDir(new File(System.getProperty("java.io.tmpdir")));
		slcApp.setParentContext(getContext());

		SlcExecution slcExecution = new SlcExecution();
		slcExecution.setUuid(UUID.randomUUID().toString());
		slcExecution.getAttributes().put(AntConstants.EXECATTR_ANT_FILE,
				scriptPath);
		slcExecution.setUser("user");

		slcApp.execute(slcExecution, null, null, this);
	}

	/** to be overridden */
	public void postExecution(AntExecutionContext executionContext) {
	}

}
