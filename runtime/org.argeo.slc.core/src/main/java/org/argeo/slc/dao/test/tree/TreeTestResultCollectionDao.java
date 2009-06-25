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
