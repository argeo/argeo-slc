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

import javax.jcr.Node;
import javax.jcr.observation.EventListener;

/**
 * Adds metadata to an existing node, ideally via observation after it has been
 * added. THere is a similar concept in ModeShape with which this abstraction
 * may be merged in the future.
 */
public interface NodeIndexer {
	/**
	 * Whether the node at this path will be supported. This is typically use in
	 * an {@link EventListener} before the node is loaded, and would apply on
	 * information contained in the path / file name: file extension, base path,
	 * etc. If the node needs to be loaded, the recommended approach is to
	 * return <code>true</code> here and wait for index to be called, possibly
	 * returning without processing if the node should node be indexed. While
	 * not stricly a requirements, this avoids to open sessions in the indexer,
	 * centrlaizing such tasks in the caller.
	 */
	public Boolean support(String path);

	/**
	 * Adds the metadata. This is the responsibility of the caller to save the
	 * udnerlying session.
	 */
	public void index(Node node);
}
