package org.argeo.slc.client.ui.dist.providers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

public class ArtifactLabelProvider extends ColumnLabelProvider implements
		DistConstants {

	// To be able to change column order easily
	public static final int COLUMN_TREE = 0;
	public static final int COLUMN_DATE = 1;
	public static final int COLUMN_SIZE = 2;

	// Utils
	protected DateFormat timeFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);

	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		cell.setText(getColumnText(element, cell.getColumnIndex()));
		// Image image = getImage(element);
		// cell.setImage(image);
		// cell.setBackground(getBackground(element));
		// cell.setForeground(getForeground(element));
		// cell.setFont(getFont(element));
	}

	public String getColumnText(Object element, int columnIndex) {
		try {
			if (element instanceof Node) {
				Node node = (Node) element;
				switch (columnIndex) {
				case COLUMN_TREE:
					return node.getName();
				case COLUMN_SIZE:
					long size = JcrUtils.getNodeApproxSize(node) / 1024;
					if (size > 1024)
						return size / 1024 + " MB";
					else
						return size + " KB";
				case COLUMN_DATE:
					if (node.hasProperty(Property.JCR_CREATED))
						return timeFormatter.format(node
								.getProperty(Property.JCR_CREATED).getDate()
								.getTime());
					else
						return null;
				}
			}
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexepected error while getting property values", re);
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
			throw new ArgeoException("unexpected error while formatting value",
					e);
		}
	}
}
