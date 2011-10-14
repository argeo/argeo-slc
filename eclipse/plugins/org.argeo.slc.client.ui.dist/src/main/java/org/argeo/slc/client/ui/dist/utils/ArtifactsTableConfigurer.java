package org.argeo.slc.client.ui.dist.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Row;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.GenericTableComparator;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Centralizes and factorizes useful methods to create and manage tables that
 * display artifacts for both editors and views.
 */
public class ArtifactsTableConfigurer implements SlcNames, SlcTypes,
		DistConstants {
	private final static Log log = LogFactory
			.getLog(ArtifactsTableConfigurer.class);
	// Used in the comparator to be able to retrieve the value from a row
	// knowing the corresponding column index.
	private Map<Integer, String> indexToName = new HashMap<Integer, String>();

	private CurrentTableComparator comparator;
	private TableViewer viewer;

	protected DateFormat timeFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);

	/**
	 * Create and initialize the table configurer.
	 */
	public ArtifactsTableConfigurer(TableViewer viewer,
			int defaultSortColumnIndex, int direction) {
		this.viewer = viewer;
		comparator = new CurrentTableComparator(defaultSortColumnIndex,
				direction);
	}

	public GenericTableComparator getComparator() {
		return comparator;
	}

	/**
	 * Configure column width and header label depending on the value that will
	 * be displayed in the current column.
	 * 
	 * @param jcrColumnName
	 * @param column
	 * @param columnIndex
	 */
	public void configureColumn(String jcrColumnName, TableViewerColumn column,
			int columnIndex) {

		if (columnIndex != -1
				&& getSelectionAdapter(column.getColumn(), columnIndex) != null) {
			column.getColumn().addSelectionListener(
					getSelectionAdapter(column.getColumn(), columnIndex));
			indexToName.put(new Integer(columnIndex), jcrColumnName);
		}
		Object[] objs = DistUiHelpers
				.getLabelAndDefaultValueWidth(jcrColumnName);
		column.getColumn().setWidth((Integer) objs[1]);
		column.getColumn().setText((String) objs[0]);
	}

	/**
	 * Might be used by client classes to sort the table with based on selected
	 * columns.
	 * 
	 * @param column
	 * @param index
	 * @return
	 */
	public SelectionAdapter getSelectionAdapter(final TableColumn column,
			final int index) {

		// A comparator must be define
		if (comparator == null)
			return null;

		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				try {

					comparator.setColumn(index);
					int dir = viewer.getTable().getSortDirection();
					if (viewer.getTable().getSortColumn() == column) {
						dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
					} else {

						dir = SWT.DOWN;
					}
					viewer.getTable().setSortDirection(dir);
					viewer.getTable().setSortColumn(column);
					viewer.refresh();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		};
		return selectionAdapter;
	}

	/**
	 * provides a label provider that returns the content of a specific cell.
	 * Specific treatment is done for some columns when the query returns a code
	 * that must be translated to the corresponding value at display time.
	 */
	public ColumnLabelProvider getLabelProvider(final String columnName) {
		boolean test = false;

		if (test) {
			return new ColumnLabelProvider() {
				public String getText(Object element) {
					return null;
				}

				public Image getImage(Object element) {
					return null;
				}
			};
		} else
			return new ColumnLabelProvider() {
				public String getText(Object element) {
					Row row = (Row) element;
					try {
						return row.getValue(columnName).getString();
					} catch (RepositoryException e) {
						throw new ArgeoException("Cannot display row " + row, e);
					}
				}

				public Image getImage(Object element) {
					return null;
				}
			};
	}

	/** Implements comparator for various types of Artifact Table row */
	private class CurrentTableComparator extends GenericTableComparator {

		public CurrentTableComparator(int colIndex, int direction) {
			super(colIndex, direction);
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			int rc = 0;

			if (e1 instanceof Row) {
				try {

					Value v1 = ((Row) e1).getValue(indexToName
							.get(propertyIndex));
					Value v2 = ((Row) e2).getValue(indexToName
							.get(propertyIndex));

					if (v1.getType() == PropertyType.STRING)
						rc = v1.getString().compareTo(v2.getString());
					else if (v1.getType() == PropertyType.DATE)
						rc = v1.getDate().compareTo(v2.getDate());
					else
						throw new ArgeoException("comparator for object type "
								+ v1.getType() + " is not yet implemented");
				} catch (Exception e) {
					throw new ArgeoException("rows cannot be compared ", e);
				}
			} else
				throw new ArgeoException("Unsupported row type");
			// If descending order, flip the direction
			if (direction == DESCENDING) {
				rc = -rc;
			}
			return rc;
		}
	}
}
