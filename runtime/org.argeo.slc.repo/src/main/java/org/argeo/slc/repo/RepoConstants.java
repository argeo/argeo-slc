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
package org.argeo.slc.repo;

/** SLC repository constants */
public interface RepoConstants {
	public final static String DEFAULT_JAVA_REPOSITORY_ALIAS = "java";
	public final static String DEFAULT_JAVA_REPOSITORY_LABEL = "Internal Java Repository";

	public final static String DEFAULT_ARTIFACTS_BASE_PATH = "/";
	public final static String REPO_BASEPATH = "/slc:repo";
	public final static String PROXIED_REPOSITORIES = REPO_BASEPATH
			+ "/slc:sources";
	public final static String DISTRIBUTIONS_BASE_PATH = REPO_BASEPATH
			+ "/slc:distributions";
	public final static String REPOSITORIES_BASE_PATH = REPO_BASEPATH
			+ "/slc:repositories";
}
