package org.argeo.slc.detached;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.detached.admin.CloseSession;
import org.argeo.slc.detached.admin.OpenSession;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.BundleContextAware;

/** Default implementation of a detached server. */
public class DetachedExecutionServerImpl implements DetachedExecutionServer,
		BundleContextAware {
	private final static Log log = LogFactory
			.getLog(DetachedExecutionServerImpl.class);

	private final DetachedContextImpl detachedContext;
	private final List sessions;

	private int skipCount = 1;// start skipCount at 1 since the first step is
	// always an open session

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
					ApplicationContext.class.getName(), null);
			Object obj = null;
			for (int i = 0; i < refs.length; i++) {
				ApplicationContext appContext = (ApplicationContext) bundleContext
						.getService(refs[i]);
				try {
					obj = appContext.getBean(request.getRef());
				} catch (Exception e) {
					// silent
					if (log.isTraceEnabled())
						log.trace("Could not find ref " + request.getRef(), e);
				}
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
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			answer.setLog(writer.toString());
			IOUtils.closeQuietly(writer);
		}

		// Case where current session is unexpectly null
		if (getCurrentSession() == null) {
			log
					.error("CURRENT SESSION IS NULL."
							+ " Detached status is inconsistent dumping sessions history:");
			log.error(dumpSessionsHistory(request, answer));
			if (answer != null) {
				answer.setStatus(DetachedAnswer.ERROR);
				answer
						.addToLog("CURRENT SESSION IS NULL."
								+ " Detached status is inconsistent, see detached log for more details.");
				return answer;
			} else {
				throw new DetachedException(
						"Answer is null. Cannot return it. See log for more details.");
			}

		}

		getCurrentSession().getRequests().add(request);
		getCurrentSession().getAnswers().add(answer);
		if (log.isDebugEnabled())
			log.debug("Processed '" + request.getRef() + "' (status="
					+ answer.getStatusAsString() + ", path="
					+ request.getPath() + ")");
		return answer;
	}

	protected synchronized DetachedAnswer processStep(DetachedStep obj,
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
										+ " (skipCount=" + skipCount
										+ "). Reset skip count to 1");
						skipCount = 1;
					}
				} else {
					// went further as skip count, doing nothing.
				}
			}
		}

		if (execute) {
			DetachedStep step = (DetachedStep) obj;
			// Actually execute the step
			answer = step.execute(detachedContext, request);
		} else {
			answer = new DetachedAnswer(request);
			answer.setStatus(DetachedAnswer.SKIPPED);
			answer.setLog(skippedLog.toString());
		}
		return answer;
	}

	protected synchronized DetachedAnswer processAdminCommand(
			DetachedAdminCommand obj, DetachedRequest request) {
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

	/**
	 * Returns the current session based on the list of previous sessions.
	 * 
	 * @return the current session or null if there is no session yet defined or
	 *         if the last registered session is null or in error.
	 */
	protected synchronized final DetachedSession getCurrentSession() {
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

	protected synchronized String dumpSessionsHistory(
			DetachedRequest requestCurrent, DetachedAnswer answerCurrent) {
		StringBuffer buf = new StringBuffer("## SESSIONS HISTORY DUMP\n");
		buf.append("# CURRENT\n");
		buf.append("Current session: ").append(getCurrentSession())
				.append('\n');
		buf.append("Current request: ").append(requestCurrent).append('\n');
		buf.append("Current answer: ").append(requestCurrent).append('\n');
		buf.append("Skip count: ").append(skipCount).append('\n');

		buf.append("# SESSIONS\n");
		for (int i = 0; i < sessions.size(); i++) {
			DetachedSession session = (DetachedSession) sessions.get(i);
			buf.append(i).append(". ").append(session).append('\n');
			List requests = session.getRequests();
			List answers = session.getAnswers();
			for (int j = 0; j < requests.size(); j++) {
				DetachedRequest request = (DetachedRequest) requests.get(j);
				buf.append('\t').append(j).append(". ").append(request).append(
						'\n');
				if (answers.size() > j) {
					DetachedAnswer answer = (DetachedAnswer) answers.get(j);
					buf.append('\t').append(j).append(". ").append(answer)
							.append('\n');
				}
			}
		}

		buf.append("# DETACHED CONTEXT\n");
		buf.append(detachedContext).append('\n');

		return buf.toString();
	}

	protected synchronized final DetachedSession getPreviousSession() {
		if (sessions.size() < 2)
			return null;
		else
			return (DetachedSession) sessions.get(sessions.size() - 2);
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

}
