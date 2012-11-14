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
package org.argeo.slc.unit.db;

import java.io.InputStream;

import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.DeploymentData;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.core.io.Resource;

public class DbUnitDeploymentData implements DeploymentData {
	private Resource dataSetLocation;

	public IDataSet createDataSet() {
		try {
			InputStream in = dataSetLocation.getInputStream();
			IDataSet dataSet = new FlatXmlDataSet(in);
			in.close();
			return dataSet;
		} catch (Exception e) {
			throw new SlcException("Cannot create data set", e);
		}

	}

	public void setDataSetLocation(Resource dataSetLocation) {
		this.dataSetLocation = dataSetLocation;
	}

}
