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
package org.argeo.slc.repo.core;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.NodeIndexer;

/** Maintains the metadata of a workspace, using listeners */
public class WorkspaceIndexer {
	private final static Log log = LogFactory.getLog(WorkspaceIndexer.class);

	private final Session adminSession;
	private IndexingListener artifactListener;
	/** order may be important */
	private final List<NodeIndexer> nodeIndexers;

	public WorkspaceIndexer(Session adminSession, List<NodeIndexer> nodeIndexers) {
		this.adminSession = adminSession;
		this.nodeIndexers = nodeIndexers;
		try {
			artifactListener = new IndexingListener();
			adminSession
					.getWorkspace()
					.getObservationManager()
					.addEventListener(artifactListener, Event.NODE_ADDED, "/",
							true, null, null, true);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot initialize repository backend", e);
		}
	}

	public void close() {
		try {
			adminSession.getWorkspace().getObservationManager()
					.removeEventListener(artifactListener);
		} catch (RepositoryException e) {
			log.error("Cannot close workspace indexer "
					+ adminSession.getWorkspace().getName(), e);
		}
	}

	class IndexingListener implements EventListener {

		public void onEvent(EventIterator events) {
			while (events.hasNext()) {
				Event event = events.nextEvent();
				try {
					String newNodePath = event.getPath();
					Node newNode = null;
					for (NodeIndexer nodeIndexer : nodeIndexers) {
						try {
							if (nodeIndexer.support(newNodePath)) {
								if (newNode == null)
									newNode = adminSession.getNode(newNodePath);
								nodeIndexer.index(newNode);
							}
						} catch (RuntimeException e) {
							e.printStackTrace();
							throw e;
						}
					}
					if (newNode != null)
						adminSession.save();
				} catch (RepositoryException e) {
					throw new SlcException("Cannot process event " + event, e);
				} finally {
					JcrUtils.discardQuietly(adminSession);
				}
			}
		}
	}
}
