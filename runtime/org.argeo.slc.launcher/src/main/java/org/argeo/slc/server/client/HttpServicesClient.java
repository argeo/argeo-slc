/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
