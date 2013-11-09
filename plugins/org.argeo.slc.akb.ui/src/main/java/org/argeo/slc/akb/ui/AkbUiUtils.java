package org.argeo.slc.akb.ui;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;

import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

/** Some helper methods that factorize widely used snippets in people UI */
public class AkbUiUtils {

	/**
	 * Shortcut to refresh the value of a <code>Text</code> given a Node and a
	 * property Name
	 */
	public static String refreshTextWidgetValue(Text text, Node entity,
			String propName) {
		String tmpStr = AkbJcrUtils.get(entity, propName);
		if (AkbJcrUtils.checkNotEmptyString(tmpStr))
			text.setText(tmpStr);
		return tmpStr;
	}

	/**
	 * Shortcut to refresh a <code>Text</code> widget given a Node in a form and
	 * a property Name. Also manages its enable state
	 */
	public static String refreshFormTextWidget(Text text, Node entity,
			String propName) {
		String tmpStr = AkbJcrUtils.get(entity, propName);
		if (AkbJcrUtils.checkNotEmptyString(tmpStr))
			text.setText(tmpStr);
		text.setEnabled(AkbJcrUtils.isNodeCheckedOutByMe(entity));
		return tmpStr;
	}

	/**
	 * Shortcut to refresh a Check box <code>Button</code> widget given a Node
	 * in a form and a property Name.
	 */
	public static boolean refreshCheckBoxWidget(Button button, Node entity,
			String propName) {
		Boolean tmp = null;
		try {
			if (entity.hasProperty(propName)) {
				tmp = entity.getProperty(propName).getBoolean();
				button.setSelection(tmp);
			}
		} catch (RepositoryException re) {
			throw new AkbException("unable get boolean value for property "
					+ propName);
		}
		return tmp;
	}

	/**
	 * Shortcut to add a default modify listeners to a <code>Text</code> widget
	 * that is bound a JCR String Property. Any change in the text is
	 * immediately stored in the active session, but no save is done.
	 */
	public static void addTextModifyListener(final Text text, final Node node,
			final String propName, final AbstractFormPart part) {
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				if (setJcrProperty(node, propName, PropertyType.STRING,
						text.getText()))
					part.markDirty();
			}
		});
	}

	/**
	 * Centralizes management of updating property value. Among other to avoid
	 * infinite loop when the new value is the same as the ones that is already
	 * stored in JCR.
	 * 
	 * @return true if the value as changed
	 */
	public static boolean setJcrProperty(Node node, String propName,
			int propertyType, Object value) {
		try {
			// int propertyType = getPic().getProperty(propName).getType();
			switch (propertyType) {
			case PropertyType.STRING:
				if ("".equals((String) value)
						&& (!node.hasProperty(propName) || node
								.hasProperty(propName)
								&& "".equals(node.getProperty(propName)
										.getString())))
					// workaround the fact that the Text widget value cannot be
					// set to null
					return false;
				else if (node.hasProperty(propName)
						&& node.getProperty(propName).getString()
								.equals((String) value))
					// nothing changed yet
					return false;
				else {
					node.setProperty(propName, (String) value);
					return true;
				}
			case PropertyType.BOOLEAN:
				if (node.hasProperty(propName)
						&& node.getProperty(propName).getBoolean() == (Boolean) value)
					// nothing changed yet
					return false;
				else {
					node.setProperty(propName, (Boolean) value);
					return true;
				}
			case PropertyType.DATE:
				if (node.hasProperty(propName)
						&& node.getProperty(propName).getDate()
								.equals((Calendar) value))
					// nothing changed yet
					return false;
				else {
					node.setProperty(propName, (Calendar) value);
					return true;
				}
			case PropertyType.LONG:
				Long lgValue = (Long) value;

				if (lgValue == null)
					lgValue = 0L;

				if (node.hasProperty(propName)
						&& node.getProperty(propName).getLong() == lgValue)
					// nothing changed yet
					return false;
				else {
					node.setProperty(propName, lgValue);
					return true;
				}

			default:
				throw new AkbException("Unimplemented save for property type: "
						+ propertyType + " - property: " + propName);

			}
		} catch (RepositoryException re) {
			throw new AkbException("Error while setting property" + propName
					+ " - propertyType: " + propertyType, re);
		}
	}

	// ////////////////////////
	// LAYOUTS AND STYLES

	/** shortcut to set form data while dealing with switching panel */
	public static void setSwitchingFormData(Composite composite) {
		FormData fdLabel = new FormData();
		fdLabel.top = new FormAttachment(0, 0);
		fdLabel.left = new FormAttachment(0, 0);
		fdLabel.right = new FormAttachment(100, 0);
		fdLabel.bottom = new FormAttachment(100, 0);
		composite.setLayoutData(fdLabel);
	}

	public static void setTableDefaultStyle(TableViewer viewer,
			int customItemHeight) {
		Table table = viewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(false);
	}

	/**
	 * Shortcut to provide a gridlayout with no margin and no spacing (dafault
	 * are normally 5 px)
	 */
	public static GridLayout gridLayoutNoBorder() {
		return gridLayoutNoBorder(1);
	}

	/**
	 * Shortcut to provide a gridlayout with no margin and no spacing (default
	 * are normally 5 px) with the given column number (equals width is false).
	 */
	public static GridLayout gridLayoutNoBorder(int nbOfCol) {
		GridLayout gl = new GridLayout(nbOfCol, false);
		gl.marginWidth = gl.marginHeight = gl.horizontalSpacing = gl.verticalSpacing = 0;
		return gl;
	}

	/** Creates a text widget with RowData already set */
	public static Text createRDText(FormToolkit toolkit, Composite parent,
			String msg, String toolTip, int width) {
		Text text = toolkit.createText(parent, "", SWT.BORDER | SWT.SINGLE
				| SWT.LEFT);
		text.setMessage(msg);
		text.setToolTipText(toolTip);
		text.setLayoutData(new RowData(width, SWT.DEFAULT));
		return text;
	}

	/**
	 * Creates a text widget with GridData already set
	 * 
	 * @param toolkit
	 * @param parent
	 * @param msg
	 * @param toolTip
	 * @param width
	 * @param colSpan
	 * @return
	 */
	public static Text createGDText(FormToolkit toolkit, Composite parent,
			String msg, String toolTip, int width, int colSpan) {
		Text text = toolkit.createText(parent, "", SWT.BORDER | SWT.SINGLE
				| SWT.LEFT);
		text.setMessage(msg);
		text.setToolTipText(toolTip);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.widthHint = width;
		gd.horizontalSpan = colSpan;
		text.setLayoutData(gd);
		return text;
	}

	/**
	 * Shortcut to quickly get a FormData object with configured FormAttachment
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @return
	 */
	public static FormData createformData(int left, int top, int right,
			int bottom) {
		FormData formData = new FormData();
		formData.left = new FormAttachment(left, 0);
		formData.top = new FormAttachment(top, 0);
		formData.right = new FormAttachment(right, 0);
		formData.bottom = new FormAttachment(bottom, 0);
		return formData;
	}
}