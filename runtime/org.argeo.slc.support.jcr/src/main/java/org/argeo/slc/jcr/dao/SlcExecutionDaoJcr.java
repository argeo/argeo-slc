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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;

public class SlcExecutionDaoJcr extends AbstractSlcJcrDao implements
		SlcExecutionDao {
	private final static Log log = LogFactory.getLog(SlcExecutionDaoJcr.class);

	public void addSteps(String slcExecutionId,
			List<SlcExecutionStep> additionalSteps) {
		// TODO: optimize, do one single query
		SlcExecution slcExecution = getSlcExecution(slcExecutionId);
		slcExecution.getSteps().addAll(additionalSteps);
		update(slcExecution);

	}

	public void create(SlcExecution slcExecution) {
		try {
			nodeMapper.save(getSession(), basePath(slcExecution), slcExecution);
			getSession().save();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot create slcExecution" + slcExecution,
					e);
		}
	}

	protected String basePath(SlcExecution slcExecution) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		// cal.setTime(slcExecution.getStartDate());
		String host = slcExecution.getHost();
		if (host == null)
			host = "UNKOWNHOST";
		return "/slc/processes/" + JcrUtils.hostAsPath(host) + '/'
				+ JcrUtils.dateAsPath(cal) + "process";
	}

	public SlcExecution getSlcExecution(String uuid) {
		// TODO: optimize query
		String queryString = "//process[@uuid='" + uuid + "']";
		Query query = createQuery(queryString, Query.XPATH);
		Node node = JcrUtils.querySingleNode(query);
		if (node == null)
			return null;
		return (SlcExecution) nodeMapper.load(node);
	}

	public List<SlcExecution> listSlcExecutions() {
		List<SlcExecution> res = new ArrayList<SlcExecution>();
		// TODO: optimize query
		String queryString = "//process";
		try {
			Query query = createQuery(queryString, Query.XPATH);
			QueryResult qr = query.execute();
			NodeIterator iterator = qr.getNodes();
			while (iterator.hasNext()) {
				Node node = iterator.nextNode();
				SlcExecution slcExecution = (SlcExecution) nodeMapper
						.load(node);
				res.add(slcExecution);
			}
			return res;
		} catch (RepositoryException e) {
			throw new SlcException("Cannot list SLC executions", e);
		}
	}

	public void merge(SlcExecution slcExecution) {
		throw new UnsupportedOperationException();
	}

	public void update(SlcExecution slcExecution) {
		// TODO: optimize query
		String queryString = "//process[@uuid='" + slcExecution.getUuid()
				+ "']";
		try {
			Query query = createQuery(queryString, Query.XPATH);
			Node node = JcrUtils.querySingleNode(query);
			nodeMapper.update(node, slcExecution);
			getSession().save();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot update " + slcExecution, e);
		}
	}

	public List<SlcExecutionStep> tailSteps(String slcExecutionId,
			Integer nbrOfSteps) {
		log.error("Method not implemented, returning an empty list.");
		return new ArrayList<SlcExecutionStep>();
	}

	public List<SlcExecutionStep> tailSteps(String slcExecutionId,
			String slcExecutionStepId) {
		log.error("Method not implemented, returning an empty list.");
		return new ArrayList<SlcExecutionStep>();
	}

}
