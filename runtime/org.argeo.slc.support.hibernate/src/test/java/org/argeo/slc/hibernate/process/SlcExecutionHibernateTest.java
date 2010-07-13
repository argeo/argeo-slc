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

package org.argeo.slc.hibernate.process;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.hibernate.unit.HibernateTestCase;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;
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

	public void testTailSteps() {
		SlcExecutionDao dao = getBean(SlcExecutionDao.class);

		SlcExecution slcExec = SlcExecutionTestUtils.createSimpleSlcExecution();
		int totalStepCount = 20;
		for (int i = 0; i < totalStepCount; i++) {
			slcExec.getSteps().add(new SlcExecutionStep("Log " + i));
		}
		dao.create(slcExec);

		int lastStepsCount = 7;
		List<SlcExecutionStep> firstSteps = dao.tailSteps(slcExec.getUuid(),
				lastStepsCount);
		assertEquals(lastStepsCount, firstSteps.size());

		SlcExecutionStep lastStep = firstSteps.get(lastStepsCount - 1);

		List<SlcExecutionStep> additionalSteps = new ArrayList<SlcExecutionStep>();
		int additionalStepsCount = 13;
		for (int i = 0; i < additionalStepsCount; i++) {
			additionalSteps.add(new SlcExecutionStep("Additonal log " + i));
		}
		dao.addSteps(slcExec.getUuid(), additionalSteps);

		List<SlcExecutionStep> lastSteps = dao.tailSteps(slcExec.getUuid(),
				lastStep.getUuid());
		assertEquals(additionalStepsCount, lastSteps.size());
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
