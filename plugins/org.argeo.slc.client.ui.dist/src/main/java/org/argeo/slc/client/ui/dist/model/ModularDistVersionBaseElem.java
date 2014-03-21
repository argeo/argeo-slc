package org.argeo.slc.client.ui.dist.model;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;

import org.argeo.ArgeoException;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;

/**
 * Abstract the base of a given modular distribution set i.e. the parent of all
 * versions of a given modular distribution
 */
public class ModularDistVersionBaseElem extends DistParentElem {

	// final static public String AETHER_CATEGORY_BASE = "categoryBase";
	final static public String AETHER_BINARIES_TYPE = "binaries";
	final static public String AETHER_DEP_TYPE = "dep";
	private String type;
	private Node modularDistVersionBase;

	public ModularDistVersionBaseElem(WorkspaceElem wkspElem, String name,
			Node modularDistVersionBase, String type) {
		super(name, wkspElem.inHome(), wkspElem.isReadOnly());
		setParent(wkspElem);
		this.modularDistVersionBase = modularDistVersionBase;
		this.type = type;
	}

	public Node getModularDistBase() {
		// // TODO clean this
		// if (type.equals(AETHER_CATEGORY_BASE))
		// return modularDistVersionBase;
		// else
		try {
			return modularDistVersionBase.getParent();
		} catch (RepositoryException e) {
			throw new SlcException("unable to get parent node for "
					+ modularDistVersionBase, e);
		}
	}

	public WorkspaceElem getWkspElem() {
		return (WorkspaceElem) getParent();
	}

	/**
	 * Override normal behaviour to initialise children only when first
	 * requested
	 */
	@Override
	public synchronized boolean hasChildren() {
		if (isLoaded()) {
			return super.hasChildren();
		} else {
			return true;
		}
	};

	/**
	 * Override normal behaviour to initialise children only when first
	 * requested
	 */
	@Override
	public synchronized Object[] getChildren() {
		if (isLoaded()) {
			return super.getChildren();
		} else {
			try {
				NodeIterator ni = getDistVersions();
				while (ni != null && ni.hasNext()) {
					Node curNode = ni.nextNode();
					if (curNode.hasProperty(SlcNames.SLC_ARTIFACT_VERSION))
						addChild(new ModularDistVersionElem(this, curNode
								.getProperty(SlcNames.SLC_ARTIFACT_VERSION)
								.getString(), curNode));
				}
				return super.getChildren();
			} catch (RepositoryException re) {
				throw new ArgeoException("Unable to retrieve children for "
						+ modularDistVersionBase, re);
			}
		}
	}

	private NodeIterator getDistVersions() {
		try {
			// if (AETHER_CATEGORY_BASE.equals(type))
			// return null;

			QueryManager queryManager = modularDistVersionBase.getSession()
					.getWorkspace().getQueryManager();
			QueryObjectModelFactory factory = queryManager.getQOMFactory();
			Selector source = factory.selector(
					SlcTypes.SLC_MODULAR_DISTRIBUTION,
					SlcTypes.SLC_MODULAR_DISTRIBUTION);
			Constraint constraint = factory.descendantNode(
					source.getSelectorName(), modularDistVersionBase.getPath());
			// Ordering order = factory.descending(factory.propertyValue(
			// source.getSelectorName(), SlcNames.SLC_ARTIFACT_VERSION));
			// Ordering[] orderings = { order };
			QueryObjectModel query = factory.createQuery(source, constraint,
					null, null);
			QueryResult queryResult = query.execute();
			return queryResult.getNodes();
		} catch (RepositoryException e) {
			throw new SlcException(
					"Unable to version for modular distribution: " + getName(),
					e);
		}
	}

	public String getType() {
		return type;
	}
}