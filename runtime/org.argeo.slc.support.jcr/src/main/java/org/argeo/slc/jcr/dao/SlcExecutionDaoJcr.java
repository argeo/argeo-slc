package org.argeo.slc.jcr.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.argeo.jcr.BeanNodeMapper;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;

public class SlcExecutionDaoJcr implements SlcExecutionDao {
	private Session session;
	private Workspace workspace;
	private QueryManager queryManager;

	private BeanNodeMapper beanNodeMapper = new BeanNodeMapper();

	public void init() {
		try {
			workspace = session.getWorkspace();
			queryManager = workspace.getQueryManager();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot initialize DAO", e);
		}
	}

	public void addSteps(String slcExecutionId,
			List<SlcExecutionStep> additionalSteps) {
		// TODO: optimize, do one single query
		SlcExecution slcExecution = getSlcExecution(slcExecutionId);
		slcExecution.getSteps().addAll(additionalSteps);
		update(slcExecution);

	}

	public void create(SlcExecution slcExecution) {
		try {
			beanNodeMapper.save(getSession(), basePath(slcExecution),
					slcExecution);
			session.save();
		} catch (Exception e) {
			throw new SlcException("Cannot import " + slcExecution, e);
		}
	}

	protected String basePath(SlcExecution slcExecution) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		// cal.setTime(slcExecution.getStartDate());
		return "/slc/processes/" + slcExecution.getHost().replace('.', '/')
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
			return (SlcExecution) beanNodeMapper.nodeToBean(node);
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
				SlcExecution slcExecution = (SlcExecution) beanNodeMapper
						.nodeToBean(node);
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
			beanNodeMapper.update(node, slcExecution);
		} catch (Exception e) {
			throw new SlcException("Cannot update " + slcExecution, e);
		}
	}

	public void setSession(Session session) {
		this.session = session;
	}

	protected Session getSession() {
		return session;
	}

	public void setBeanNodeMapper(BeanNodeMapper beanNodeMapper) {
		this.beanNodeMapper = beanNodeMapper;
	}

}
