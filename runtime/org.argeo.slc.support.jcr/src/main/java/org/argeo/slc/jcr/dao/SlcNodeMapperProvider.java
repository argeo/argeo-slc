package org.argeo.slc.jcr.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.ArgeoException;
import org.argeo.jcr.NodeMapper;
import org.argeo.jcr.NodeMapperProvider;

public class SlcNodeMapperProvider implements NodeMapperProvider {
//	private final static Log log = LogFactory
//			.getLog(SlcNodeMapperProvider.class);

	private NodeMapper defaultNodeMapper;
	private NodeMapper treeTestResultNodeMapper;

	public void init() {
		defaultNodeMapper.setNodeMapperProvider(this);
		treeTestResultNodeMapper.setNodeMapperProvider(this);
	}

	public NodeMapper findNodeMapper(Node node) {
		try {
			// useful to initialize recursivity
			if (node == null)
				return defaultNodeMapper;

			if ("testresult".equals(node.getName()))
				return treeTestResultNodeMapper;
			else
				return defaultNodeMapper;

		} catch (RepositoryException re) {
			throw new ArgeoException("Cannot find NodeMapper for node " + node,
					re);
		}
	}

	public void setTreeTestResultNodeMapper(NodeMapper treeTestResultNodeMapper) {
		this.treeTestResultNodeMapper = treeTestResultNodeMapper;
	}

	public void setDefaultNodeMapper(NodeMapper defaultNodeMapper) {
		this.defaultNodeMapper = defaultNodeMapper;
	}

}
