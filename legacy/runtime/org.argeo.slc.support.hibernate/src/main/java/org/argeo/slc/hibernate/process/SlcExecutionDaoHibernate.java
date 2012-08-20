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
package org.argeo.slc.hibernate.process;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SlcExecutionDaoHibernate extends HibernateDaoSupport implements
		SlcExecutionDao {
	private final static Log log = LogFactory
			.getLog(SlcExecutionDaoHibernate.class);

	public void create(SlcExecution slcExecution) {
		getHibernateTemplate().save(slcExecution);
	}

	public void update(final SlcExecution slcExecution) {
		getHibernateTemplate().update(slcExecution);
	}

	public void merge(final SlcExecution slcExecution) {
		getHibernateTemplate().merge(slcExecution);
	}

	public SlcExecution getSlcExecution(String uuid) {
		return (SlcExecution) getHibernateTemplate().get(SlcExecution.class,
				uuid);
	}

	@SuppressWarnings("unchecked")
	public List<SlcExecution> listSlcExecutions() {
		return (List<SlcExecution>) getHibernateTemplate().loadAll(
				SlcExecution.class);
	}

	public void addSteps(final String slcExecutionId,
			final List<SlcExecutionStep> additionalSteps) {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				SlcExecution slcExecution = getSlcExecution(session,
						slcExecutionId);
				slcExecution.getSteps().addAll(additionalSteps);
				session.update(slcExecution);
				return slcExecution;
			}
		});

	}

	@SuppressWarnings("unchecked")
	public List<SlcExecutionStep> tailSteps(final String slcExecutionId,
			final Integer nbrOfSteps) {
		return (List<SlcExecutionStep>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						SlcExecution slcExecution = getSlcExecution(session,
								slcExecutionId);
						// TODO: do a query count() instead?
						int stepCount = slcExecution.getSteps().size();
						if (stepCount > nbrOfSteps) {
							return session.createFilter(
									slcExecution.getSteps(), "")
									.setFirstResult(stepCount - nbrOfSteps)
									.setMaxResults(nbrOfSteps).list();
						} else {
							return slcExecution.getSteps();
						}
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<SlcExecutionStep> tailSteps(final String slcExecutionId,
			final String slcExecutionStepId) {
		Object[] values = { slcExecutionStepId, slcExecutionId };
		List<Integer> indexes = getHibernateTemplate().findByNamedQuery(
				SlcExecutionStep.class.getName() + ".stepIndex", values);

		Integer index = indexes.get(0);
		if (log.isTraceEnabled()){
			log.trace(indexes.size());
			log.trace("Index " + index + " for step " + slcExecutionStepId
					+ " in process " + slcExecutionId);
		}
		Object[] values2 = { slcExecutionId, index };
		return getHibernateTemplate().findByNamedQuery(
				SlcExecutionStep.class.getName() + ".stepsAfter", values2);
	}

	protected SlcExecution getSlcExecution(Session session,
			String slcExecutionId) {
		SlcExecution slcExecution = (SlcExecution) session.get(
				SlcExecution.class, slcExecutionId);

		if (slcExecution == null)
			throw new SlcException("Could not find SLC execution "
					+ slcExecutionId);

		return slcExecution;
	}

}