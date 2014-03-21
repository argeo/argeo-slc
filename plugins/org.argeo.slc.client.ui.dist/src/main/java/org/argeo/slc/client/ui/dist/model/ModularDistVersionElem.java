package org.argeo.slc.client.ui.dist.model;

import javax.jcr.Node;

/**
 * Abstract a node of type slc:modularDistribution that has a child node that
 * lists the modules contained in the current distribution
 */
public class ModularDistVersionElem extends DistParentElem {
	private final Node modularDistVersionNode;

	public ModularDistVersionElem(ModularDistVersionBaseElem modularDistGroupElem,
			String version, Node modularDistVersionNode) {
		super(version, modularDistGroupElem.inHome(), modularDistGroupElem
				.isReadOnly());
		setParent(modularDistGroupElem);
		this.modularDistVersionNode = modularDistVersionNode;
	}

	public Object[] getChildren() {
		return null;
	}

	public String getLabel() {
		return getName();
	}

	public WorkspaceElem getWorkspaceElem() {
		return (WorkspaceElem) getParent().getParent();
	}

	public Node getModularDistVersionNode() {
		return modularDistVersionNode;
	}
}