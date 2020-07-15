package org.argeo.slc.lib.linux;

import java.util.List;

import org.argeo.slc.build.Distribution;

public interface RpmDistribution extends Distribution {
	public List<String> getAdditionalPackages();

	public void setAdditionalPackages(List<String> additionalPackages);

}
