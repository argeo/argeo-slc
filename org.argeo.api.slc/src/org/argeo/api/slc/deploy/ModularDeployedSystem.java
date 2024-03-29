package org.argeo.api.slc.deploy;

import java.util.List;

public interface ModularDeployedSystem<M extends Module> extends DeployedSystem {
	/** List the underlying deployed modules (in real time) */
	public List<M> listModules();
}
