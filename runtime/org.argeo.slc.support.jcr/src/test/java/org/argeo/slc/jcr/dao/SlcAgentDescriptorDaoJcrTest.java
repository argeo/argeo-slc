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
package org.argeo.slc.jcr.dao;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.util.UUID;

import javax.jcr.Session;

import org.argeo.slc.dao.runtime.SlcAgentDescriptorDao;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class SlcAgentDescriptorDaoJcrTest extends AbstractSpringTestCase {
	// private final static Log log = LogFactory
	// .getLog(SlcAgentDescriptorDaoJcrTest.class);

	private SlcAgentDescriptorDao slcAgentDescriptorDao;
	private String host = "localhost";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		slcAgentDescriptorDao = getBean(SlcAgentDescriptorDao.class);
	}

	public void testZero() throws Exception {
		SlcAgentDescriptor slcAgentDescriptor0 = new SlcAgentDescriptor();
		slcAgentDescriptor0.setHost(host);
		slcAgentDescriptor0.setUuid(UUID.randomUUID().toString());
	}

	public void testExportXml() throws Exception {
		SlcAgentDescriptor slcAgentDescriptor0 = new SlcAgentDescriptor();
		slcAgentDescriptor0.setHost(host);

		String agentID = UUID.randomUUID().toString();
		slcAgentDescriptor0.setUuid(agentID);
		slcAgentDescriptorDao.create(slcAgentDescriptor0);

		Session session = getBean(Session.class);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		session.exportDocumentView("/slc", out, true, false);
		log.debug("\n\n"+new String(out.toByteArray())+"\n\n");

	}

	public void testCreate() throws Exception {
		SlcAgentDescriptor slcAgentDescriptor0 = new SlcAgentDescriptor();
		slcAgentDescriptor0.setHost(host);

		String agentID = UUID.randomUUID().toString();
		slcAgentDescriptor0.setUuid(agentID);
		slcAgentDescriptorDao.create(slcAgentDescriptor0);

		// JcrUtils.debug(session.getRootNode());

		SlcAgentDescriptor slcAgentDescriptor1 = slcAgentDescriptorDao
				.getAgentDescriptor(agentID);
		// log.debug("expected agentID :"+agentID+
		// " . Retrieved one : "+slcAgentDescriptor1.getUuid());
		// TODO : compare retrieved AgentDescriptor with expected one.
		assertEquals(agentID, slcAgentDescriptor1.getUuid());

		slcAgentDescriptorDao.delete(agentID);

	}

	public void testList() throws Exception {
		SlcAgentDescriptor slcAgentDescriptor0 = new SlcAgentDescriptor();
		SlcAgentDescriptor slcAgentDescriptor1 = new SlcAgentDescriptor();
		SlcAgentDescriptor slcAgentDescriptor2 = new SlcAgentDescriptor();

		slcAgentDescriptor0.setHost(host);
		slcAgentDescriptor1.setHost(host);
		slcAgentDescriptor2.setHost(host);

		String agentID = UUID.randomUUID().toString();
		String agentID1 = UUID.randomUUID().toString();
		String agentID2 = UUID.randomUUID().toString();

		slcAgentDescriptor0.setUuid(agentID);
		slcAgentDescriptor1.setUuid(agentID1);
		slcAgentDescriptor2.setUuid(agentID2);

		slcAgentDescriptorDao.create(slcAgentDescriptor0);
		slcAgentDescriptorDao.create(slcAgentDescriptor1);
		slcAgentDescriptorDao.create(slcAgentDescriptor2);

		// List<SlcAgentDescriptor> list =
		// slcAgentDescriptorDao.listSlcAgentDescriptors();
	}

}
