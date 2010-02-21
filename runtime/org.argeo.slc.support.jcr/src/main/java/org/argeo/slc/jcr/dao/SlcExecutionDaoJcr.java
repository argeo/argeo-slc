package org.argeo.slc.jcr.dao;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.io.IOUtils;
import org.argeo.jcr.BeanNodeMapper;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;
import org.springframework.oxm.Marshaller;
import org.springframework.xml.transform.StringResult;

public class SlcExecutionDaoJcr implements SlcExecutionDao {
	private Session session;

	private Marshaller marshaller;

	private BeanNodeMapper beanNodeMapper = new BeanNodeMapper();

	public void addSteps(String slcExecutionId,
			List<SlcExecutionStep> additionalSteps) {
		// TODO Auto-generated method stub

	}

	public void create(SlcExecution slcExecution) {
		StringResult result = new StringResult();
		InputStream in = null;
		try {
			beanNodeMapper.saveOrUpdate(getSession(), basePath(slcExecution),
					slcExecution);

			// TODO: optimize with piped streams
			// marshaller.marshal(slcExecution, result);
			// in = new ByteArrayInputStream(result.toString().getBytes());
			//
			// String basePath = basePath(slcExecution);
			// JcrUtils.mkdirs(getSession(), basePath);
			//
			// session.importXML(basePath(slcExecution), in,
			// ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);

			JcrUtils.debug(session.getRootNode());
			session.save();
		} catch (Exception e) {
			throw new SlcException("Cannot import " + slcExecution, e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	protected String basePath(SlcExecution slcExecution) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		// cal.setTime(slcExecution.getStartDate());
		return "/slc/processes/" + slcExecution.getHost().replace('.', '/')
				+ '/' + JcrUtils.dateAsPath(cal) + slcExecution.getUuid();
	}

	public SlcExecution getSlcExecution(String uuid) {
		try {
			Workspace workspace = session.getWorkspace();
			QueryManager qm = workspace.getQueryManager();

			String queryString = "//*[@uuid='" + uuid + "']";
			Query query = qm.createQuery(queryString, Query.XPATH);
			QueryResult queryResult = query.execute();
			NodeIterator nodeIterator = queryResult.getNodes();
			Node node;
			if (nodeIterator.hasNext())
				node = nodeIterator.nextNode();
			else
				throw new SlcException("Query returned no node.");

			if (nodeIterator.hasNext())
				throw new SlcException("Query returned more than one node.");

			return (SlcExecution) beanNodeMapper.nodeToBean(node);
		} catch (Exception e) {
			throw new SlcException("Cannot load SLC execution " + uuid, e);
		}
	}

	public List<SlcExecution> listSlcExecutions() {
		// TODO Auto-generated method stub
		return null;
	}

	public void merge(SlcExecution slcExecution) {
		// TODO Auto-generated method stub

	}

	public void update(SlcExecution slcExecution) {
		// TODO Auto-generated method stub

	}

	public void setSession(Session session) {
		this.session = session;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	protected Session getSession() {
		return session;
	}

}
