package org.argeo.slc.support.deploy.db;

import javax.sql.DataSource;

import org.argeo.slc.build.Distribution;
import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.TargetData;

public class SimpleJdbcDatabase implements DeployedSystem, JdbcAware {
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

	@Override
	public Distribution getDistribution() {
		throw new UnsupportedOperationException();
	}

	@Override
	public DeploymentData getDeploymentData() {
		throw new UnsupportedOperationException();
	}

	@Override
	public TargetData getTargetData() {
		throw new UnsupportedOperationException();
	}
}
