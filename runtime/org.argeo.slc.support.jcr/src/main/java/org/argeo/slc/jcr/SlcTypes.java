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

/** JCR node types used by SLC */
public interface SlcTypes {

	public final static String SLC_AGENT_FACTORY = "slc:agentFactory";
	public final static String SLC_AGENT = "slc:agent";
	public final static String SLC_MODULE = "slc:module";
	public final static String SLC_EXECUTION_MODULE = "slc:executionModule";
	public final static String SLC_EXECUTION_SPEC = "slc:executionSpec";
	public final static String SLC_EXECUTION_FLOW = "slc:executionFlow";
	public final static String SLC_PROCESS = "slc:process";
	public final static String SLC_REALIZED_FLOW = "slc:realizedFlow";

	public final static String SLC_EXECUTION_SPEC_ATTRIBUTE = "slc:executionSpecAttribute";
	public final static String SLC_PRIMITIVE_SPEC_ATTRIBUTE = "slc:primitiveSpecAttribute";
	public final static String SLC_REF_SPEC_ATTRIBUTE = "slc:refSpecAttribute";

	public final static String SLC_RESULT = "slc:result";
	public final static String SLC_CHECK = "slc:check";
	public final static String SLC_PROPERTY = "slc:property";
	public final static String SLC_DIFF_RESULT = "slc:diffResult";

	// Log levels
	public final static String SLC_LOG_ENTRY = "slc:logEntry";
	public final static String SLC_LOG_TRACE = "slc:logTrace";
	public final static String SLC_LOG_DEBUG = "slc:logDebug";
	public final static String SLC_LOG_INFO = "slc:logInfo";
	public final static String SLC_LOG_WARNING = "slc:logWarning";
	public final static String SLC_LOG_ERROR = "slc:logError";

	/*
	 * REPO
	 */
	public final static String SLC_ARTIFACT = "slc:artifact";
	public final static String SLC_ARTIFACT_VERSION_BASE = "slc:artifactVersion";
	public final static String SLC_ARTIFACT_BASE = "slc:artifactBase";
	public final static String SLC_GROUP_BASE = "slc:groupBase";
	public final static String SLC_JAR_FILE = "slc:jarFile";
	public final static String SLC_BUNDLE_ARTIFACT = "slc:bundleArtifact";
	public final static String SLC_OSGI_VERSION = "slc:osgiVersion";
	public final static String SLC_JAVA_PACKAGE = "slc:javaPackage";
	public final static String SLC_EXPORTED_PACKAGE = "slc:exportedPackage";
	public final static String SLC_IMPORTED_PACKAGE = "slc:importedPackage";
	public final static String SLC_DYNAMIC_IMPORTED_PACKAGE = "slc:dynamicImportedPackage";
	public final static String SLC_REQUIRED_BUNDLE = "slc:requiredBundle";
	public final static String SLC_FRAGMENT_HOST = "slc:fragmentHost";
	
	// origin
	public final static String SLC_KNOWN_ORIGIN = "slc:knownOrigin";
	public final static String SLC_PROXIED = "slc:proxied";

}
