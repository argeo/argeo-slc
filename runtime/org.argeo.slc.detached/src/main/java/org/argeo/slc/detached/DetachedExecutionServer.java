package org.argeo.slc.detached;

/**
 * Interface between the detached and the source context via request and
 * answers.
 */
public interface DetachedExecutionServer {
	/** Actually executes the request. */
	public DetachedAnswer executeRequest(DetachedRequest request);
}
