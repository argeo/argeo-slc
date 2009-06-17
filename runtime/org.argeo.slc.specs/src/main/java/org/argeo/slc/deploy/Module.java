package org.argeo.slc.deploy;

import org.argeo.slc.build.Distribution;

public interface Module<D extends Distribution> extends DeployedSystem<D> {
	public String getName();

	public String getVersion();
}
