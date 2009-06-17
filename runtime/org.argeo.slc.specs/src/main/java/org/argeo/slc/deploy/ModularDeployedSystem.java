package org.argeo.slc.deploy;

import java.util.List;

@SuppressWarnings("unchecked")
public interface ModularDeployedSystem<M extends Module> extends DeployedSystem {
	/** List the underlying deployed modules (in real time) */
	public List<M> listModules();
}
