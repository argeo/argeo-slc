package org.argeo.slc.jcr.execution;

import java.util.List;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.ProcessThread;
import org.argeo.slc.core.runtime.DefaultAgent;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.jcr.SlcJcrConstants;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;

/** SLC agent synchronizing with a JCR repository. */
public class JcrAgent extends DefaultAgent implements SlcAgentFactory {
	private Session session;

	/*
	 * LIFECYCLE
	 */
	protected String initAgentUuid() {
		try {
			Node vmAgentFactoryNode = JcrUtils.mkdirs(session,
					SlcJcrConstants.VM_AGENT_FACTORY_PATH);
			vmAgentFactoryNode.addMixin(SlcTypes.SLC_AGENT_PROXY);
			if (!vmAgentFactoryNode.hasNodes()) {
				String uuid = UUID.randomUUID().toString();
				vmAgentFactoryNode.addNode(uuid);
			}
			session.save();

			return vmAgentFactoryNode.getNodes().nextNode().getName();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot find JCR agent UUID", e);
		} finally {
			JcrUtils.discardQuietly(session);
		}
	}

	@Override
	protected ProcessThread createProcessThread(
			ExecutionModulesManager modulesManager, ExecutionProcess process) {
		return new JcrProcessThread(modulesManager,
				(JcrExecutionProcess) process);
	}

	public void dispose() {

	}

	/*
	 * SLC AGENT FACTORY
	 */
	public SlcAgent getAgent(String uuid) {
		return this;
	}

	public void pingAll(List<String> activeAgentIds) {
		ping();
	}

	/*
	 * BEAN METHODS
	 */
	public void setSession(Session session) {
		this.session = session;
	}

}
