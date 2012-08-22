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
package org.argeo.slc.jcr;

/** JCR related constants used across SLC */
public interface SlcJcrConstants {
	public final static String PROPERTY_PATH = "argeo.slc.jcr.path";
	public final static String SLC_BASE_PATH = "/slc:system";
	public final static String PROCESSES_BASE_PATH = SLC_BASE_PATH
			+ "/slc:processes";
	public final static String AGENTS_BASE_PATH = SLC_BASE_PATH + "/slc:agents";
	// public final static String RESULTS_BASE_PATH = SLC_BASE_PATH
	// + "/slc:results";
	public final static String VM_AGENT_FACTORY_PATH = AGENTS_BASE_PATH
			+ "/slc:vm";
	
	/*
	 * SLC SPECIFIC JCR PATHS
	 */
	public final static String SLC_MYRESULT_BASEPATH = "slc:myResults/";
}
