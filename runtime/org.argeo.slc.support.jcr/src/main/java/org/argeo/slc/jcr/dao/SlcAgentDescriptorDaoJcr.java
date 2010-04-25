package org.argeo.slc.jcr.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.dao.runtime.SlcAgentDescriptorDao;
import org.argeo.slc.runtime.SlcAgentDescriptor;

public class SlcAgentDescriptorDaoJcr extends AbstractSlcJcrDao implements
		SlcAgentDescriptorDao {
	private final static Log log = LogFactory
			.getLog(SlcAgentDescriptorDaoJcr.class);

	public void create(SlcAgentDescriptor slcAgentDescriptor) {
		try {
			nodeMapper.save(getSession(), basePath(slcAgentDescriptor),
					slcAgentDescriptor);
			getSession().save();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot import " + slcAgentDescriptor, e);
		}
	}

	public SlcAgentDescriptor getAgentDescriptor(String agentId) {
		// TODO: optimize query
		String queryString = "//agent[@uuid='" + agentId + "']";
		Query query = createQuery(queryString, Query.XPATH);
		Node node = JcrUtils.querySingleNode(query);
		if (node == null)
			return null;
		return (SlcAgentDescriptor) nodeMapper.load(node);
	}

	public void delete(SlcAgentDescriptor slcAgentDescriptor) {
		try {
			String queryString = "//agent[@uuid='"
					+ slcAgentDescriptor.getUuid() + "']";
			Query query = createQuery(queryString, Query.XPATH);
			Node node = JcrUtils.querySingleNode(query);
			if (node != null) {
				node.remove();
				getSession().save();
			} else
				log.warn("No node found for agent descriptor: "
						+ slcAgentDescriptor);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot delete " + slcAgentDescriptor, e);
		}

	}

	public void delete(String agentId) {
		try {
			// TODO: optimize query
			String queryString = "//agent[@uuid='" + agentId + "']";
			Query query = createQuery(queryString, Query.XPATH);
			Node node = JcrUtils.querySingleNode(query);
			if (node != null)
				node.remove();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot find AgentDescriptor" + agentId, e);
		}

	}

	public List<SlcAgentDescriptor> listSlcAgentDescriptors() {
		try {
			String queryString = "//agent";
			Query query = createQuery(queryString, Query.XPATH);

			List<SlcAgentDescriptor> listSad = new ArrayList<SlcAgentDescriptor>();

			NodeIterator ni = query.execute().getNodes();
			while (ni.hasNext()) {
				Node curNode = (Node) ni.next();
				// JcrUtils.debug(curNode);
				listSad.add((SlcAgentDescriptor) nodeMapper.load(curNode));
			}

			return listSad;
		} catch (Exception e) {
			throw new SlcException("Cannot load AgentDescriptorList", e);
		}
	}

}
