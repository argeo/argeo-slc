package org.argeo.slc.akb.ui.editors;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Parent Abstract Node editor for AKB. Manage life cycle of the JCR session
 * that is bound to it.
 */
public abstract class AbstractAkbNodeEditor extends FormEditor {
	// private final static Log log = LogFactory
	// .getLog(AbstractEntityEditor.class);

	// We use a one session per editor pattern to secure various nodes and
	// changes life cycle
	private Repository repository;
	private Session session;
	private AkbService akbService;

	// Business Objects
	private Node akbNode;

	// Some constants
	private final static int SHORT_NAME_LENGHT = 10;

	// to implement methods
	protected abstract String getEditorId();

	protected abstract void populateMainPage(Composite parent,
			IManagedForm managedForm);

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		try {
			session = repository.login();
			AkbNodeEditorInput anei = (AkbNodeEditorInput) getEditorInput();
			akbNode = session.getNodeByIdentifier(anei.getIdentifier());
			updatePartNameAndToolTip();
		} catch (RepositoryException e) {
			throw new AkbException("Unable open editor for akb node", e);
		}
	}

	/**
	 * Overwrite to provide a specific part Name and / or tooltip
	 */
	protected void updatePartNameAndToolTip() {
		String name = JcrUtils.get(akbNode, Property.JCR_TITLE);

		// Name
		if (AkbJcrUtils.checkNotEmptyString(name)) {
			if (name.length() > SHORT_NAME_LENGHT)
				name = name.substring(0, SHORT_NAME_LENGHT - 1) + "...";
			setPartName(name);
		}

		// Tooltip
		if (AkbJcrUtils.isEmptyString(name))
			name = "current item";
		setTitleToolTip("Display and edit " + name);
	}

	/** Overwrite to provide supplementary pages between main and history page */
	protected void addOtherPages() throws PartInitException {
	}

	/* Pages management */
	@Override
	protected void addPages() {
		try {
			addPage(new ConnectorAliasPage(this, "mainPage", "Main"));
			// TODO Add history page
			// addPage(new ConnectorAliasPage(this, "mainPage", "Main"));
		} catch (PartInitException e) {
			throw new AkbException("Unable to initialise pages for editor "
					+ getEditorId(), e);
		}
	}

	/**
	 * Display and edit info
	 */
	private class ConnectorAliasPage extends FormPage {

		public ConnectorAliasPage(FormEditor editor, String id, String title) {
			super(editor, id, title);
		}

		protected void createFormContent(IManagedForm managedForm) {
			super.createFormContent(managedForm);
			ScrolledForm form = managedForm.getForm();
			form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			populateMainPage(form.getBody(), managedForm);
		}
	}

	/* EXPOSES TO CHILDREN CLASSES */
	protected Session getSession() {
		return session;
	}

	protected AkbService getAkbService() {
		return akbService;
	}

	protected Node getAkbNode() {
		return akbNode;
	}

	/* LIFE CYCLE MANAGEMENT */
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
		try {
			if (getSession().hasPendingChanges())
				JcrUtils.updateLastModified(getAkbNode());
			getSession().save();
			updatePartNameAndToolTip();
			this.firePropertyChange(PROP_DIRTY);
		} catch (Exception e) {
			throw new AkbException("Error getting session status.", e);
		}
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

	public void setAkbService(AkbService akbService) {
		this.akbService = akbService;
	}
}