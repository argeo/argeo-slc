package org.argeo.api.slc.deploy;

import java.io.File;
import java.util.Map;

public interface DeployEnvironment {
	public void unpackTo(Object packg, File targetLocation,
			Map<String, String> filter);
}
