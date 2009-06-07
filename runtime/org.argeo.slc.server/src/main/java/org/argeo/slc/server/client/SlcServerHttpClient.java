package org.argeo.slc.server.client;

import org.argeo.slc.runtime.SlcAgentDescriptor;

/** Abstraction of the access to HTTP services of an SLC Server. */
public interface SlcServerHttpClient extends HttpServicesClient {
	/** Wait for one agent to be available. */
	public SlcAgentDescriptor waitForOneAgent();
}
