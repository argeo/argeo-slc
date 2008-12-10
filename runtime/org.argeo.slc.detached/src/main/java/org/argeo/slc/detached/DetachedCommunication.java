package org.argeo.slc.detached;

import java.io.Serializable;

/**
 * Common interface for all communications between the source context and the
 * detached server.
 */
public interface DetachedCommunication extends Serializable {
	/** The unique identifier of this answer. */
	public String getUuid();
}
