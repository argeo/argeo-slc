package org.argeo.slc.hibernate.process;

import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionStep;
import org.argeo.slc.core.process.SlcExecutionTestUtils;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class SlcExecutionHibernateTest extends AbstractSpringTestCase {

	public void testSave() {
		SlcExecutionDao dao = getBean("slcExecutionDao");

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

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

}
