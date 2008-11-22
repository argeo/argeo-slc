package org.argeo.slc.hibernate.process;

import java.sql.SQLException;
import java.util.List;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.argeo.slc.SlcException;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class SlcExecutionDaoHibernate extends HibernateDaoSupport implements
		SlcExecutionDao {

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

	public List<SlcExecution> listSlcExecutions() {
		return (List<SlcExecution>) getHibernateTemplate().loadAll(
				SlcExecution.class);
	}

	public void addSteps(final String slcExecutionId,
			final List<SlcExecutionStep> additionalSteps) {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				SlcExecution slcExecution = (SlcExecution) session.get(
						SlcExecution.class, slcExecutionId);

				if (slcExecution == null)
					throw new SlcException("Could not find SLC execution "
							+ slcExecutionId);

				slcExecution.getSteps().addAll(additionalSteps);
				session.update(slcExecution);
				return slcExecution;
			}
		});

	}

}
