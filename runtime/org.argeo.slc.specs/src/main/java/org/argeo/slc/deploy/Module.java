package org.argeo.slc.deploy;

import org.argeo.slc.build.Distribution;

public interface Module {
	public String getName();
	public String getVersion();
	public Distribution getDistribution();
}
