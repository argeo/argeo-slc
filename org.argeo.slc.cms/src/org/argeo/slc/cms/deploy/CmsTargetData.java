package org.argeo.slc.cms.deploy;

import java.nio.file.Path;

import org.argeo.slc.deploy.TargetData;

public interface CmsTargetData extends TargetData {
	Path getInstanceData();

	Integer getHttpPort();

}
