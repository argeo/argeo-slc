package org.argeo.slc.repo;

import javax.jcr.Node;
import javax.jcr.observation.EventListener;

/**
 * Adds metadata to an existing node, ideally via observation after it has been
 * added. There is a similar concept in ModeShape with which this abstraction
 * may be merged in the future.
 */
public interface NodeIndexer {
	/**
	 * Whether the node at this path will be supported. This is typically use in
	 * an {@link EventListener} before the node is loaded, and would apply on
	 * information contained in the path / file name: file extension, base path,
	 * etc. If the node needs to be loaded, the recommended approach is to
	 * return <code>true</code> here and wait for index to be called, possibly
	 * returning without processing if the node should not be indexed. While
	 * not strictly a requirement, this avoids to open sessions in the indexer,
	 * centralizing such tasks in the caller.
	 */
	public Boolean support(String path);

	/**
	 * Adds the metadata. This is the responsibility of the caller to save the
	 * underlying session.
	 */
	public void index(Node node);
}
