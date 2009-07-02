package org.argeo.slc.deploy;

public interface DynamicRuntime<M extends Module> extends
		ModularDeployedSystem<M> {
	public void shutdown();

}
