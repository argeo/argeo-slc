package org.argeo.slc.cms.deploy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SimpleCmsDeploymentData implements CmsDeploymentData {
	private Map<Integer, List<String>> startLevels = new TreeMap<>();

	@Override
	public List<String> getModulesToActivate(int startLevel) {
		startLevels.putIfAbsent(startLevel, new ArrayList<>());
		return startLevels.get(startLevel);
	}

}
