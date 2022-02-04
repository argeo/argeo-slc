package org.argeo.slc.build.m2;

import org.argeo.slc.DefaultCategoryNameVersion;

public class DefaultArtifact extends DefaultCategoryNameVersion implements Artifact {

	@Override
	public String getGroupId() {
		return getCategory();
	}

	@Override
	public String getArtifactId() {
		return getName();
	}

}
