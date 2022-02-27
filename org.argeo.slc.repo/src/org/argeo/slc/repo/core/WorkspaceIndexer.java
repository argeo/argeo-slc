package org.argeo.slc.repo.core;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.NodeIndexer;

/** Maintains the metadata of a workspace, using listeners */
public class WorkspaceIndexer {
	private final static CmsLog log = CmsLog.getLog(WorkspaceIndexer.class);

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
