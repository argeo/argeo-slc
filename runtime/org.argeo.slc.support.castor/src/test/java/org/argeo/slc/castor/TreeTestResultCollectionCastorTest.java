/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.castor;

import static org.argeo.slc.unit.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;
import org.springframework.xml.transform.StringResult;

public class TreeTestResultCollectionCastorTest extends AbstractCastorTestCase {
	public void testMarshUnmarsh() throws Exception {
		TreeTestResult ttr = createCompleteTreeTestResult();
		TreeTestResult ttr2 = createCompleteTreeTestResult();

		TreeTestResultCollection ttrc = new TreeTestResultCollection();
		ttrc.setId("testCollection");
		ttrc.getResults().add(ttr);
		ttrc.getResults().add(ttr2);

		StringResult xml = marshalAndValidate(ttrc);

		TreeTestResultCollection ttrcUnm = unmarshal(xml);

		assertEquals(ttrc.getId(), ttrcUnm.getId());
		assertEquals(ttrc.getResults().size(), ttrcUnm.getResults().size());
		for (TreeTestResult ttrT : ttrc.getResults()) {
			if (ttrT.getUuid().equals(ttr.getUuid()))
				UnitTestTreeUtil.assertTreeTestResult(ttr, ttrT);
			else
				UnitTestTreeUtil.assertTreeTestResult(ttr2, ttrT);
		}
	}
}
