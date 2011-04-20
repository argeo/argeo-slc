package org.argeo.slc.jcr.execution;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.jcr.SlcNames;

/** Execution process implementation based on a JCR node. */
public class JcrExecutionProcess implements ExecutionProcess {
	private Log log = LogFactory.getLog(JcrExecutionProcess.class);
	private final Node node;

	public JcrExecutionProcess(Node node) {
		this.node = node;
	}

	public String getUuid() {
		try {
			return node.getProperty(SlcNames.SLC_UUID).getString();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot get uuid for " + node, e);
		}
	}

	public String getStatus() {
		try {
			return node.getProperty(SlcNames.SLC_STATUS).getString();
		} catch (RepositoryException e) {
			log.error("Cannot get status: " + e);
			// we should re-throw exception because this information can
			// probably used for monitoring in case there are already unexpected
			// exceptions
			return UNKOWN;
		}
	}

	public void setStatus(String status) {
		try {
			node.setProperty(SlcNames.SLC_STATUS, status);
			// last modified properties needs to be manually updated
			// see https://issues.apache.org/jira/browse/JCR-2233
			JcrUtils.updateLastModified(node);
			node.getSession().save();
		} catch (RepositoryException e) {
			try {
				JcrUtils.discardQuietly(node.getSession());
			} catch (RepositoryException e1) {
				// silent
			}
			// we should re-throw exception because this information can
			// probably used for monitoring in case there are already unexpected
			// exceptions
			log.error("Cannot set status " + status + ": " + e);
		}
	}

	public Node getNode() {
		return node;
	}

}
