/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.server;

public interface HttpServices {
	public final static String LIST_AGENTS = "listAgents.service";
	public final static String IS_SERVER_READY = "isServerReady.service";
	public final static String NEW_SLC_EXECUTION = "newSlcExecution.service";
	public final static String LIST_SLC_EXECUTIONS = "listSlcExecutions.service";
	public final static String GET_MODULE_DESCRIPTOR = "getExecutionDescriptor.service";
	public final static String LIST_MODULE_DESCRIPTORS = "listModulesDescriptors.service";
	public final static String LIST_RESULTS = "listResults.service";
	public final static String ADD_EVENT_LISTENER = "addEventListener.service";
	public final static String REMOVE_EVENT_LISTENER = "removeEventListener.service";
	public final static String POLL_EVENT = "pollEvent.service";

}
