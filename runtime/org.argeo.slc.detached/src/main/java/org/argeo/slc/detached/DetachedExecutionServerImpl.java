package org.argeo.slc.detached;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

	private DetachedSession currentSession;

	/**
	 * Session being replayed, skipping the steps in the current session. If
	 * null, no session is replayed
	 */
	private DetachedSession replayedSession = null;

	private BundleContext bundleContext;
	private ApplicationContext applicationContext;

	private final static String ALL_APP_CONTEXTS_KEY = "__allApplicationContexts";

	private Map/* <String,ServiceTracker> */appContextServiceTrackers = Collections
			.synchronizedMap(new HashMap());

	public DetachedExecutionServerImpl() {
		detachedContext = new DetachedContextImpl();
		currentSession = new DetachedSession();
		currentSession.setUuid(Long.toString(System.currentTimeMillis()));		
	}

	public synchronized DetachedAnswer executeRequest(DetachedRequest request) {
		if(log.isDebugEnabled())
			log.debug("Received " + request);

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
			log.error("Error executing request " + request, e);
		}

		currentSession.getRequests().add(request);
		currentSession.getAnswers().add(answer);
		if(log.isDebugEnabled())
			log.debug("Sent " + answer);
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

		throw new Exception(
				"Cannot find any published application context containing bean "
						+ request.getRef());
	}

	protected synchronized DetachedAnswer processStep(DetachedStep obj,
			DetachedRequest request) {
		DetachedAnswer answer;
		
		StringBuffer skippedLog = new StringBuffer();
		boolean execute = true;

		if (replayedSession != null) {
			// Skip execution of already successful steps
			int stepIndex = currentSession.getExecutedStepCount();

			if (stepIndex < replayedSession.getExecutedStepCount()) {
				DetachedAnswer previousAnswer = (DetachedAnswer) replayedSession
						.getAnswers().get(stepIndex);
				DetachedRequest previousRequest = (DetachedRequest) replayedSession
						.getRequests().get(stepIndex);

				// check step names				
				if (!previousRequest.getRef().equals(request.getRef())) {
					String msg = "New request is not consistent with previous ref. previousRef="
							+ previousRequest.getRef()
							+ ", newRef="
							+ request.getRef() + "\n";
					skippedLog.append(msg);
					log.warn(msg);
				}				
				
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
					// if no error occurred in the replayedSession,
					// skip the step
					execute = false;
					String msg = "Skipped Step " + request.getRef()
							+ " (stepIndex=" + stepIndex + ")";
					skippedLog.append(msg);
					log.info(msg);

				} else {
					// if an error occurred, execute the step and leave
					// skipUntillError mode (even if replayedSession
					// has more steps)
					log.info("### End of SkipUntilError Mode ###");
					log.info("Step " + request.getRef()
							+ " was previously in error, executing it again."
							+ " (stepIndex=" + stepIndex + ").");
					replayedSession = null;
				}
			} else {
				// went further as skip count, doing nothing.
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
			DetachedSession newSession = ((OpenSession) obj).execute(request,
					bundleContext);

			log.debug("Creating new DetachedSession : " + newSession);

			if ((currentSession != null) && currentSession.lastActionIsError()
					&& DetachedSession.SKIP_UNTIL_ERROR.equals(newSession.getDoItAgainPolicy())) {
				// switch to replay mode
				log.info("### Start SkipUntilError Mode ###");
				replayedSession = currentSession;
			}

			currentSession = newSession;

			answer = new DetachedAnswer(request, "Session #"
					+ currentSession.getUuid() + " open.");
		} else if (obj instanceof CloseSession) {
			if (currentSession == null)
				throw new DetachedException("There is no open session to close");
			answer = new DetachedAnswer(request, "Session #"
					+ currentSession.getUuid() + " closed.");
			answer.setStatus(DetachedAnswer.CLOSED_SESSION);
		} else {
			answer = null;
		}
		return answer;
	}

	protected synchronized String dumpSessionsHistory(
			DetachedRequest requestCurrent, DetachedAnswer answerCurrent) {
		StringBuffer buf = new StringBuffer(
				"##\n## SESSIONS HISTORY DUMP\n##\n");
		buf.append("# CURRENT\n");
		buf.append("Current session: ").append(currentSession)
				.append('\n');
		buf.append("Current request: ").append(requestCurrent).append('\n');
		buf.append("Current answer: ").append(answerCurrent).append('\n');

		buf.append("# CURRENT SESSION\n");

		List requests = currentSession.getRequests();
		List answers = currentSession.getAnswers();
		for (int j = 0; j < requests.size(); j++) {
			DetachedRequest request = (DetachedRequest) requests.get(j);
			buf.append('\t').append(j).append(". ").append(request)
					.append('\n');
			if (answers.size() > j) {
				DetachedAnswer answer = (DetachedAnswer) answers.get(j);
				buf.append('\t').append(j).append(". ").append(answer).append(
						'\n');
			}
		}

		buf.append("# DETACHED CONTEXT\n");
		buf.append(detachedContext).append('\n');

		buf.append("##\n## END OF SESSIONS HISTORY DUMP\n##\n");
		return buf.toString();
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void afterPropertiesSet() throws Exception {
		log.debug("Detached execution server initialized.");
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

		log.debug("Detached execution server closed.");
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
