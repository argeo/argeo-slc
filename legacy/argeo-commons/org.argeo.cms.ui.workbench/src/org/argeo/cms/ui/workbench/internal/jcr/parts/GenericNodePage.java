package org.argeo.cms.ui.workbench.internal.jcr.parts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;

import org.argeo.cms.ui.CmsConstants;
import org.argeo.cms.ui.workbench.internal.WorkbenchConstants;
import org.argeo.eclipse.ui.EclipseUiException;
import org.argeo.jcr.JcrUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Work-In-Progress Node editor page: provides edition feature on String
 * properties for power users. TODO implement manual modification of all
 * property types.
 */

public class GenericNodePage extends FormPage implements WorkbenchConstants {
	// private final static Log log = LogFactory.getLog(GenericNodePage.class);

	// local constants
	private final static String JCR_PROPERTY_NAME = "jcr:name";

	// Utils
	protected DateFormat timeFormatter = new SimpleDateFormat(CmsConstants.DATE_TIME_FORMAT);

	// Main business Objects
	private Node currentNode;

	// This page widgets
	private FormToolkit tk;
	private List<Control> modifyableProperties = new ArrayList<Control>();

	public GenericNodePage(FormEditor editor, String title, Node currentNode) {
		super(editor, "id", title);
		this.currentNode = currentNode;
	}

	protected void createFormContent(IManagedForm managedForm) {
		tk = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		Composite innerBox = form.getBody();
		// Composite innerBox = new Composite(form.getBody(), SWT.NO_FOCUS);
		GridLayout twt = new GridLayout(3, false);
		innerBox.setLayout(twt);
		createPropertiesPart(innerBox);
	}

	private void createPropertiesPart(Composite parent) {
		try {
			AbstractFormPart part = new AbstractFormPart() {
				public void commit(boolean onSave) {
					try {
						if (onSave) {
							ListIterator<Control> it = modifyableProperties.listIterator();
							while (it.hasNext()) {
								// we only support Text controls
								Text curControl = (Text) it.next();
								String value = curControl.getText();
								currentNode.setProperty((String) curControl.getData(JCR_PROPERTY_NAME), value);
							}

							// We only commit when onSave = true,
							// thus it is still possible to save after a tab
							// change.
							if (currentNode.getSession().hasPendingChanges())
								currentNode.getSession().save();
							super.commit(onSave);
						}
					} catch (RepositoryException re) {
						throw new EclipseUiException("Cannot save properties on " + currentNode, re);
					}
				}
			};

			PropertyIterator pi = currentNode.getProperties();
			while (pi.hasNext()) {
				Property prop = pi.nextProperty();
				addPropertyLine(parent, part, prop);
			}
			getManagedForm().addPart(part);
		} catch (RepositoryException re) {
			throw new EclipseUiException("Cannot display properties for " + currentNode, re);
		}
	}

	private void addPropertyLine(Composite parent, AbstractFormPart part, Property prop) {
		try {
			tk.createLabel(parent, prop.getName());
			tk.createLabel(parent, "[" + JcrUtils.getPropertyDefinitionAsString(prop) + "]");

			if (prop.getDefinition().isProtected()) {
				tk.createLabel(parent, formatReadOnlyPropertyValue(prop));
			} else
				addModifyableValueWidget(parent, part, prop);
		} catch (RepositoryException re) {
			throw new EclipseUiException("Cannot display property " + prop, re);
		}
	}

	private String formatReadOnlyPropertyValue(Property prop) throws RepositoryException {
		String strValue;
		if (prop.getType() == PropertyType.BINARY)
			strValue = "<binary>";
		else if (prop.isMultiple())
			strValue = Arrays.asList(prop.getValues()).toString();
		else if (prop.getType() == PropertyType.DATE)
			strValue = timeFormatter.format(prop.getValue().getDate().getTime());
		else
			strValue = prop.getValue().getString();
		return strValue;
	}

	private Control addModifyableValueWidget(Composite parent, AbstractFormPart part, Property prop)
			throws RepositoryException {
		GridData gd;
		if (prop.getType() == PropertyType.STRING && !prop.isMultiple()) {
			Text txt = tk.createText(parent, prop.getString(), SWT.WRAP | SWT.MULTI);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			txt.setLayoutData(gd);
			txt.addModifyListener(new ModifiedFieldListener(part));
			txt.setData(JCR_PROPERTY_NAME, prop.getName());
			modifyableProperties.add(txt);
		} else {
			// unsupported property type for editing, we create a read only
			// label.
			return tk.createLabel(parent, formatReadOnlyPropertyValue(prop));
		}
		return null;
	}

	private class ModifiedFieldListener implements ModifyListener {
		private static final long serialVersionUID = 2117484480773434646L;
		private AbstractFormPart formPart;

		public ModifiedFieldListener(AbstractFormPart generalPart) {
			this.formPart = generalPart;
		}

		public void modifyText(ModifyEvent e) {
			formPart.markDirty();
		}
	}
}
