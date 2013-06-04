package org.argeo.slc.repo;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

/**
 * Recursively visit a sub tree and apply the list of node indexer on supported
 * nodes.
 */
public class NodeIndexerVisitor implements ItemVisitor {
	/** order may be important */
	private List<NodeIndexer> nodeIndexers = new ArrayList<NodeIndexer>();

	public NodeIndexerVisitor() {
	}

	/** Convenience constructor */
	public NodeIndexerVisitor(NodeIndexer nodeIndexer) {
		nodeIndexers.add(nodeIndexer);
	}

	public NodeIndexerVisitor(List<NodeIndexer> nodeIndexers) {
		this.nodeIndexers = nodeIndexers;
	}

	public void visit(Node node) throws RepositoryException {
		for (NodeIndexer nodeIndexer : nodeIndexers)
			if (nodeIndexer.support(node.getPath()))
				nodeIndexer.index(node);

		for (NodeIterator it = node.getNodes(); it.hasNext();)
			visit(it.nextNode());
	}

	public void visit(Property property) throws RepositoryException {
	}

	public void setNodeIndexers(List<NodeIndexer> nodeIndexers) {
		this.nodeIndexers = nodeIndexers;
	}

}
