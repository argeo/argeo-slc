package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.RepositoryFactory;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.repo.RepoSync;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/** Wraps a {@link RepoSync} as an Eclipse command. */
public class RepoSyncCommand extends AbstractHandler {
	public final static String ID = DistPlugin.ID + ".repoSync";
	public final static String PARAM_SOURCE_REPO = "sourceRepo";
	public final static String PARAM_TARGET_REPO = "targetRepo";
	public final static String DEFAULT_LABEL = "Repo sync";
	public final static String DEFAULT_ICON_PATH = "icons/addItem.gif";
	
	private RepositoryFactory repositoryFactory;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		RepoSync repoSync = new RepoSync();
		repoSync.setRepositoryFactory(repositoryFactory);
		repoSync.setSourceRepo(event.getParameter(PARAM_SOURCE_REPO));
		repoSync.setTargetRepo(event.getParameter(PARAM_TARGET_REPO));
		repoSync.run();
		return null;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

}
