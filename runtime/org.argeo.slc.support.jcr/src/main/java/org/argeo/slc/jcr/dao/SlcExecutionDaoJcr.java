package org.argeo.slc.jcr.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;

public class SlcExecutionDaoJcr extends AbstractSlcJcrDao implements
		SlcExecutionDao {
	// private final static Log log =
	// LogFactory.getLog(SlcExecutionDaoJcr.class);

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
		} catch (Exception e) {
			throw new SlcException("Cannot create slcExecution" + slcExecution,
					e);
		}
	}

	protected String basePath(SlcExecution slcExecution) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		// cal.setTime(slcExecution.getStartDate());
		return "/slc/processes/" + JcrUtils.hostAsPath(slcExecution.getHost())
				+ '/' + JcrUtils.dateAsPath(cal) + "process";
	}

	public SlcExecution getSlcExecution(String uuid) {
		try {
			// TODO: optimize query
			String queryString = "//process[@uuid='" + uuid + "']";
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			Node node = JcrUtils.querySingleNode(query);
			if (node == null)
				return null;
			return (SlcExecution) nodeMapper.load(node);
		} catch (Exception e) {
			throw new SlcException("Cannot load SLC execution " + uuid, e);
		}
	}

	public List<SlcExecution> listSlcExecutions() {
		List<SlcExecution> res = new ArrayList<SlcExecution>();
		// TODO: optimize query
		String queryString = "//process";
		try {
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			QueryResult qr = query.execute();
			NodeIterator iterator = qr.getNodes();
			while (iterator.hasNext()) {
				Node node = iterator.nextNode();
				SlcExecution slcExecution = (SlcExecution) nodeMapper
						.load(node);
				res.add(slcExecution);
			}
			return res;
		} catch (Exception e) {
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
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			Node node = JcrUtils.querySingleNode(query);
			nodeMapper.update(node, slcExecution);
			getSession().save();
		} catch (Exception e) {
			throw new SlcException("Cannot update " + slcExecution, e);
		}
	}

}
