package org.argeo.slc.client.ui.dist.wizards;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.eclipse.jface.wizard.Wizard;

/**
 * Small wizard to manage authorizations on the root node of the current
 * workspace
 */
public class ChangeRightsWizard extends Wizard {

	private Session currentSession;

	// This page widget
	private ChooseRightsPage page;

	public ChangeRightsWizard(Session currentSession) {
		super();
		this.currentSession = currentSession;
	}

	@Override
	public void addPages() {
		try {
			page = new ChooseRightsPage();
			addPage(page);
		} catch (Exception e) {
			throw new SlcException("Cannot add page to wizard ", e);
		}
	}

	@Override
	public boolean performFinish() {
		if (!canFinish())
			return false;
		try {
			JcrUtils.addPrivilege(currentSession, "/", page.getGroupName(),
					page.getAuthTypeStr());
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while setting privileges", re);
		}
		return true;
	}
}
