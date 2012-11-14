/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.client.ui.dist.providers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.client.ui.dist.DistImages;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

public class ArtifactLabelProvider extends ColumnLabelProvider implements
		DistConstants, SlcTypes {

	// To be able to change column order easily
	public static final int COLUMN_TREE = 0;
	public static final int COLUMN_DATE = 1;
	public static final int COLUMN_SIZE = 2;

	// Utils
	protected static DateFormat timeFormatter = new SimpleDateFormat(
			DATE_TIME_FORMAT);

	public void update(ViewerCell cell) {
		int colIndex = cell.getColumnIndex();
		Object element = cell.getElement();
		cell.setText(getColumnText(element, colIndex));
		if (element instanceof Node && colIndex == 0) {
			Node node = (Node) element;
			try {
				if (node.isNodeType(SLC_ARTIFACT_BASE))
					cell.setImage(DistImages.IMG_ARTIFACT_BASE);
				else if (node.isNodeType(SLC_ARTIFACT_VERSION_BASE))
					cell.setImage(DistImages.IMG_ARTIFACT_VERSION_BASE);
			} catch (RepositoryException e) {
				// Silent
			}
		}
	}

	@Override
	public Image getImage(Object element) {

		if (element instanceof Node) {
			Node node = (Node) element;
			try {
				if (node.isNodeType(SLC_ARTIFACT_BASE)) {
					return DistImages.IMG_ARTIFACT_BASE;
				} else if (node.isNodeType(SLC_ARTIFACT_VERSION_BASE)) {
					return DistImages.IMG_ARTIFACT_VERSION_BASE;
				}
			} catch (RepositoryException e) {
				// Silent
			}
		}
		return null;
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
					if (node.hasProperty(Property.JCR_LAST_MODIFIED))
						return timeFormatter.format(node
								.getProperty(Property.JCR_LAST_MODIFIED)
								.getDate().getTime());
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
}
