package org.argeo.slc.server.client;

import java.util.Map;

import org.argeo.slc.Condition;

/** Abstraction of the access to HTTP services . */
public interface HttpServicesClient {
	/** Call service, failing if it is not available. */
	public <T> T callService(String path, Map<String, String> parameters);

	/**
	 * Call service, waiting and retrying until the timeout is reached if it is
	 * not immediately available.
	 * 
	 * @param path
	 *            service path
	 * @param condition
	 *            if not null, a condition to be applied on received object,
	 *            keep trying if it returns false.
	 * @param timeout
	 *            timeout after which an exception is thrown
	 */
	public <T> T callServiceSafe(String path, Map<String, String> parameters,
			Condition<T> condition, Long timeout);

}
