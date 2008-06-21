package org.argeo.slc.ant.unit;

import junit.framework.TestCase;

import org.argeo.slc.ant.AntExecutionContext;
import org.argeo.slc.cli.DefaultSlcRuntime;
import org.argeo.slc.runtime.SlcExecutionOutput;

public abstract class SlcAntAppliTestCase extends TestCase implements
		SlcExecutionOutput<AntExecutionContext> {

	/** To be overriden */
	public void postExecution(AntExecutionContext executionContext) {

	}

	protected String getRootDir() {
		return System.getProperty("slc.rootDir", "src/main/slc/root");
	}

	protected String getAbsoluteScript(String relative) {
		return getRootDir() + '/' + relative;
	}

	protected void execute(String relativeScript) {
		execute(relativeScript, null);
	}

	protected void execute(String relativeScript, String targets) {
		new DefaultSlcRuntime().executeScript(
				getAbsoluteScript(relativeScript), targets, this);
	}

}
