package org.argeo.slc.detached;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.detached.admin.CloseSession;
import org.argeo.slc.detached.admin.OpenSession;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.osgi.context.BundleContextAware;

/** Default implementation of a detached server. */
public class DetachedExecutionServerImpl implements DetachedExecutionServer,
		BundleContextAware, InitializingBean, DisposableBean,
		ApplicationContextAware {
	private final static Log log = LogFactory
			.getLog(DetachedExecutionServerImpl.class);

	private final DetachedContextImpl detachedContext;
	private final List sessions;

	private int skipCount = 1;// start skipCount at 1 since the first step is
	// always an open session

	private BundleContext bundleContext;
	private ApplicationContext applicationContext;

	private final static String ALL_APP_CONTEXTS_KEY = "__allApplicationContexts";

	private Map/* <String,ServiceTracker> */appContextServiceTrackers = Collections
			.synchronizedMap(new HashMap());

	public DetachedExecutionServerImpl() {
		detachedContext = new DetachedContextImpl();
		sessions = new Vector();
	}

	public synchronized DetachedAnswer executeRequest(DetachedRequest request) {
		log.info("Received " + request);

		DetachedAnswer answer = null;
		try {
			Object obj = retrieveStep(request);

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

		// Case where current session is unexpectedly null
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
		log.info("Sent " + answer);
		return answer;
	}

	protected synchronized Object retrieveStep(DetachedRequest request)
			throws Exception {

		// Check whether there is a cached object
		if (request.getCachedObject() != null) {
			Object cachedObj = request.getCachedObject();
			if (log.isTraceEnabled())
				log.trace("Use cached object " + cachedObj + " for request "
						+ request);
			return cachedObj;
		}

		// Check its own app context (typically for admin steps)
		if (applicationContext.containsBean(request.getRef())) {
			try {
				Object obj = applicationContext.getBean(request.getRef());
				if (log.isTraceEnabled())
					log.trace("Retrieve from server app context " + obj
							+ " for request " + request);
				return obj;
			} catch (Exception e) {
				if (log.isTraceEnabled())
					log.trace("Could not retrieve " + request.getRef()
							+ " from server app context: " + e);
			}
		}

		// Check whether the source bundle is set
		String bundleName = request.getProperties().getProperty(
				Constants.BUNDLE_SYMBOLICNAME);

		ApplicationContext sourceAppContext = null;
		if (bundleName != null) {
			if (!appContextServiceTrackers.containsKey(bundleName)) {
				ServiceTracker nSt = new ServiceTracker(bundleContext,
						bundleContext.createFilter("(Bundle-SymbolicName="
								+ bundleName + ")"), null);
				nSt.open();
				appContextServiceTrackers.put(bundleName, nSt);
			}
			ServiceTracker st = (ServiceTracker) appContextServiceTrackers
					.get(bundleName);
			sourceAppContext = (ApplicationContext) st.getService();
			if (log.isTraceEnabled())
				log.trace("Use source application context from bundle "
						+ bundleName);

			Object obj = null;
			try {
				obj = sourceAppContext.getBean(request.getRef());
			} catch (Exception e) {
				if (log.isTraceEnabled())
					log.trace("Could not retrieve " + request.getRef()
							+ " from app context of " + bundleName + ": " + e);
			}
			return obj;
		}

		// no bundle name specified or it failed
		if (!appContextServiceTrackers.containsKey(ALL_APP_CONTEXTS_KEY)) {
			ServiceTracker nSt = new ServiceTracker(bundleContext,
					ApplicationContext.class.getName(), null);
			nSt.open();
			appContextServiceTrackers.put(ALL_APP_CONTEXTS_KEY, nSt);
		}
		ServiceTracker st = (ServiceTracker) appContextServiceTrackers
				.get(ALL_APP_CONTEXTS_KEY);
		Object[] arr = st.getServices();
		for (int i = 0; i < arr.length; i++) {
			ApplicationContext appC = (ApplicationContext) arr[i];
			if (appC.containsBean(request.getRef())) {
				sourceAppContext = appC;
				if (log.isTraceEnabled())
					log
							.trace("Retrieved source application context "
									+ "by scanning all published application contexts.");
				try {
					Object obj = sourceAppContext.getBean(request.getRef());
					return obj;
				} catch (Exception e) {
					if (log.isTraceEnabled())
						log.trace("Could not retrieve " + request.getRef()
								+ " from app context " + appC + ": " + e);
				}
			}
		}

		// ServiceReference[] refs = bundleContext.getAllServiceReferences(
		// ApplicationContext.class.getName(), null);
		// Object obj = null;
		// for (int i = 0; i < refs.length; i++) {
		// ApplicationContext appContext = (ApplicationContext)
		// bundleContext
		// .getService(refs[i]);
		// try {
		// obj = appContext.getBean(request.getRef());
		// } catch (Exception e) {
		// // silent
		// if (log.isTraceEnabled())
		// log.trace("Could not find ref " + request.getRef(), e);
		// }
		// if (obj != null) {
		// break;
		// }
		// }
		// return obj;

		throw new Exception(
				"Cannot find any published application context containing bean "
						+ request.getRef());
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
			if (getCurrentSession() != null) {
				// TODO: better understand why there is sometimes two open
				// sessions sent.
				log.warn("There is already an open session #"
						+ getCurrentSession().getUuid() + ". Closing it...");
				DetachedAnswer answerT = new DetachedAnswer(
						request,
						"Session #"
								+ getCurrentSession().getUuid()
								+ " forcibly closed. THIS ANSWER WAS NOT SENT BACK.");
				answerT.setStatus(DetachedAnswer.CLOSED_SESSION);
				getCurrentSession().getAnswers().add(answerT);
			}
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
		StringBuffer buf = new StringBuffer(
				"##\n## SESSIONS HISTORY DUMP\n##\n");
		buf.append("# CURRENT\n");
		buf.append("Current session: ").append(getCurrentSession())
				.append('\n');
		buf.append("Current request: ").append(requestCurrent).append('\n');
		buf.append("Current answer: ").append(answerCurrent).append('\n');
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

		buf.append("##\n## END OF SESSIONS HISTORY DUMP\n##\n");
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

	public void afterPropertiesSet() throws Exception {
		log.info("Detached execution server initialized.");
	}

	public synchronized void destroy() throws Exception {
		Iterator/* <String> */keys = appContextServiceTrackers.keySet()
				.iterator();
		while (keys.hasNext()) {
			ServiceTracker st = (ServiceTracker) appContextServiceTrackers
					.get(keys.next());
			st.close();
		}
		appContextServiceTrackers.clear();

		log.info("Detached execution server closed.");
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
