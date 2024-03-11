package org.argeo.slc.cms.deploy;

import java.util.List;

import org.argeo.api.slc.deploy.DeploymentData;

public interface CmsDeploymentData extends DeploymentData {
	List<String> getModulesToActivate(int startLevel);
}
