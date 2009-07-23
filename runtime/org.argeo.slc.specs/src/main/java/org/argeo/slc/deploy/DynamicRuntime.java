package org.argeo.slc.deploy;

import org.argeo.slc.build.Distribution;
import org.argeo.slc.build.NameVersion;

public interface DynamicRuntime<M extends Module> extends
		ModularDeployedSystem<M> {
	public void shutdown();

	public M installModule(Distribution distribution);

	public void uninstallModule(NameVersion nameVersion);

	public void startModule(NameVersion nameVersion);

}
