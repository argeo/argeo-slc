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

package org.argeo.slc.hibernate.test;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.argeo.slc.dao.test.TestRunDescriptorDao;
import org.argeo.slc.test.TestRunDescriptor;

public class TestRunDescriptorDaoHibernate extends HibernateDaoSupport
		implements TestRunDescriptorDao {

	public TestRunDescriptor getTestRunDescriptor(String id) {
		return (TestRunDescriptor) getHibernateTemplate().get(
				TestRunDescriptor.class, id);
	}

	public void saveOrUpdate(TestRunDescriptor testRunDescriptor) {
		getHibernateTemplate().saveOrUpdate(testRunDescriptor);
	}

}
