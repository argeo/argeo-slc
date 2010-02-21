package org.argeo.slc.jcr.dao;

import java.net.InetAddress;

import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.process.SlcExecutionTestUtils;

public class SlcExecutionDaoJcrTest extends AbstractSpringTestCase {
	public void testCreate() throws Exception {
		SlcExecutionDao slcExecutionDao = getBean(SlcExecutionDao.class);
		Session session = getBean(Session.class);

		// SlcExecution slcExecution = SlcExecutionTestUtils
		// .createSimpleSlcExecution();
		SlcExecution slcExecution = SlcExecutionTestUtils
				.createSlcExecutionWithRealizedFlows();
		slcExecution.setHost(InetAddress.getLocalHost().getCanonicalHostName());
		String uuid = slcExecution.getUuid();
		slcExecutionDao.create(slcExecution);

		JcrUtils.debug(session.getRootNode());

		SlcExecution slcExecutionPersist = slcExecutionDao
				.getSlcExecution(uuid);
		SlcExecutionTestUtils.assertSlcExecution(slcExecution,
				slcExecutionPersist);
	}
}
