package org.argeo.slc.core.execution;


public class ResourceTest extends AbstractExecutionFlowTestCase {
	//private final static Log log = LogFactory.getLog(ResourceTest.class);

	private final String defFile = "resourceTest.xml";

	public void testResourceSimple() throws Exception {
		configureAndExecuteSlcFlow(defFile, "resourceSimple");
	}

	public void testResourceOverridden() throws Exception {
		configureAndExecuteSlcFlow(defFile, "resourceOverridden");
	}

}
