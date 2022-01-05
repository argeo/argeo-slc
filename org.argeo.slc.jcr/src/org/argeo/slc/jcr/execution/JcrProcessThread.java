package org.argeo.slc.jcr.execution;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.api.cms.CmsConstants;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.RealizedFlow;
import org.argeo.slc.runtime.ProcessThread;

/** Where the actual execution takes place */
public class JcrProcessThread extends ProcessThread implements SlcNames {

	public JcrProcessThread(ThreadGroup processesThreadGroup, ExecutionModulesManager executionModulesManager,
			JcrExecutionProcess process) {
		super(processesThreadGroup, executionModulesManager, process);
	}

	/** Overridden in order to set progress status on realized flow nodes. */
	@Override
	protected void process() throws InterruptedException {
		Session session = null;
		if (getProcess() instanceof JcrExecutionProcess)
			try {
				session = ((JcrExecutionProcess) getProcess()).getRepository().login(CmsConstants.HOME_WORKSPACE);

				List<RealizedFlow> realizedFlows = getProcess().getRealizedFlows();
				for (RealizedFlow realizedFlow : realizedFlows) {
					Node realizedFlowNode = session.getNode(((JcrRealizedFlow) realizedFlow).getPath());
					setFlowStatus(realizedFlowNode, ExecutionProcess.RUNNING);

					try {
						//
						// EXECUTE THE FLOW
						//
						execute(realizedFlow, true);

						setFlowStatus(realizedFlowNode, ExecutionProcess.COMPLETED);
					} catch (RepositoryException e) {
						throw e;
					} catch (InterruptedException e) {
						setFlowStatus(realizedFlowNode, ExecutionProcess.KILLED);
						throw e;
					} catch (RuntimeException e) {
						setFlowStatus(realizedFlowNode, ExecutionProcess.ERROR);
						throw e;
					}
				}
			} catch (RepositoryException e) {
				throw new SlcException("Cannot process " + getJcrExecutionProcess().getNodePath(), e);
			} finally {
				JcrUtils.logoutQuietly(session);
			}
		else
			super.process();
	}

	protected void setFlowStatus(Node realizedFlowNode, String status) throws RepositoryException {
		realizedFlowNode.setProperty(SLC_STATUS, status);
		realizedFlowNode.getSession().save();
	}

	protected JcrExecutionProcess getJcrExecutionProcess() {
		return (JcrExecutionProcess) getProcess();
	}
}
