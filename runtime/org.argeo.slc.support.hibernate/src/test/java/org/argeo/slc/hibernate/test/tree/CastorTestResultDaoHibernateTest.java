/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.hibernate.test.tree;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.hibernate.unit.HibernateTestCase;
import org.argeo.slc.unit.test.tree.TreeTestResultTestUtils;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

public class CastorTestResultDaoHibernateTest extends HibernateTestCase {

	public void testUnmarshallAndCreate() throws Exception {
		TreeTestResult ttr = TreeTestResultTestUtils
				.createCompleteTreeTestResult();

		StringResult result = new StringResult();
		getBean(Marshaller.class).marshal(ttr, result);

		StringSource source = new StringSource(result.toString());
		TreeTestResult ttrUnm = (TreeTestResult) getBean(Unmarshaller.class)
				.unmarshal(source);

		for (TreeSPath path : ttrUnm.getResultParts().keySet()) {
			log.debug("Path: " + path.getClass() + ": " + path);
		}

		TreeTestResultDao ttrDao = getBean(TreeTestResultDao.class);
		ttrDao.create(ttrUnm);
		TreeTestResult ttrPersist = ttrDao.getTestResult(ttr.getUuid());

		TreeTestResultDaoHibernateTest.assertInHibernate(
				getHibernateTemplate(), ttr, ttrPersist);
	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/withCastor.xml";
	}

}
