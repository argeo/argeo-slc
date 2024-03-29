package org.argeo.slc.runtime;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.WARNING;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

import org.argeo.api.slc.execution.ExecutionFlowDescriptor;
import org.argeo.api.slc.execution.ExecutionModulesManager;
import org.argeo.api.slc.execution.ExecutionStep;
import org.argeo.api.slc.execution.FlowConfigurationException;
import org.argeo.api.slc.execution.RealizedFlow;

/** Thread of a single execution */
public class ExecutionThread extends Thread {
	public final static String SYSPROP_EXECUTION_AUTO_UPGRADE = "slc.execution.autoupgrade";
	private final static System.Logger logger = System.getLogger(ExecutionThread.class.getName());

	private ExecutionModulesManager executionModulesManager;
	private final RealizedFlow realizedFlow;
	private final AccessControlContext accessControlContext;

	private List<Runnable> destructionCallbacks = new ArrayList<Runnable>();

	public ExecutionThread(ProcessThreadGroup processThreadGroup, ExecutionModulesManager executionModulesManager,
			RealizedFlow realizedFlow) {
		super(processThreadGroup, "Flow " + realizedFlow.getFlowDescriptor().getName());
		this.realizedFlow = realizedFlow;
		this.executionModulesManager = executionModulesManager;
		accessControlContext = AccessController.getContext();
	}

	public void run() {
		// authenticate thread
		// Authentication authentication = getProcessThreadGroup()
		// .getAuthentication();
		// if (authentication == null)
		// throw new SlcException("Can only execute authenticated threads");
		// SecurityContextHolder.getContext().setAuthentication(authentication);

		// Retrieve execution flow descriptor
		ExecutionFlowDescriptor executionFlowDescriptor = realizedFlow.getFlowDescriptor();
		String flowName = executionFlowDescriptor.getName();

		getProcessThreadGroup().dispatchAddStep(
				new ExecutionStep(realizedFlow.getModuleName(), ExecutionStep.PHASE_START, "Flow " + flowName));

		try {
			Subject subject = Subject.getSubject(accessControlContext);
			try {
				Subject.doAs(subject, new PrivilegedExceptionAction<Void>() {

					@Override
					public Void run() throws Exception {
						String autoUpgrade = System.getProperty(SYSPROP_EXECUTION_AUTO_UPGRADE);
						if (autoUpgrade != null && autoUpgrade.equals("true"))
							executionModulesManager.upgrade(realizedFlow.getModuleNameVersion());
						executionModulesManager.start(realizedFlow.getModuleNameVersion());
						//
						// START FLOW
						//
						executionModulesManager.execute(realizedFlow);
						// END FLOW
						return null;
					}

				});
			} catch (PrivilegedActionException privilegedActionException) {
				throw (Exception) privilegedActionException.getCause();
			}
		} catch (FlowConfigurationException e) {
			String msg = "Configuration problem with flow " + flowName + ":\n" + e.getMessage();
			logger.log(ERROR, msg);
			getProcessThreadGroup().dispatchAddStep(
					new ExecutionStep(realizedFlow.getModuleName(), ExecutionStep.ERROR, msg + " " + e.getMessage()));
		} catch (Exception e) {
			// TODO: re-throw exception ?
			String msg = "Execution of flow " + flowName + " failed.";
			logger.log(ERROR, msg, e);
			getProcessThreadGroup().dispatchAddStep(
					new ExecutionStep(realizedFlow.getModuleName(), ExecutionStep.ERROR, msg + " " + e.getMessage()));
		} finally {
			getProcessThreadGroup().dispatchAddStep(
					new ExecutionStep(realizedFlow.getModuleName(), ExecutionStep.PHASE_END, "Flow " + flowName));
			processDestructionCallbacks();
		}
	}

	private synchronized void processDestructionCallbacks() {
		for (int i = destructionCallbacks.size() - 1; i >= 0; i--) {
			try {
				destructionCallbacks.get(i).run();
			} catch (Exception e) {
				logger.log(WARNING, "Could not process destruction callback " + i + " in thread " + getName(), e);
			}
		}
	}

	/**
	 * Gather object destruction callback to be called in reverse order at the end
	 * of the thread
	 */
	public synchronized void registerDestructionCallback(String name, Runnable callback) {
		destructionCallbacks.add(callback);
	}

	protected ProcessThreadGroup getProcessThreadGroup() {
		return (ProcessThreadGroup) getThreadGroup();
	}
}