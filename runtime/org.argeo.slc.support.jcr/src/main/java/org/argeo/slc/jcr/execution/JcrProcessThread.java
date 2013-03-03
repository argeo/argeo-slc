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
package org.argeo.slc.jcr.execution;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.core.execution.ProcessThread;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.execution.RealizedFlow;
import org.argeo.slc.jcr.SlcNames;

/** Where the actual execution takes place */
public class JcrProcessThread extends ProcessThread implements SlcNames {

	public JcrProcessThread(ThreadGroup processesThreadGroup,
			ExecutionModulesManager executionModulesManager,
			JcrExecutionProcess process) {
		super(processesThreadGroup, executionModulesManager, process);
	}

	/** Overridden in order to set progress status on realized flow nodes. */
	@Override
	protected void process() throws InterruptedException {
		Session session = null;
		if (getProcess() instanceof JcrExecutionProcess)
			try {
				session = ((JcrExecutionProcess) getProcess()).getRepository()
						.login();

				List<RealizedFlow> realizedFlows = getProcess()
						.getRealizedFlows();
				for (RealizedFlow realizedFlow : realizedFlows) {
					Node realizedFlowNode = session
							.getNode(((JcrRealizedFlow) realizedFlow).getPath());
					setFlowStatus(realizedFlowNode, ExecutionProcess.RUNNING);

					try {
						//
						// EXECUTE THE FLOW
						//
						execute(realizedFlow, true);

						setFlowStatus(realizedFlowNode,
								ExecutionProcess.COMPLETED);
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
				throw new ArgeoException("Cannot process "
						+ getJcrExecutionProcess().getNodePath(), e);
			} finally {
				JcrUtils.logoutQuietly(session);
			}
		else
			super.process();
	}

	protected void setFlowStatus(Node realizedFlowNode, String status)
			throws RepositoryException {
		realizedFlowNode.setProperty(SLC_STATUS, status);
		realizedFlowNode.getSession().save();
	}

	protected JcrExecutionProcess getJcrExecutionProcess() {
		return (JcrExecutionProcess) getProcess();
	}
}
