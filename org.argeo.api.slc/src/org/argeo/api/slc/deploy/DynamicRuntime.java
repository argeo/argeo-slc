package org.argeo.api.slc.deploy;

import org.argeo.api.slc.NameVersion;
import org.argeo.api.slc.build.Distribution;

public interface DynamicRuntime<M extends Module> extends
		ModularDeployedSystem<M> {
	public void shutdown();

	public M installModule(Distribution distribution);

	public void uninstallModule(NameVersion nameVersion);

	public void updateModule(NameVersion nameVersion);

	public void startModule(NameVersion nameVersion);

}
