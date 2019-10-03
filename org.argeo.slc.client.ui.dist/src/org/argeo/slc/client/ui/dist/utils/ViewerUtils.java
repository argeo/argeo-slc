package org.argeo.slc.client.ui.dist.utils;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

/** Useful methods to manage table to display nodes list. */
public class ViewerUtils {

	/**
	 * Creates a basic column for the given table. For the time being, we do not
	 * support moveable columns.
	 */
	public static TableColumn createColumn(Table parent, String name,
			int style, int width) {
		TableColumn result = new TableColumn(parent, style);
		result.setText(name);
		result.setWidth(width);
		result.setResizable(true);
		return result;
	}

	/**
	 * Creates a TableViewerColumn for the given viewer. For the time being, we
	 * do not support moveable columns.
	 */
	public static TableViewerColumn createTableViewerColumn(TableViewer parent,
			String name, int style, int width) {
		TableViewerColumn tvc = new TableViewerColumn(parent, style);
		final TableColumn column = tvc.getColumn();
		column.setText(name);
		column.setWidth(width);
		column.setResizable(true);
		return tvc;
	}

	/**
	 * Creates a TreeViewerColumn for the given viewer. For the time being, we
	 * do not support moveable columns.
	 */
	public static TreeViewerColumn createTreeViewerColumn(TreeViewer parent,
			String name, int style, int width) {
		TreeViewerColumn tvc = new TreeViewerColumn(parent, style);
		final TreeColumn column = tvc.getColumn();
		column.setText(name);
		column.setWidth(width);
		column.setResizable(true);
		return tvc;
	}
}
