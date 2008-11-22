package org.argeo.slc.support.deploy.db;

import javax.sql.DataSource;

import org.argeo.slc.deploy.AbstractDeployedSystem;

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
