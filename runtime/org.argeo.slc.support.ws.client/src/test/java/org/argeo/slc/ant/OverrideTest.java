package org.argeo.slc.ant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.ant.unit.MinimalAntClasspathTestCase;

public class OverrideTest extends MinimalAntClasspathTestCase {
	private Log log = LogFactory.getLog(getClass());

	public void testSimpleRun() {
		execute("/org/argeo/slc/ant/buildOverride.xml");
	}

	@Override
	public void postExecution(AntExecutionContext executionContext) {
		log.info("Analyzing context after execution...");

		DummyObject dummy1UnModified = executionContext
				.getAntRef("dummy1.unmodified");
		assertEquals("dummy2", dummy1UnModified.getOther().getName());
		assertEquals(2, dummy1UnModified.getChildren().size());
		assertEquals(2, dummy1UnModified.getMap().size());

		DummyObject dummy1Modif1 = executionContext.getAntRef("dummy1.modif1");
		assertEquals("dummy1.modif1", dummy1Modif1.getName());
		assertEquals("dummy3", dummy1Modif1.getOther().getName());

		DummyObject dummy1Modif2 = executionContext.getAntRef("dummy1.modif2");
		assertEquals(1, dummy1Modif2.getChildren().size());
		assertEquals("dummy3", dummy1Modif2.getChildren().get(0).getName());

		DummyObject dummy1Modif3 = executionContext.getAntRef("dummy1.modif3");
		assertEquals(2, dummy1Modif3.getChildren().size());
		assertEquals("dummy3", dummy1Modif3.getChildren().get(0).getName());
		assertEquals("dummy2", dummy1Modif3.getChildren().get(1).getName());

		DummyObject dummy1Modif4 = executionContext.getAntRef("dummy1.modif4");
		assertEquals(2, dummy1Modif4.getChildren().size());
		assertEquals("dummy3", dummy1Modif4.getChildren().get(0).getName());
		assertEquals("dummy1.modif1", dummy1Modif4.getChildren().get(0)
				.getOther().getName());
		assertEquals("dummy2", dummy1Modif4.getChildren().get(1).getName());
		assertEquals(1, dummy1Modif4.getChildren().get(1).getChildren().size());
		assertEquals("dummy3", dummy1Modif4.getChildren().get(1).getChildren()
				.get(0).getName());

		DummyObject dummy1Modif5 = executionContext.getAntRef("dummy1.modif5");
		assertEquals(2, dummy1Modif5.getMap().size());
		assertEquals("dummy3", dummy1Modif5.getMap().get("key1").getName());
		assertEquals("dummy2", dummy1Modif5.getMap().get("key2").getName());
		
		DummyObject dummy1Modif6 = executionContext.getAntRef("dummy1.modif6");
		assertEquals(2, dummy1Modif6.getMap().size());
		assertEquals("dummy2.merged", dummy1Modif6.getMap().get("key1").getName());
		assertEquals("dummy3.merged", dummy1Modif6.getMap().get("key2").getName());
		
	}

	@Override
	protected String getApplicationContextLocation() {
		return inPackage("acOverride.xml");
	}

}
