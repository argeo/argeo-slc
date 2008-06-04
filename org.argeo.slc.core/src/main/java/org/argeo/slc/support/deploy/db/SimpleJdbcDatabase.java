package org.argeo.slc.support.deploy.db;

import javax.sql.DataSource;

import org.argeo.slc.core.UnsupportedException;
import org.argeo.slc.core.build.Distribution;
import org.argeo.slc.core.deploy.AbstractDeployedSystem;
import org.argeo.slc.core.deploy.DeployedSystem;
import org.argeo.slc.core.deploy.DeploymentData;
import org.argeo.slc.core.deploy.TargetData;

public class SimpleJdbcDatabase extends AbstractDeployedSystem implements
		JdbcAware {
	private DataSource dataSource;

	public String getDeployedSystemId() {
		return dataSource.toString();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
