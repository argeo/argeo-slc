package org.argeo.slc.ant.deploy;

import java.lang.reflect.Method;

import org.argeo.slc.ant.structure.SAwareTask;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.deploy.DeployedSystem;
import org.argeo.slc.core.deploy.DeployedSystemManager;

public class SlcManagerTask extends SAwareTask {
	private String action;
	private String manager;

	@Override
	protected void executeActions(String mode) {
		DeployedSystemManager<DeployedSystem> systemManager = getBean(manager);

		try {
			Method method = systemManager.getClass().getMethod(action, null);
			method.invoke(systemManager, null);
		} catch (Exception e) {
			throw new SlcException("Cannot execute action " + action
					+ " for manager " + manager);
		}
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

}
