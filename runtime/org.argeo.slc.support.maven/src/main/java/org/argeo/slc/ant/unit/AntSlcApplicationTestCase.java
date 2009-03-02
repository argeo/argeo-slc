package org.argeo.slc.ant.unit;

import java.io.File;

import junit.framework.TestCase;

import org.argeo.slc.ant.AntExecutionContext;
import org.argeo.slc.ant.AntSlcRuntime;
import org.argeo.slc.runtime.SlcExecutionOutput;

public abstract class AntSlcApplicationTestCase extends TestCase implements
		SlcExecutionOutput<AntExecutionContext> {

	/** To be overriden */
	public void postExecution(AntExecutionContext executionContext) {

	}

	protected String getRootDir() {
		String rootDirPath =  System.getProperty("slc.rootDir", "src/slc/root");
		if(!new File(rootDirPath).exists())
			rootDirPath = "src/main/slc/root";// try older convention
		return rootDirPath;
	}

	protected String getAbsoluteScript(String relative) {
		return getRootDir() + '/' + relative;
	}

	protected void execute(String relativeScript) {
		execute(relativeScript, null);
	}

	protected void execute(String relativeScript, String targets) {
		new AntSlcRuntime().executeScript(
				getAbsoluteScript(relativeScript), targets, this);
	}

}
