package org.argeo.slc.it.webapp;

import org.argeo.slc.Condition;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.server.client.SlcServerHttpClient;
import org.argeo.slc.server.unit.AbstractHttpClientTestCase;

public class SimpleScenarioTest extends AbstractHttpClientTestCase {
	public void testSimpleScenario() throws Exception {
		String moduleName = "org.argeo.slc.demo.basic";
		getHttpClient().startFlowDefault(moduleName, "main", null);

		getHttpClient().callServiceSafe(SlcServerHttpClient.LIST_RESULTS, null,
				new Condition<ObjectList>() {

					public Boolean check(ObjectList obj) {
						return obj.getObjects().size() == 3;
					}
				}, null);

		getHttpClient().callServiceSafe(SlcServerHttpClient.LIST_RESULTS, null,
				new Condition<ObjectList>() {

					public Boolean check(ObjectList obj) {
						return obj.getObjects().size() == 3;
					}
				}, null);
	}
}
