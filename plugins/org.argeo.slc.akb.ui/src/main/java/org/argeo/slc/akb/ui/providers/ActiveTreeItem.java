package org.argeo.slc.akb.ui.providers;

import javax.jcr.Node;

/**
 * Simply wraps a JCR AKB Node to be able to also store corresponding current
 * environment (active or template)
 */
public class ActiveTreeItem {

	private final ActiveTreeItem parent;
	private final Node akbNode;
	private final Node akbEnvironment;

	public ActiveTreeItem(ActiveTreeItem parent, Node akbNode,
			Node akbEnvironment) {
		this.parent = parent;
		this.akbNode = akbNode;
		this.akbEnvironment = akbEnvironment;
	}

	public Node getNode() {
		return akbNode;
	}

	public Node getEnvironment() {
		return akbEnvironment;
	}

	public ActiveTreeItem getParent() {
		return parent;
	}
}
