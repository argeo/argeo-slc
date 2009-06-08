package org.argeo.slc.it.webapp;

import org.argeo.slc.Condition;
import org.argeo.slc.core.test.tree.TreeTestResultList;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.server.client.SlcServerHttpClient;
import org.argeo.slc.server.unit.AbstractHttpClientTestCase;

public class SimpleScenarioTest extends AbstractHttpClientTestCase {
	public void testSimpleScenario() throws Exception {
		String moduleName = "org.argeo.slc.demo.basic";
		SlcExecution slcExecution = getHttpClient().startFlowDefault(
				moduleName, "main", null);

		getHttpClient().callServiceSafe(SlcServerHttpClient.LIST_RESULTS, null,
				new Condition<TreeTestResultList>() {

					public Boolean check(TreeTestResultList obj) {
						return obj.getList().size() == 3;
					}
				}, null);
		
		
		getHttpClient().callServiceSafe(SlcServerHttpClient.LIST_RESULTS, null,
				new Condition<TreeTestResultList>() {

					public Boolean check(TreeTestResultList obj) {
						return obj.getList().size() == 3;
					}
				}, null);
	}
}
