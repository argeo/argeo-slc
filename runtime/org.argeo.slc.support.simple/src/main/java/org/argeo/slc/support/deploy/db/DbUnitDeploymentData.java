package org.argeo.slc.support.deploy.db;

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
