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
package org.argeo.slc.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** A basic implementation of <code>DiffResult</code>. */
public class SimpleDiffResult implements DiffResult {
	private final List<DiffIssue> issues;
	private final Map<String, String> summary;

	/** Empty constructor */
	public SimpleDiffResult() {
		this(new TreeMap<String, String>(), new ArrayList<DiffIssue>());
	}

	/** Initialize from existing data */
	public SimpleDiffResult(Map<String, String> summary, List<DiffIssue> issues) {
		this.summary = summary;
		this.issues = issues;
	}

	/**
	 * Initialize from existing {@link DiffResult}, the collections are NOT
	 * cloned for performance purposes.
	 */
	public SimpleDiffResult(DiffResult diffResult) {
		this.summary = diffResult.getSummary();
		this.issues = diffResult.getIssues();
	}

	/** Summary information, alphabetically ordered key/value pairs */
	public Map<String, String> getSummary() {
		return summary;
	}

	/** The diff issues. */
	public List<DiffIssue> getIssues() {
		return issues;
	}

}
