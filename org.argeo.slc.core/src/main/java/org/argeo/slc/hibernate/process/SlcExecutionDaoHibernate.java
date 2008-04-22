package org.argeo.slc.hibernate.process;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.dao.process.SlcExecutionDao;

public class SlcExecutionDaoHibernate extends HibernateDaoSupport implements
		SlcExecutionDao {

	public void create(SlcExecution slcExecution) {
		getHibernateTemplate().save(slcExecution);
	}

	public SlcExecution getSlcExecution(String uuid) {
		return (SlcExecution) getHibernateTemplate().get(SlcExecution.class,
				uuid);
	}

	public List<SlcExecution> listSlcExecutions() {
		return (List<SlcExecution>) getHibernateTemplate().loadAll(
				SlcExecution.class);
	}

}
