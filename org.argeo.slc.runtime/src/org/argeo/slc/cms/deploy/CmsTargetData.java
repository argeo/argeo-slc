package org.argeo.slc.cms.deploy;

import java.nio.file.Path;

import org.argeo.api.slc.deploy.TargetData;

public interface CmsTargetData extends TargetData {
	Path getInstanceData();

	String getHost();

	Integer getHttpPort();

}
