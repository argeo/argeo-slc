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

	public void delete(String agentId) {
		Object obj = getHibernateTemplate().get(SlcAgentDescriptor.class,
				agentId);
		if (obj != null)
			getHibernateTemplate().delete(obj);
	}

	public SlcAgentDescriptor getAgentDescriptor(String agentId) {

		return (SlcAgentDescriptor) getHibernateTemplate().get(
				SlcAgentDescriptor.class, agentId);
	}

	@SuppressWarnings("unchecked")
	public List<SlcAgentDescriptor> listSlcAgentDescriptors() {
		return (List<SlcAgentDescriptor>) getHibernateTemplate().loadAll(
				SlcAgentDescriptor.class);
	}

}
