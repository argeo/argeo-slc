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
package org.argeo.slc.client.ui.dist.utils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.GenericTableComparator;
import org.eclipse.jface.viewers.Viewer;

/** Add ability to order by name version and version */
public class DistNodeViewerComparator extends GenericTableComparator {
	private static final long serialVersionUID = -5966120108210992211L;

	private final static Log log = LogFactory
			.getLog(DistNodeViewerComparator.class);

	// Jcr property type goes to 12
	public final static int NAME_VERSION_TYPE = 100;
	public final static int VERSION_TYPE = 101;

	protected List<String> propertiesList;
	protected List<Integer> propertyTypesList;
	protected Integer propertyType;
	protected String property;

	private NameVersionComparator nvc = new NameVersionComparator();
	private VersionComparator vc = new VersionComparator();

	public DistNodeViewerComparator(int defaultColIndex, int defaultDirection,
			List<String> propertiesList, List<Integer> propertyTypesList) {
		super(defaultColIndex, defaultDirection);
		this.propertiesList = propertiesList;
		this.propertyTypesList = propertyTypesList;
		this.propertyIndex = defaultColIndex;
		this.propertyType = propertyTypesList.get(defaultColIndex);
		this.property = propertiesList.get(defaultColIndex);
		setColumn(defaultColIndex);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int rc = 0;
		long lc = 0;

		try {
			Node n1 = (Node) e1;
			Node n2 = (Node) e2;

			Value v1 = null;
			Value v2 = null;
			if (n1.hasProperty(property))
				v1 = n1.getProperty(property).getValue();
			if (n2.hasProperty(property))
				v2 = n2.getProperty(property).getValue();

			if (v2 == null && v1 == null)
				return 0;
			else if (v2 == null)
				return -1;
			else if (v1 == null)
				return 1;

			switch (propertyType) {
			case NAME_VERSION_TYPE:
				rc = nvc.compare(viewer, v1.getString(), v2.getString());
				break;
			case VERSION_TYPE:
				rc = vc.compare(viewer, v1.getString(), v2.getString());
				break;
			case PropertyType.STRING:
				rc = v1.getString().compareTo(v2.getString());
				break;
			case PropertyType.BOOLEAN:
				boolean b1 = v1.getBoolean();
				boolean b2 = v2.getBoolean();
				if (b1 == b2)
					rc = 0;
				else
					// we assume true is greater than false
					rc = b1 ? 1 : -1;
				break;
			case PropertyType.DATE:
				Calendar c1 = v1.getDate();
				Calendar c2 = v2.getDate();
				if (c1 == null || c2 == null)
					log.trace("undefined date");
				lc = c1.getTimeInMillis() - c2.getTimeInMillis();
				if (lc < Integer.MIN_VALUE)
					// rc = Integer.MIN_VALUE;
					rc = -1;
				else if (lc > Integer.MAX_VALUE)
					// rc = Integer.MAX_VALUE;
					rc = 1;
				else
					rc = (int) lc;
				break;
			case PropertyType.LONG:
				long l1;
				long l2;
				// FIXME sometimes an empty string is set instead of the id
				try {
					l1 = v1.getLong();
				} catch (ValueFormatException ve) {
					l1 = 0;
				}
				try {
					l2 = v2.getLong();
				} catch (ValueFormatException ve) {
					l2 = 0;
				}

				lc = l1 - l2;
				if (lc < Integer.MIN_VALUE)
					// rc = Integer.MIN_VALUE;
					rc = -1;
				else if (lc > Integer.MAX_VALUE)
					// rc = Integer.MAX_VALUE;
					rc = 1;
				else
					rc = (int) lc;
				break;
			case PropertyType.DECIMAL:
				BigDecimal bd1 = v1.getDecimal();
				BigDecimal bd2 = v2.getDecimal();
				rc = bd1.compareTo(bd2);
				break;
			default:
				throw new ArgeoException(
						"Unimplemented comparaison for PropertyType "
								+ propertyType);
			}

			// If descending order, flip the direction
			if (direction == DESCENDING) {
				rc = -rc;
			}

		} catch (RepositoryException re) {
			throw new ArgeoException("Unexpected error "
					+ "while comparing nodes", re);
		}
		return rc;
	}

	@Override
	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do a descending sort
			this.propertyIndex = column;
			this.propertyType = propertyTypesList.get(column);
			this.property = propertiesList.get(column);
			direction = ASCENDING;
		}
	}
}