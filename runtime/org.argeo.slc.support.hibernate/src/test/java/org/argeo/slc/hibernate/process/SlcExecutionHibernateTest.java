package org.argeo.slc.hibernate.process;

import java.sql.SQLException;
import java.util.List;

import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionStep;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.hibernate.unit.HibernateTestCase;
import org.argeo.slc.unit.process.SlcExecutionTestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

public class SlcExecutionHibernateTest extends HibernateTestCase {

	public void testSave() {
		SlcExecutionDao dao = getBean(SlcExecutionDao.class);

		SlcExecution slcExec = SlcExecutionTestUtils.createSimpleSlcExecution();
		slcExec.getSteps().add(new SlcExecutionStep("A log line"));
		slcExec.getSteps().add(new SlcExecutionStep("Two log\nlines"));

		dao.create(slcExec);

		SlcExecution slcExecPersisted = dao.getSlcExecution(slcExec.getUuid());
		assertSlcExecution(slcExec, slcExecPersisted);
	}

	public void testModify() {
		SlcExecutionDao dao = getBean(SlcExecutionDao.class);

		// slcExecution Creation
		SlcExecution slcExec = SlcExecutionTestUtils.createSimpleSlcExecution();
		slcExec.getSteps().add(new SlcExecutionStep("A log line"));
		slcExec.getSteps().add(new SlcExecutionStep("Two log\nlines"));

		dao.create(slcExec);

		// slcExecution retrieval and update
		final SlcExecution slcExecRetrieved = dao.getSlcExecution(slcExec
				.getUuid());

		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				session.refresh(slcExecRetrieved);
				List<String> logLineListStep0 = slcExecRetrieved.getSteps()
						.get(0).getLogLines();
				for (String logLine : logLineListStep0)
					logLine = logLine + "appended Log text";

				slcExecRetrieved.getSteps().get(0)
						.setLogLines(logLineListStep0);
				slcExecRetrieved.getSteps().add(
						new SlcExecutionStep("Three \n log \n lines"));
				return null;
			}
		});

		dao.update(slcExecRetrieved);

		// updated slcExecution retrieval and comparison
		SlcExecution slcExecUpdated = dao.getSlcExecution(slcExec.getUuid());

		assertSlcExecution(slcExecRetrieved, slcExecUpdated);
	}

	public void assertSlcExecution(final SlcExecution expected,
			final SlcExecution persisted) {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				session.refresh(persisted);
				SlcExecutionTestUtils.assertSlcExecution(expected, persisted);
				return null;
			}
		});
	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

}
