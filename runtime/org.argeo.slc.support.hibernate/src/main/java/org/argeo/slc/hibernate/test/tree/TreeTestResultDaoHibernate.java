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

package org.argeo.slc.hibernate.test.tree;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.structure.StructureElement;
import org.argeo.slc.test.TestResult;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

/**
 * The Hibernate implementation for tree-based result of the test result dao.
 * 
 * @see TreeTestResult
 */
public class TreeTestResultDaoHibernate extends HibernateDaoSupport implements
		TreeTestResultDao {

	public void create(TestResult testResult) {
		getHibernateTemplate().save(testResult);
	}

	public void update(TestResult testResult) {
		getHibernateTemplate().saveOrUpdate(testResult);
	}

	public TreeTestResult getTestResult(String uuid) {
		List<?> list = getHibernateTemplate().find(
				"from TreeTestResult where uuid=?", uuid);
		if (list.size() == 0) {
			return null;
		} else {
			return (TreeTestResult) list.get(0);
		}

	}

	@SuppressWarnings("unchecked")
	public List<TreeTestResult> listTestResults() {
		return (List<TreeTestResult>) getHibernateTemplate().find(
				"from TreeTestResult");
	}

	@SuppressWarnings("unchecked")
	public List<TreeTestResult> listResults(TreeSPath path) {
		List<TreeTestResult> list = getHibernateTemplate().find(
				"from TreeTestResult res where ? in indices(res.resultParts)",
				path.getAsUniqueString());
		return list;
	}

	public void close(final String testResultId, final Date closeDate) {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				TreeTestResult treeTestResult = getTreeTestResult(session,
						testResultId);
				treeTestResult.setCloseDate(closeDate);
				session.update(treeTestResult);
				return treeTestResult;
			}
		});
	}

	public void addResultPart(final String testResultId, final TreeSPath path,
			final SimpleResultPart resultPart,
			final Map<TreeSPath, StructureElement> relatedElements) {

		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				TreeTestResult treeTestResult = getTreeTestResult(session,
						testResultId);
				PartSubList lst = treeTestResult.getResultParts().get(path);
				if (lst == null) {
					lst = new PartSubList();
					treeTestResult.getResultParts().put(path, lst);
				}
				lst.getParts().add(resultPart);
				if (relatedElements != null)
					treeTestResult.getElements().putAll(relatedElements);

				session.update(treeTestResult);
				return treeTestResult;
			}
		});

	}

	public void addAttachment(final String testResultId,
			final SimpleAttachment attachment) {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				TreeTestResult treeTestResult = getTreeTestResult(session,
						testResultId);

				// Check is there is already such an attachment
				Assert.notNull(attachment, "attachment is null");
				Assert.notNull(session, "session is null");
				SimpleAttachment att = (SimpleAttachment) session.get(
						attachment.getClass(), attachment.getUuid());
				if (att != null)
					throw new SlcException(
							"There is already an attachement with id "
									+ attachment.getUuid());

				treeTestResult.getAttachments().add(attachment);
				session.update(treeTestResult);
				return treeTestResult;
			}
		});

	}

	protected TreeTestResult getTreeTestResult(Session session,
			String testResultId) {
		TreeTestResult treeTestResult = (TreeTestResult) session.get(
				TreeTestResult.class, testResultId);
		if (treeTestResult == null)
			throw new SlcException("No result with id " + testResultId);
		return treeTestResult;
	}

	public void updateAttributes(final String testResultId,
			final SortedMap<String, String> attributes) {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				TreeTestResult treeTestResult = getTreeTestResult(session,
						testResultId);
				treeTestResult.setAttributes(attributes);

				session.update(treeTestResult);
				return treeTestResult;
			}
		});
	}

}
