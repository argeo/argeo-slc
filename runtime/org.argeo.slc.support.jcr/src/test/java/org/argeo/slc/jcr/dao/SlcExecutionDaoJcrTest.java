package org.argeo.slc.jcr.dao;

import java.net.InetAddress;

import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;
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
				.createSlcExecutionWithRealizedFlows();
		slcExecution0.setHost(host);
		slcExecutionDao.create(slcExecution0);
		String uuid = slcExecution0.getUuid();

		SlcExecution slcExecution = SlcExecutionTestUtils
				.createSimpleSlcExecution();
		slcExecution.setUuid(uuid);
		slcExecution.setHost(host);
		slcExecutionDao.update(slcExecution);

		JcrUtils.debug(session.getRootNode());

		SlcExecution slcExecutionPersist = slcExecutionDao
				.getSlcExecution(uuid);
		SlcExecutionTestUtils.assertSlcExecution(slcExecution,
				slcExecutionPersist);
	}
}
