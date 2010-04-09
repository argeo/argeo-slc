package org.argeo.slc.jcr.dao;

import java.net.InetAddress;
import java.util.UUID;

import org.argeo.slc.dao.runtime.SlcAgentDescriptorDao;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class SlcAgentDescriptorDaoJcrTest extends AbstractSpringTestCase {
	// private final static Log log = LogFactory
	// .getLog(SlcAgentDescriptorDaoJcrTest.class);

	private SlcAgentDescriptorDao slcAgentDescriptorDao;
	private String host;
	

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		slcAgentDescriptorDao = getBean(SlcAgentDescriptorDao.class);
		host = InetAddress.getLocalHost().getCanonicalHostName();
	}

	public void testZero() throws Exception {
		SlcAgentDescriptor slcAgentDescriptor0 = new SlcAgentDescriptor();
		slcAgentDescriptor0.setHost(host);
		slcAgentDescriptor0.setUuid(UUID.randomUUID().toString());
	}

	public void testCreate() throws Exception {
		SlcAgentDescriptor slcAgentDescriptor0 = new SlcAgentDescriptor();
		slcAgentDescriptor0.setHost(host);

		String agentID = UUID.randomUUID().toString();
		slcAgentDescriptor0.setUuid(agentID);
		slcAgentDescriptorDao.create(slcAgentDescriptor0);

		//JcrUtils.debug(session.getRootNode());
		
		SlcAgentDescriptor slcAgentDescriptor1 = slcAgentDescriptorDao.getAgentDescriptor(agentID);
		//log.debug("expected agentID :"+agentID+ " . Retrieved one : "+slcAgentDescriptor1.getUuid());
		//TODO : compare retrieved AgentDescriptor with expected one.
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

		//List<SlcAgentDescriptor> list = slcAgentDescriptorDao.listSlcAgentDescriptors();
	}

}
