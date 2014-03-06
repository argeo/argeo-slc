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
 * Abstract the base a given modular distribution set that is the parent all
 * versions of the same distribution
 */
public class ModularDistBaseElem extends DistParentElem {

	final static public String AETHER_BINARIES_TYPE = "binaries";
	final static public String AETHER_DEP_TYPE = "dep";
	private String type;
	private Node artifactBase;

	public ModularDistBaseElem(WorkspaceElem wkspElem, String name,
			Node artifactBase, String type) {
		super(name, wkspElem.inHome(), wkspElem.isReadOnly());
		setParent(wkspElem);
		this.artifactBase = artifactBase;
		this.type = type;
	}

	/**
	 * Override normal behavior to initialize children only when first requested
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
	 * Override normal behavior to initialize children only when first requested
	 */
	@Override
	public synchronized Object[] getChildren() {
		if (isLoaded()) {
			return super.getChildren();
		} else {
			// initialize current object
			try {
				NodeIterator ni = getDistVersions();
				while (ni.hasNext()) {
					Node curNode = ni.nextNode();
					addChild(new ModularDistVersionElem(this, curNode
							.getProperty(SlcNames.SLC_ARTIFACT_VERSION)
							.getString(), curNode));
				}
				return super.getChildren();
			} catch (RepositoryException re) {
				throw new ArgeoException(
						"Unexcpected error while initializing children SingleJcrNode",
						re);
			}
		}
	}

	public NodeIterator getDistVersions() {
		try {
			QueryManager queryManager = artifactBase.getSession()
					.getWorkspace().getQueryManager();
			QueryObjectModelFactory factory = queryManager.getQOMFactory();
			Selector source = factory.selector(
					SlcTypes.SLC_MODULAR_DISTRIBUTION,
					SlcTypes.SLC_MODULAR_DISTRIBUTION);
			Constraint constraint = factory.descendantNode(
					source.getSelectorName(), artifactBase.getPath());
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