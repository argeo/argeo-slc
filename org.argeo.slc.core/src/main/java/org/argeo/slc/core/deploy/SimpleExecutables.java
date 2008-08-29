package org.argeo.slc.core.deploy;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.build.Distribution;

public class SimpleExecutables implements InstalledExecutables {
	private final static Log log = LogFactory.getLog(SimpleExecutables.class);

	private String baseDir;
	private Map<String, String> paths = new TreeMap<String, String>();

	private Distribution distribution;

	public String getExecutablePath(String key) {
		String path = paths.get(key);
		if (path == null) {
			if (log.isDebugEnabled())
				log.debug("No executable path found for key " + key
						+ ", using the key as executable name.");
			path = key;
		}

		if (baseDir != null)
			path = baseDir + File.separator + path;
		return path;
	}

	public String getDeployedSystemId() {
		// TODO Auto-generated method stub
		return null;
	}

	public DeploymentData getDeploymentData() {
		// TODO Auto-generated method stub
		return null;
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public TargetData getTargetData() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public Map<String, String> getPaths() {
		return paths;
	}

	public void setPaths(Map<String, String> paths) {
		this.paths = paths;
	}

	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

}
