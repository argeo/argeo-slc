package org.argeo.slc.akb.ui.editors;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * Parent Abstract Node editor for AKB. Manage life cycle of the JCR session that
 * is bound to it.
 */
public abstract class AbstractAkbNodeEditor extends EditorPart {
	// private final static Log log = LogFactory
	// .getLog(AbstractEntityEditor.class);

	// We use a one session per editor pattern to secure various nodes and
	// changes life cycle
	private Repository repository;
	private Session session;

	// Business Objects
	private Node akbNode;

	// LIFE CYCLE
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		try {
			session = repository.login();
			AkbNodeEditorInput anei = (AkbNodeEditorInput) getEditorInput();
			akbNode = session.getNodeByIdentifier(anei.getIdentifier());

			// try to set a default part name
			updatePartName();

			// update tooltip
			// String displayName = CommonsJcrUtils.get(getEntity(),
			// Property.JCR_TITLE);

			// if (CommonsJcrUtils.isEmptyString(displayName))
			// displayName = "current item";
			// setTitleToolTip("Display and edit information for " +
			// displayName);
		} catch (RepositoryException e) {
			throw new AkbException("Unable open editor for akb node", e);
		}
	}

	/**
	 * Overwrite to provide a specific part Name
	 */
	protected void updatePartName() {
		// String name = CommonsJcrUtils.get(entity, Property.JCR_TITLE);
		// if (CommonsJcrUtils.checkNotEmptyString(name)) {
		// if (name.length() > SHORT_NAME_LENGHT)
		// name = name.substring(0, SHORT_NAME_LENGHT - 1) + "...";
		// setPartName(name);
		// }
	}

	/* EXPOSES TO CHILDREN CLASSES */
	protected Session getSession() {
		return session;
	}

	@Override
	public void dispose() {
		try {
			// if (cNode != null)
			// CommonsJcrUtils.cancelAndCheckin(cNode);
		}

		finally {
			JcrUtils.logoutQuietly(session);
		}
		super.dispose();
	}

	@Override
	public void doSaveAs() {
		// unused compulsory method
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		throw new AkbException("Implement this");
	}

	@Override
	public boolean isDirty() {
		try {
			return session.hasPendingChanges();
		} catch (Exception e) {
			throw new AkbException("Error getting session status.", e);
		}
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void setFocus() {
	}

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}