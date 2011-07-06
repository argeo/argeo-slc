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
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.runtime.SlcAgent;
import org.argeo.slc.runtime.SlcAgentFactory;

/** SLC VM agent synchronizing with a JCR repository. */
public class JcrAgent extends DefaultAgent implements SlcAgentFactory, SlcNames {
	private Session session;

	/** only one agent per VM is currently supported */
	private final String agentNodeName = "default";

	/*
	 * LIFECYCLE
	 */
	protected String initAgentUuid() {
		try {
			Node vmAgentFactoryNode = JcrUtils.mkdirs(session,
					SlcJcrConstants.VM_AGENT_FACTORY_PATH,
					SlcTypes.SLC_AGENT_FACTORY);
			if (!vmAgentFactoryNode.hasNode(agentNodeName)) {
				String uuid = UUID.randomUUID().toString();
				Node agentNode = vmAgentFactoryNode.addNode(agentNodeName,
						SlcTypes.SLC_AGENT);
				agentNode.setProperty(SLC_UUID, uuid);
			}
			session.save();
			return vmAgentFactoryNode.getNode(agentNodeName)
					.getProperty(SLC_UUID).getString();
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(session);
			throw new SlcException("Cannot find JCR agent UUID", e);
		}
	}

	public void dispose() {

	}

	/*
	 * SLC AGENT
	 */
	@Override
	protected ProcessThread createProcessThread(
			ThreadGroup processesThreadGroup,
			ExecutionModulesManager modulesManager, ExecutionProcess process) {
		return new JcrProcessThread(processesThreadGroup, modulesManager,
				(JcrExecutionProcess) process);
	}

	/*
	 * SLC AGENT FACTORY
	 */
	public SlcAgent getAgent(String uuid) {
		if (!uuid.equals(getAgentUuid()))
			throw new SlcException("Internal UUID " + getAgentUuid()
					+ " is different from argument UUID " + uuid);
		return this;
	}

	public void pingAll(List<String> activeAgentIds) {
		ping();
	}

	/*
	 * UTILITIES
	 */
	public String getNodePath() {
		return SlcJcrConstants.VM_AGENT_FACTORY_PATH + '/' + getAgentNodeName();
	}

	/*
	 * BEAN
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	public String getAgentNodeName() {
		return agentNodeName;
	}

}
