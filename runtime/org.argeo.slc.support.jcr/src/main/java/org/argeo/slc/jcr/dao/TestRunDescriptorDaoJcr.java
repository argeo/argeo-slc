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

package org.argeo.slc.jcr.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.dao.test.TestRunDescriptorDao;
import org.argeo.slc.test.TestRunDescriptor;

public class TestRunDescriptorDaoJcr extends AbstractSlcJcrDao implements
		TestRunDescriptorDao {

	public TestRunDescriptor getTestRunDescriptor(String id) {
		// TODO: optimize query
		String queryString = "//testrun[@testRunUuid='" + id + "']";
		Query query = createQuery(queryString, Query.XPATH);
		Node node = JcrUtils.querySingleNode(query);
		if (node == null)
			return null;
		return (TestRunDescriptor) nodeMapper.load(node);
	}

	public void saveOrUpdate(TestRunDescriptor testRunDescriptor) {
		try {
			nodeMapper.save(getSession(), basePath(testRunDescriptor),
					testRunDescriptor);
			getSession().save();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot import " + testRunDescriptor, e);
		}
	}

}
