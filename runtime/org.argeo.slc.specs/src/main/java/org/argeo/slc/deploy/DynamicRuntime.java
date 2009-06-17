package org.argeo.slc.deploy;

@SuppressWarnings("unchecked")
public interface DynamicRuntime<M extends Module> extends
		ModularDeployedSystem<M> {
	public void shutdown();

}
