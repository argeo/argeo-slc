package org.argeo.slc.hibernate.process;

import java.util.List;

import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionStep;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.process.SlcExecutionTestUtils;

public class SlcExecutionHibernateTest extends AbstractSpringTestCase {

	public void testSave() {
		SlcExecutionDao dao = getBean(SlcExecutionDao.class);

		SlcExecution slcExec = SlcExecutionTestUtils.createSimpleSlcExecution();
		slcExec.getSteps().add(new SlcExecutionStep("LOG", "A log line"));
		slcExec.getSteps().add(new SlcExecutionStep("LOG", "Two log\nlines"));

		dao.create(slcExec);

		SlcExecution slcExecPersisted = dao.getSlcExecution(slcExec.getUuid());
		SlcExecutionTestUtils.assertSlcExecution(slcExec, slcExecPersisted);
		assertEquals(2, slcExecPersisted.getSteps().size());
		SlcExecutionTestUtils.assertSlcExecutionStep(slcExec.getSteps().get(0),
				slcExecPersisted.getSteps().get(0));
		SlcExecutionTestUtils.assertSlcExecutionStep(slcExec.getSteps().get(1),
				slcExecPersisted.getSteps().get(1));
	}

	public void testModify() {
		SlcExecutionDao dao = getBean(SlcExecutionDao.class);

		// slcExecution Creation
		SlcExecution slcExec = SlcExecutionTestUtils.createSimpleSlcExecution();
		slcExec.getSteps().add(new SlcExecutionStep("LOG", "A log line"));
		slcExec.getSteps().add(new SlcExecutionStep("LOG", "Two log\nlines"));

		dao.create(slcExec);

		// slcExecution retrieval and update
		SlcExecution slcExecRetrieved = dao.getSlcExecution(slcExec.getUuid());

		List<String> logLineListStep0 = slcExecRetrieved.getSteps().get(0)
				.getLogLines();
		for (String logLine : logLineListStep0)
			logLine = logLine + "appended Log text";

		slcExecRetrieved.getSteps().get(0).setLogLines(logLineListStep0);
		slcExecRetrieved.getSteps().add(
				new SlcExecutionStep("LOG", "Three \n log \n lines"));

		dao.update(slcExecRetrieved);

		// updated slcExecution retrieval and comparison
		SlcExecution slcExecUpdated = dao.getSlcExecution(slcExec.getUuid());

		SlcExecutionTestUtils.assertSlcExecution(slcExecRetrieved,
				slcExecUpdated);
		assertEquals(3, slcExecUpdated.getSteps().size());
		SlcExecutionTestUtils.assertSlcExecutionStep(slcExecUpdated.getSteps()
				.get(0), slcExecRetrieved.getSteps().get(0));
		SlcExecutionTestUtils.assertSlcExecutionStep(slcExecUpdated.getSteps()
				.get(1), slcExecRetrieved.getSteps().get(1));
		SlcExecutionTestUtils.assertSlcExecutionStep(slcExecUpdated.getSteps()
				.get(2), slcExecRetrieved.getSteps().get(2));
	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

}
