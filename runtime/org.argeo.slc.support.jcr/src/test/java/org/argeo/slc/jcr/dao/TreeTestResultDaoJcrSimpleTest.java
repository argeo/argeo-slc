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

package org.argeo.slc.jcr.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.test.tree.TreeTestResultTestUtils;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

public class TreeTestResultDaoJcrSimpleTest extends AbstractSpringTestCase {
	private final static Log log = LogFactory
			.getLog(TreeTestResultDaoJcrSimpleTest.class);

	private TreeTestResultDao ttrDao;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ttrDao = getBean(TreeTestResultDao.class);
		log.debug("Context Initialized");
	}

	@SuppressWarnings("restriction")
	public void testCreate() {
		TreeTestResult ttr = TreeTestResultTestUtils
				.createComplexeTreeTestResult();
		ttrDao.create(ttr);
		TreeTestResult ttrPersisted = ttrDao.getTestResult(ttr.getUuid());
		compareTestResult(ttr, ttrPersisted);
		UnitTestTreeUtil.assertTreeTestResult(ttr, ttrPersisted);

	}

	@SuppressWarnings("restriction")
	public void testCreate2() {
		TreeTestResult ttr = TreeTestResultTestUtils
				.createComplexeTreeTestResult();
		ttrDao.create(ttr);
		TreeTestResult ttrPersisted = ttrDao.getTestResult(ttr.getUuid());
		compareTestResult(ttr, ttrPersisted);
		UnitTestTreeUtil.assertTreeTestResult(ttr, ttrPersisted);

	}

	public static void compareTestResult(final TreeTestResult t1,
			final TreeTestResult t2) {

		assertEquals(t1.getUuid(), t2.getUuid());
		assertEquals(t1.getCloseDate(), t2.getCloseDate());
		assertEquals(t1.getAttachments().size(), t2.getAttachments().size());
		assertEquals(t1.getAttributes().size(), t2.getAttributes().size());
		assertEquals(t1.getElements().size(), t2.getElements().size());
		// resultParts
		assertEquals(t1.getResultParts().size(), t2.getResultParts().size());

		// TODO Add more check.
	}

}
