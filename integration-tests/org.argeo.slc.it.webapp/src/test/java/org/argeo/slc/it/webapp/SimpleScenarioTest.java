package org.argeo.slc.it.webapp;

import org.argeo.slc.Condition;
import org.argeo.slc.core.test.tree.TreeTestResultList;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.server.unit.AbstractHttpClientTestCase;

public class SimpleScenarioTest extends AbstractHttpClientTestCase {
	public void testSimpleScenario() throws Exception {
		// Get agent
		SlcAgentDescriptor agentDescriptor = getHttpClient().waitForOneAgent();
		assertNotNull(agentDescriptor);

		// Launch SLC Execution
		// TODO: don't hardcode tested version
		assertAnswerOk(getHttpClient().startFlow(agentDescriptor.getUuid(),
				"org.argeo.slc.demo.basic", "0.11.4.SNAPSHOT", "main"));

		getHttpClient().callServiceSafe("listResults.service", null,
				new Condition<TreeTestResultList>() {

					public Boolean check(TreeTestResultList obj) {
						return obj.getList().size() == 3;
					}
				}, null);
	}
}
