package org.argeo.slc.core.deploy;

import java.io.File;
import java.util.Map;

public interface DeployEnvironment {
	public void unpackTo(Object packg, File targetLocation,
			Map<String, String> filter);
}
