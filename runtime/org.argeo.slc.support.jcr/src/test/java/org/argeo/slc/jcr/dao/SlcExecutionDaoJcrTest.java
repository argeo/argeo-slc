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

import java.net.InetAddress;

import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.process.SlcExecutionTestUtils;

public class SlcExecutionDaoJcrTest extends AbstractSpringTestCase {
	private SlcExecutionDao slcExecutionDao;
	private Session session;
	private String host;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		slcExecutionDao = getBean(SlcExecutionDao.class);
		session = getBean(Session.class);
		host = InetAddress.getLocalHost().getCanonicalHostName();
	}

	public void testCreate() throws Exception {
		SlcExecution slcExecution0 = SlcExecutionTestUtils
				.createSimpleSlcExecution();
		slcExecution0.setHost(host);
		slcExecutionDao.create(slcExecution0);

		SlcExecution slcExecution = SlcExecutionTestUtils
				.createSlcExecutionWithRealizedFlows();
		slcExecution.setHost(host);
		String uuid = slcExecution.getUuid();
		slcExecutionDao.create(slcExecution);

		JcrUtils.debug(session.getRootNode());

		SlcExecution slcExecutionPersist = slcExecutionDao
				.getSlcExecution(uuid);
		SlcExecutionTestUtils.assertSlcExecution(slcExecution,
				slcExecutionPersist);
	}

	public void testUpdate() throws Exception {
		SlcExecution slcExecution0 = SlcExecutionTestUtils
				.createSimpleSlcExecution();
		slcExecution0.setHost(host);
		slcExecutionDao.create(slcExecution0);
		String uuid = slcExecution0.getUuid();

		SlcExecution slcExecution = SlcExecutionTestUtils
				.createSlcExecutionWithRealizedFlows();
		slcExecution.setUuid(uuid);
		slcExecution.setHost(host);
		slcExecution.getSteps().add(new SlcExecutionStep("My log"));
		slcExecutionDao.update(slcExecution);

		JcrUtils.debug(session.getRootNode());

		SlcExecution slcExecutionPersist = slcExecutionDao
				.getSlcExecution(uuid);
		SlcExecutionTestUtils.assertSlcExecution(slcExecution,
				slcExecutionPersist);
	}

	// // FIXME
	// protected void tearDown() {
	// try {
	// super.tearDown();
	// } catch (Exception e) {
	// log.debug("pbs Remain while closing jcr test env.");
	// log.debug("Exc Name : " + e.getClass().getName());
	// }
	// }
}
