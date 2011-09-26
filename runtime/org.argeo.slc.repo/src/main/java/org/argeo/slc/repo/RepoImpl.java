package org.argeo.slc.repo;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;

/** Repository backend, maintain the JCR repository, mainly through listeners */
public class RepoImpl {

	private Repository jcrRepository;
	private Session adminSession;

	private ArtifactListener artifactListener;

	/** order may be important */
	private List<NodeIndexer> nodeIndexers = new ArrayList<NodeIndexer>();

	public void init() {
		try {
			adminSession = jcrRepository.login();
			artifactListener = new ArtifactListener();
			adminSession
					.getWorkspace()
					.getObservationManager()
					.addEventListener(artifactListener, Event.NODE_ADDED,
							RepoConstants.ARTIFACTS_BASE_PATH, true, null,
							null, true);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot initialize repository backend", e);
		}
	}

	public void destroy() {
		JcrUtils.logoutQuietly(adminSession);
	}

	public void setJcrRepository(Repository jcrRepository) {
		this.jcrRepository = jcrRepository;
	}

	public void setNodeIndexers(List<NodeIndexer> nodeIndexers) {
		this.nodeIndexers = nodeIndexers;
	}

	class ArtifactListener implements EventListener {

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
