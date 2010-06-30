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
import java.util.SortedSet;

import org.argeo.slc.core.test.tree.ResultAttributes;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;

/** Collections DAO */
public interface TreeTestResultCollectionDao {
	public void create(TreeTestResultCollection ttrCollection);

	public void update(TreeTestResultCollection ttrCollection);

	public void delete(TreeTestResultCollection ttrCollection);

	public TreeTestResultCollection getTestResultCollection(String id);

	/** Lists all collections */
	public SortedSet<TreeTestResultCollection> listCollections();

	/**
	 * Lists only result ids and attributes of the results belonging to these
	 * collection, or all results if id is null.
	 */
	public List<ResultAttributes> listResultAttributes(String collectionId);

	/** Lists results filtering based on the arguments. */
	public List<TreeTestResult> listResults(String collectionId,
			Map<String, String> attributes);

	/** Adds a result to a collection. */
	public void addResultToCollection(TreeTestResultCollection ttrc,
			String resultUuid);

	/** Removes a result from a collection. */
	public void removeResultFromCollection(TreeTestResultCollection ttrc,
			String resultUuid);

}
