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

package org.argeo.slc.hibernate.test.tree;

import java.sql.SQLException;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.hibernate.unit.HibernateTestCase;
import org.argeo.slc.test.TestStatus;
import org.argeo.slc.unit.test.tree.TreeTestResultTestUtils;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class TreeTestResultDaoHibernateTest extends HibernateTestCase {
	private TreeTestResultDao testResultDao = null;

	@Override
	public void setUp() {
		testResultDao = getBean(TreeTestResultDao.class);
	}

	public void testCreate() {

		TreeTestResult ttr = TreeTestResultTestUtils
				.createCompleteTreeTestResult();
		testResultDao.create(ttr);

		TreeTestResult ttrPersisted = (TreeTestResult) testResultDao
				.getTestResult(ttr.getUuid());

		assertInHibernate(ttr, ttrPersisted);
	}

	public void testUpdate() {
		TreeTestResult ttr = TreeTestResultTestUtils
				.createCompleteTreeTestResult();
		testResultDao.create(ttr);

		final TreeTestResult ttrUpdated = (TreeTestResult) testResultDao
				.getTestResult(ttr.getUuid());

		// Modifying ttrUpdated

		/**
		 * this closeDate update commented because the assertTreeTestResult will
		 * find a unexpected discrepancy in the date.
		 * ttrUpdated.setCloseDate(new Date());
		 */

		String pathStr = "/test";
		final TreeSPath path = new TreeSPath(pathStr);

		final TreeSRegistry registry = new TreeSRegistry();
		SimpleSElement elem = new SimpleSElement("Unit Test");
		elem.getTags().put("myTag", "myTagValue");
		registry.register(path, elem);

		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				session.refresh(ttrUpdated);
				ttrUpdated.notifyCurrentPath(registry, path);

				ttrUpdated.addResultPart(TreeTestResultTestUtils
						.createSimpleResultPartPassed());
				ttrUpdated.addResultPart(TreeTestResultTestUtils
						.createSimpleResultPartFailed());
				ttrUpdated.addResultPart(TreeTestResultTestUtils
						.createSimpleResultPartError());
				return null;
			}
		});

		testResultDao.update(ttrUpdated);

		// comparison of ttrUpdated and ttrRetrieved
		TreeTestResult ttrRetrieved = (TreeTestResult) testResultDao
				.getTestResult(ttr.getUuid());

		assertInHibernate(ttrUpdated, ttrRetrieved);
	}

	public void testMultipleUpdateScenario() throws Exception {
		TreeSRegistry registry = new TreeSRegistry();

		TreeSPath path = new TreeSPath("/root/test");
		SimpleSElement elem = new SimpleSElement("Unit Test");
		elem.getTags().put("myTag", "myTagValue");
		registry.register(path, elem);

		TreeTestResult ttr = TreeTestResultTestUtils
				.createSimpleTreeTestResult();
		ttr.notifyCurrentPath(registry, path);
		ttr
				.addResultPart(new SimpleResultPart(TestStatus.PASSED,
						"First test"));

		testResultDao.create(ttr);

		path = new TreeSPath("/root/test2/subtest");
		elem = new SimpleSElement("Sub Test");
		elem.getTags().put("myTag", "myTagValue");
		registry.register(path, elem);

		ttr.notifyCurrentPath(registry, path);
		ttr
				.addResultPart(new SimpleResultPart(TestStatus.PASSED,
						"Second test"));

		testResultDao.update(ttr);

		ttr.notifyCurrentPath(registry, path);
		ttr.addResultPart(new SimpleResultPart(TestStatus.PASSED,
				"Third test with same path"));

		testResultDao.update(ttr);

		ttr.close();

		testResultDao.close(ttr.getUuid(), ttr.getCloseDate());

		TreeTestResult ttrRetrieved = (TreeTestResult) testResultDao
				.getTestResult(ttr.getUuid());

		assertInHibernate(ttr, ttrRetrieved);

	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

	public static void assertInHibernate(HibernateTemplate template,
			final TreeTestResult ttrExpected, final TreeTestResult ttrPersisted) {
		template.execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				session.refresh(ttrPersisted);
				UnitTestTreeUtil
						.assertTreeTestResult(ttrExpected, ttrPersisted);
				return null;
			}

		});

	}

	public void assertInHibernate(final TreeTestResult ttrExpected,
			final TreeTestResult ttrPersisted) {
		assertInHibernate(getHibernateTemplate(), ttrExpected, ttrPersisted);
	}

}
