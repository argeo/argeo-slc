package org.argeo.slc.dao.test.tree;

import java.util.SortedSet;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;

public interface TreeTestResultCollectionDao {
	public void create(TreeTestResultCollection ttrCollection);

	public void update(TreeTestResultCollection ttrCollection);

	public TreeTestResultCollection getTestResultCollection(String id);

	public SortedSet<TreeTestResultCollection> listCollections();

	public void addResultToCollection(TreeTestResultCollection ttrc,
			String resultUuid);

	public void removeResultFromCollection(TreeTestResultCollection ttrc,
			String resultUuid);

}
