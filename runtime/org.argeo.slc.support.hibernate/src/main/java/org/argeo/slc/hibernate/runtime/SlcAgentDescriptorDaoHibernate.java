package org.argeo.slc.hibernate.runtime;

import java.util.List;

import org.argeo.slc.dao.runtime.SlcAgentDescriptorDao;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SlcAgentDescriptorDaoHibernate extends HibernateDaoSupport
		implements SlcAgentDescriptorDao {

	public void create(SlcAgentDescriptor slcAgentDescriptor) {
		getHibernateTemplate().save(slcAgentDescriptor);
	}

	public void delete(SlcAgentDescriptor slcAgentDescriptor) {
		getHibernateTemplate().delete(slcAgentDescriptor);
	}

	public List<SlcAgentDescriptor> listSlcAgentDescriptors() {
		return (List<SlcAgentDescriptor>) getHibernateTemplate().loadAll(
				SlcAgentDescriptor.class);
	}

}
