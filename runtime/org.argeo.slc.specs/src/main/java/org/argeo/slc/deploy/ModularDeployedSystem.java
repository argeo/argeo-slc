package org.argeo.slc.deploy;

import java.util.List;

public interface ModularDeployedSystem extends DeployedSystem {
	/** List the underlying deployed modules (in real time) */
	public List<DeployedSystem> listModules();
}
