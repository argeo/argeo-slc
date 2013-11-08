package org.argeo.slc.akb;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/** Provides method interfaces to manage an AKB repository */
public interface AkbService {

	/** Creates a preconfigured AKB Template */
	public Node createAkbTemplate(Node parent, String name) throws RepositoryException;
}
