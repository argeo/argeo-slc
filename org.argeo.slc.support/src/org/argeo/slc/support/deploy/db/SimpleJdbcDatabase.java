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
