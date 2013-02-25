/*
 * Copyright (C) 2007-2012 Argeo GmbH
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

/** JCR names used by SLC */
public interface SlcNames {
	public final static String SLC_ = "slc:";

	/*
	 * GENERAL
	 */
	public final static String SLC_UUID = "slc:uuid";
	public final static String SLC_STATUS = "slc:status";
	// generic name for result parts of a given test result (slc:testResult)
	// note that corresponding nodes can be retrieved using
	// myTestResultNode.getNodes(SLC_RESULT_PART+"*") method
	public final static String SLC_RESULT_PART = "slc:resultPart";
	// Fixed name of the child node of a slc:testResult of type
	// slc:check that aggregate status of all result parts of a given test
	// result
	public final static String SLC_AGGREGATED_STATUS = "slc:aggregatedStatus";

	public final static String SLC_TYPE = "slc:type";
	public final static String SLC_NAME = "slc:name";
	public final static String SLC_VERSION = "slc:version";
	public final static String SLC_VALUE = "slc:value";
	public final static String SLC_ADDRESS = "slc:address";
	public final static String SLC_METADATA = "slc:metadata";

	public final static String SLC_TIMESTAMP = "slc:timestamp";
	public final static String SLC_STARTED = "slc:started";
	public final static String SLC_COMPLETED = "slc:completed";

	public final static String SLC_VM = "slc:vm";
	/*
	 * SLC RUNTIME
	 */
	// execution
	public final static String SLC_SPEC = "slc:spec";
	public final static String SLC_EXECUTION_SPECS = "slc:executionSpecs";
	public final static String SLC_FLOW = "slc:flow";
	public final static String SLC_LOG = "slc:log";
	public final static String SLC_AGENTS = "slc:agents";

	// spec attribute
	public final static String SLC_IS_IMMUTABLE = "slc:isImmutable";
	public final static String SLC_IS_CONSTANT = "slc:isConstant";
	public final static String SLC_IS_HIDDEN = "slc:isHidden";

	// base directories
	public final static String SLC_SYSTEM = "slc:system";
	public final static String SLC_RESULTS = "slc:results";
	public final static String SLC_MY_RESULTS = "slc:myResults";
	public final static String SLC_PROCESSES = "slc:processes";

	// result
	public final static String SLC_SUCCESS = "slc:success";
	public final static String SLC_MESSAGE = "slc:message";
	public final static String SLC_TAG = "slc:tag";
	public final static String SLC_ERROR_MESSAGE = "slc:errorMessage";
	public final static String SLC_TEST_CASE = "slc:testCase";
	public final static String SLC_TEST_CASE_TYPE = "slc:testCaseType";

	// diff result
	public final static String SLC_SUMMARY = "slc:summary";
	public final static String SLC_ISSUES = "slc:issues";

	/*
	 * SLC REPO
	 */
	// shared
	public final static String SLC_URL = "slc:url";
	public final static String SLC_OPTIONAL = "slc:optional";
	public final static String SLC_AS_STRING = "slc:asString";

	// origin
	public final static String SLC_ORIGIN = "slc:origin";
	public final static String SLC_PROXY = "slc:proxy";

	// slc:artifact
	public final static String SLC_ARTIFACT_ID = "slc:artifactId";
	public final static String SLC_GROUP_ID = "slc:groupId";
	public final static String SLC_GROUP_BASE_ID = "slc:groupBaseId";
	public final static String SLC_ARTIFACT_VERSION = "slc:artifactVersion";
	public final static String SLC_ARTIFACT_EXTENSION = "slc:artifactExtension";
	public final static String SLC_ARTIFACT_CLASSIFIER = "slc:artifactClassifier";

	// slc:jarArtifact
	public final static String SLC_MANIFEST = "slc:manifest";

	// shared OSGi
	public final static String SLC_SYMBOLIC_NAME = "slc:symbolic-name";
	public final static String SLC_BUNDLE_VERSION = "slc:bundle-version";

	// slc:osgiBaseVersion
	public final static String SLC_MAJOR = "slc:major";
	public final static String SLC_MINOR = "slc:minor";
	public final static String SLC_MICRO = "slc:micro";
	// slc:osgiVersion
	public final static String SLC_QUALIFIER = "slc:qualifier";

	// slc:exportedPackage
	public final static String SLC_USES = "slc:uses";

}
