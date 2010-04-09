package org.argeo.slc.jcr.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.NodeMapper;
import org.argeo.slc.SlcException;
import org.argeo.slc.dao.runtime.SlcAgentDescriptorDao;
import org.argeo.slc.runtime.SlcAgentDescriptor;

public class SlcAgentDescriptorDaoJcr extends AbstractSlcJcrDao implements
		SlcAgentDescriptorDao {
	private final static Log log = LogFactory
			.getLog(SlcAgentDescriptorDaoJcr.class);

	private Workspace workspace;
	private QueryManager queryManager;
	private NodeMapper nodeMapper;
	
	public void init() {
		try {
			workspace = getSession().getWorkspace();
			queryManager = workspace.getQueryManager();
			nodeMapper = getNodeMapperProvider().findNodeMapper(null);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot initialize DAO", e);
		}
	}

	public void create(SlcAgentDescriptor slcAgentDescriptor) {
		if (log.isDebugEnabled())
			log.debug("in SlcAgentDescriptorDaoJcr.create");
		try {
			nodeMapper.save(getSession(), basePath(slcAgentDescriptor),
					slcAgentDescriptor);
			getSession().save();
		} catch (Exception e) {
			throw new SlcException("Cannot import " + slcAgentDescriptor, e);
		}
	}

	public SlcAgentDescriptor getAgentDescriptor(String agentId) {
		try {
			// TODO: optimize query
			String queryString = "//agent[@uuid='" + agentId + "']";
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			Node node = JcrUtils.querySingleNode(query);
			if (node == null)
				return null;
			return (SlcAgentDescriptor) nodeMapper.load(node);
		} catch (Exception e) {
			throw new SlcException("Cannot load Agent Descriptor" + agentId, e);
		}
	}

	public void delete(SlcAgentDescriptor slcAgentDescriptor) {
		if (log.isDebugEnabled())
			log.debug("slcAgentDescriptorDaoJcr.delete(slcAgentDescriptor)");

	}

	public void delete(String agentId) {
		if (log.isDebugEnabled())
			log.debug("slcAgentDescriptorDaoJcr.delete(agentID)");
		try {
			// TODO: optimize query
			String queryString = "//agent[@uuid='" + agentId + "']";
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			Node node = JcrUtils.querySingleNode(query);
			if (node != null)
				node.remove();
		} catch (Exception e) {
			throw new SlcException("Cannot find AgentDescriptor" + agentId, e);
		}

	}

	public List<SlcAgentDescriptor> listSlcAgentDescriptors() {
		if (log.isDebugEnabled())
			log.debug("slcAgentDescriptorDaoJcr.delete(agentID)");

		try {
			String queryString = "//agent";
			Query query = queryManager.createQuery(queryString, Query.XPATH);

			List<SlcAgentDescriptor> listSad = new ArrayList<SlcAgentDescriptor>();

			NodeIterator ni = query.execute().getNodes();
			while (ni.hasNext()) {
				Node curNode = (Node) ni.next();
				JcrUtils.debug(curNode);
				listSad.add((SlcAgentDescriptor) nodeMapper.load(curNode));
			}

			return listSad;
		} catch (Exception e) {
			throw new SlcException("Cannot load AgentDescriptorList", e);
		}
	}

}
