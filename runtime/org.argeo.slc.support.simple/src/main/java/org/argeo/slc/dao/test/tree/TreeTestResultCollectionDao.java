package org.argeo.slc.dao.test.tree;

import java.util.List;
import java.util.SortedSet;

import org.argeo.slc.core.test.tree.ResultAttributes;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;

public interface TreeTestResultCollectionDao {
	public void create(TreeTestResultCollection ttrCollection);

	public void update(TreeTestResultCollection ttrCollection);

	public TreeTestResultCollection getTestResultCollection(String id);

	public SortedSet<TreeTestResultCollection> listCollections();

	public List<ResultAttributes> listResultAttributes(String collectionId);

	public void addResultToCollection(TreeTestResultCollection ttrc,
			String resultUuid);

	public void removeResultFromCollection(TreeTestResultCollection ttrc,
			String resultUuid);

}
