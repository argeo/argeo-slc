package org.argeo.slc.jcr.execution;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.ExecutionStep;
import org.argeo.slc.execution.RealizedFlow;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.runtime.ProcessThread;

/** Execution process implementation based on a JCR node. */
public class JcrExecutionProcess implements ExecutionProcess, SlcNames {
	private final static CmsLog log = CmsLog.getLog(JcrExecutionProcess.class);
	private final Node node;

	private Long nextLogLine = 1l;

	public JcrExecutionProcess(Node node) {
		this.node = node;
	}

	public synchronized String getUuid() {
		try {
			return node.getProperty(SLC_UUID).getString();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot get uuid for " + node, e);
		}
	}

	public synchronized String getStatus() {
		try {
			return node.getProperty(SLC_STATUS).getString();
		} catch (RepositoryException e) {
			log.error("Cannot get status: " + e);
			// we should re-throw exception because this information can
			// probably used for monitoring in case there are already unexpected
			// exceptions
			return UNKOWN;
		}
	}

	public synchronized void setStatus(String status) {
		try {
			node.setProperty(SLC_STATUS, status);
			// last modified properties needs to be manually updated
			// see https://issues.apache.org/jira/browse/JCR-2233
			JcrUtils.updateLastModified(node);
			node.getSession().save();
		} catch (RepositoryException e) {
			JcrUtils.discardUnderlyingSessionQuietly(node);
			// we should re-throw exception because this information can
			// probably used for monitoring in case there are already unexpected
			// exceptions
			log.error("Cannot set status " + status + ": " + e);
		}
	}

	/**
	 * Synchronized in order to make sure that there is no concurrent modification
	 * of {@link #nextLogLine}.
	 */
	public synchronized void addSteps(List<ExecutionStep> steps) {
		try {
			steps: for (ExecutionStep step : steps) {
				String type;
				if (step.getType().equals(ExecutionStep.TRACE))
					type = SlcTypes.SLC_LOG_TRACE;
				else if (step.getType().equals(ExecutionStep.DEBUG))
					type = SlcTypes.SLC_LOG_DEBUG;
				else if (step.getType().equals(ExecutionStep.INFO))
					type = SlcTypes.SLC_LOG_INFO;
				else if (step.getType().equals(ExecutionStep.WARNING))
					type = SlcTypes.SLC_LOG_WARNING;
				else if (step.getType().equals(ExecutionStep.ERROR))
					type = SlcTypes.SLC_LOG_ERROR;
				else
					// skip
					continue steps;

				String relPath = SLC_LOG + '/' + step.getThread().replace('/', '_') + '/'
						+ step.getLocation().replace('.', '/');
				String path = node.getPath() + '/' + relPath;
				// clean special character
				// TODO factorize in JcrUtils
				path = path.replace('@', '_');

				Node location = JcrUtils.mkdirs(node.getSession(), path);
				Node logEntry = location.addNode(Long.toString(nextLogLine), type);
				logEntry.setProperty(SLC_MESSAGE, step.getLog());
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(step.getTimestamp());
				logEntry.setProperty(SLC_TIMESTAMP, calendar);

				// System.out.println("Logged " + logEntry.getPath());

				nextLogLine++;
			}

			// last modified properties needs to be manually updated
			// see https://issues.apache.org/jira/browse/JCR-2233
			JcrUtils.updateLastModified(node);

			node.getSession().save();
		} catch (Exception e) {
			JcrUtils.discardUnderlyingSessionQuietly(node);
			e.printStackTrace();
		}
	}

	// public Node getNode() {
	// return node;
	// }

	public List<RealizedFlow> getRealizedFlows() {
		try {
			List<RealizedFlow> realizedFlows = new ArrayList<RealizedFlow>();
			Node rootRealizedFlowNode = node.getNode(SLC_FLOW);
			// we just manage one level for the time being
			NodeIterator nit = rootRealizedFlowNode.getNodes(SLC_FLOW);
			while (nit.hasNext()) {
				Node realizedFlowNode = nit.nextNode();

				if (realizedFlowNode.hasNode(SLC_ADDRESS)) {
					String flowPath = realizedFlowNode.getNode(SLC_ADDRESS).getProperty(Property.JCR_PATH).getString();
					NameVersion moduleNameVersion = SlcJcrUtils.moduleNameVersion(flowPath);
					((ProcessThread) Thread.currentThread()).getExecutionModulesManager().start(moduleNameVersion);
				}

				RealizedFlow realizedFlow = new JcrRealizedFlow(realizedFlowNode);
				if (realizedFlow != null)
					realizedFlows.add(realizedFlow);
			}
			return realizedFlows;
		} catch (RepositoryException e) {
			throw new SlcException("Cannot get realized flows", e);
		}
	}

	public String getNodePath() {
		try {
			return node.getPath();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot get process node path for " + node, e);
		}
	}

	public Repository getRepository() {
		try {
			return node.getSession().getRepository();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot get process JCR repository for " + node, e);
		}
	}
}
