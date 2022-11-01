package org.argeo.cms.jcr.e4.rap;

import org.argeo.cms.e4.rap.AbstractRapE4App;
import org.eclipse.rap.rwt.application.Application;

/**
 * Access to canonical views of the core CMS concepts, useful for devleopers and
 * operators.
 */
public class CmsE4AdminApp extends AbstractRapE4App {
	@Override
	protected void addEntryPoints(Application application) {
		addE4EntryPoint(application, "/devops", "org.argeo.tool.devops.e4/e4xmi/devops.e4xmi",
				customise("Argeo Tool DevOps"));
	}

}
