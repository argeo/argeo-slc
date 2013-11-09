package org.argeo.slc.akb;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/** Provides method interfaces to manage an AKB repository */
public interface AkbService {

	/** Creates a preconfigured AKB Template */
	public Node createAkbTemplate(Node parent, String name)
			throws RepositoryException;

	/**
	 * Shortcut to perform whatever test on a given connector only to check if
	 * URL is correctly defined, if the target system is there and if the
	 * current user has the sufficient credentials to connect
	 */
	public boolean testConnector(Node connector);
}
