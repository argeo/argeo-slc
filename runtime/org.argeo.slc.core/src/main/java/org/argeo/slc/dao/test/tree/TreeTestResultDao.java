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

package org.argeo.slc.dao.test.tree;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.TestResultDao;
import org.argeo.slc.structure.StructureElement;

/**
 * Adds DAO features specific to tree test result.
 * 
 * @see TreeTestResult
 */
public interface TreeTestResultDao extends TestResultDao<TreeTestResult> {
	/** Lists results containing this path */
	public List<TreeTestResult> listResults(TreeSPath path);

	/** Adds a result part related to this path */
	public void addResultPart(String testResultId, TreeSPath path,
			SimpleResultPart resultPart,
			Map<TreeSPath, StructureElement> relatedElements);

	/** Update attributes */
	public void updateAttributes(String testResultId,
			SortedMap<String, String> attributes);

	public void addAttachment(String testResultId, SimpleAttachment attachment);
}
