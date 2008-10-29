package org.argeo.slc.detached;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.detached.admin.CloseSession;
import org.argeo.slc.detached.admin.OpenSession;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class DetachedExecutionServerImpl implements DetachedExecutionServer {
	private final static Log log = LogFactory
			.getLog(DetachedExecutionServerImpl.class);

	private final DetachedContextImpl detachedContext;
	private final List sessions;

	private int skipCount = 0;

	private BundleContext bundleContext;

	public DetachedExecutionServerImpl() {
		detachedContext = new DetachedContextImpl();
		sessions = new Vector();
	}

	public synchronized DetachedAnswer executeRequest(DetachedRequest request) {
		DetachedAnswer answer = null;
		try {
			// Find action
			ServiceReference[] refs = bundleContext.getAllServiceReferences(
					StaticRefProvider.class.getName(), null);
			Object obj = null;
			for (int i = 0; i < refs.length; i++) {
				StaticRefProvider provider = (StaticRefProvider) bundleContext
						.getService(refs[i]);
				obj = provider.getStaticRef(request.getRef());
				if (obj != null) {
					break;
				}
			}

			if (obj == null)
				throw new DetachedException("Could not find action with ref "
						+ request.getRef());

			// Execute actions
			if (obj instanceof DetachedStep) {
				answer = processStep((DetachedStep) obj, request);

			} else if (obj instanceof DetachedAdminCommand) {
				answer = processAdminCommand((DetachedAdminCommand) obj,
						request);
			}

			if (answer == null) {
				throw new DetachedException("Unknown action type "
						+ obj.getClass() + " for action with ref "
						+ request.getRef());
			}
		} catch (Exception e) {
			answer = new DetachedAnswer(request);
			answer.setStatus(DetachedAnswer.ERROR);
			answer.setLog(e.getMessage());
		}
		getCurrentSession().getRequests().add(request);
		getCurrentSession().getAnswers().add(answer);
		if (log.isDebugEnabled())
			log.debug("Processed '" + request.getRef() + "' (status="
					+ answer.getStatusAsString() + ", path="
					+ request.getPath() + ")");
		return answer;
	}

	protected DetachedAnswer processStep(DetachedStep obj,
			DetachedRequest request) {
		DetachedAnswer answer;
		if (getCurrentSession() == null)
			throw new DetachedException("No open session.");

		StringBuffer skippedLog = new StringBuffer();
		boolean execute = true;
		if (getPreviousSession() != null && !getPreviousSession().isClosed()) {
			if (getCurrentSession().getDoItAgainPolicy().equals(
					DetachedSession.SKIP_UNTIL_ERROR)) {
				// Skip execution of already successful steps
				if (getPreviousSession().getAnswers().size() > skipCount) {
					DetachedAnswer previousAnswer = (DetachedAnswer) getPreviousSession()
							.getAnswers().get(skipCount);
					DetachedRequest previousRequest = (DetachedRequest) getPreviousSession()
							.getRequests().get(skipCount);
					// Check paths
					if (!previousRequest.getPath().equals(request.getPath())) {
						String msg = "New request is not consistent with previous path. previousPath="
								+ previousRequest.getPath()
								+ ", newPath="
								+ request.getPath() + "\n";
						skippedLog.append(msg);
						log.warn(msg);
					}

					if (previousAnswer.getStatus() != DetachedAnswer.ERROR) {
						execute = false;
						String msg = "Skipped path " + request.getPath()
								+ " (skipCount=" + skipCount + ")";
						skippedLog.append(msg);
						log.info(msg);
						skipCount++;
					} else {
						log
								.info("Path "
										+ request.getPath()
										+ " was previously in error, executing it again."
										+ " (skipCount=" + skipCount + ")");
					}
				} else {
					// went further as skip count, doing nothing.
				}
			}
		}

		if (execute) {
			DetachedStep step = (DetachedStep) obj;
			answer = step.execute(detachedContext, request);
		} else {
			answer = new DetachedAnswer(request);
			answer.setStatus(DetachedAnswer.SKIPPED);
			answer.setLog(skippedLog.toString());
		}
		return answer;
	}

	protected DetachedAnswer processAdminCommand(DetachedAdminCommand obj,
			DetachedRequest request) {
		DetachedAnswer answer;
		if (obj instanceof OpenSession) {
			if (getCurrentSession() != null)
				throw new DetachedException(
						"There is already an open session #"
								+ getCurrentSession().getUuid());
			sessions.add(((OpenSession) obj).execute(request, bundleContext));
			answer = new DetachedAnswer(request, "Session #"
					+ getCurrentSession().getUuid() + " open.");
		} else if (obj instanceof CloseSession) {
			if (getCurrentSession() == null)
				throw new DetachedException("There is no open session to close");
			answer = new DetachedAnswer(request, "Session #"
					+ getCurrentSession().getUuid() + " closed.");
			answer.setStatus(DetachedAnswer.CLOSED_SESSION);
		} else {
			answer = null;
		}
		return answer;
	}

	protected final DetachedSession getCurrentSession() {
		if (sessions.size() == 0) {
			return null;
		} else {
			DetachedSession session = (DetachedSession) sessions.get(sessions
					.size() - 1);
			List answers = session.getAnswers();
			if (answers.size() > 0) {
				DetachedAnswer lastAnswer = (DetachedAnswer) answers
						.get(answers.size() - 1);
				if (lastAnswer.getStatus() == DetachedAnswer.ERROR
						|| lastAnswer.getStatus() == DetachedAnswer.CLOSED_SESSION)
					return null;
			}
			return session;
		}
	}

	protected final DetachedSession getPreviousSession() {
		if (sessions.size() < 2)
			return null;
		else
			return (DetachedSession) sessions.get(sessions.size() - 2);
	}

	public void init(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

}
