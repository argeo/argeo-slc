package org.argeo.cms.ui.jcr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.argeo.eclipse.ui.EclipseUiException;
import org.argeo.jcr.JcrUtils;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/** Default basic label provider for a given JCR Node's properties */
public class PropertyLabelProvider extends ColumnLabelProvider {
	private static final long serialVersionUID = -5405794508731390147L;

	// To be able to change column order easily
	public static final int COLUMN_PROPERTY = 0;
	public static final int COLUMN_VALUE = 1;
	public static final int COLUMN_TYPE = 2;
	public static final int COLUMN_ATTRIBUTES = 3;

	private final static String DATE_TIME_FORMAT = "dd/MM/yyyy, HH:mm";

	// Utils
	protected DateFormat timeFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);

	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		cell.setText(getColumnText(element, cell.getColumnIndex()));
	}

	public String getColumnText(Object element, int columnIndex) {
		try {
			if (element instanceof Property) {
				Property prop = (Property) element;
				if (prop.isMultiple()) {
					switch (columnIndex) {
					case COLUMN_PROPERTY:
						return prop.getName();
					case COLUMN_VALUE:
						// Corresponding values are listed on children
						return "";
					case COLUMN_TYPE:
						return JcrBrowserUtils.getPropertyTypeAsString(prop);
					case COLUMN_ATTRIBUTES:
						return JcrUtils.getPropertyDefinitionAsString(prop);
					}
				} else {
					switch (columnIndex) {
					case COLUMN_PROPERTY:
						return prop.getName();
					case COLUMN_VALUE:
						return formatValueAsString(prop.getValue());
					case COLUMN_TYPE:
						return JcrBrowserUtils.getPropertyTypeAsString(prop);
					case COLUMN_ATTRIBUTES:
						return JcrUtils.getPropertyDefinitionAsString(prop);
					}
				}
			} else if (element instanceof Value) {
				Value val = (Value) element;
				switch (columnIndex) {
				case COLUMN_PROPERTY:
					// Nothing to show
					return "";
				case COLUMN_VALUE:
					return formatValueAsString(val);
				case COLUMN_TYPE:
					// listed on the parent
					return "";
				case COLUMN_ATTRIBUTES:
					// Corresponding attributes are listed on the parent
					return "";
				}
			}
		} catch (RepositoryException re) {
			throw new EclipseUiException("Cannot retrieve prop value on " + element, re);
		}
		return null;
	}

	private String formatValueAsString(Value value) {
		// TODO enhance this method
		try {
			String strValue;

			if (value.getType() == PropertyType.BINARY)
				strValue = "<binary>";
			else if (value.getType() == PropertyType.DATE)
				strValue = timeFormatter.format(value.getDate().getTime());
			else
				strValue = value.getString();
			return strValue;
		} catch (RepositoryException e) {
			throw new EclipseUiException("unexpected error while formatting value", e);
		}
	}
}
