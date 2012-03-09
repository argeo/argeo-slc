/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.UnsupportedException;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.deploy.DeployedSystem;
import org.argeo.slc.deploy.Deployment;
import org.argeo.slc.deploy.DeploymentData;
import org.argeo.slc.deploy.TargetData;
import org.argeo.slc.support.deploy.db.DbModel;
import org.argeo.slc.support.deploy.db.JdbcAware;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;

public class DbUnitDeployment implements Deployment {
	private static Log log = LogFactory.getLog(DbUnitDeployment.class);

	private JdbcAware jdbcAware;
	private DbUnitDeploymentData deploymentData;
	private DbModel dbModel;

	public void run() {
		try {
			IDatabaseTester databaseTester = new DataSourceDatabaseTester(
					jdbcAware.getDataSource());
			databaseTester.setSetUpOperation(new DatabaseOperation() {

				@Override
				public void execute(IDatabaseConnection connection,
						IDataSet dataSet) throws DatabaseUnitException,
						SQLException {
					if (dbModel != null) {
						dbModel.createSchema(connection.getConnection());
						DatabaseOperation.INSERT.execute(connection, dataSet);
					} else {
						DatabaseOperation.UPDATE.execute(connection, dataSet);
					}
				}

			});
			databaseTester.setDataSet(deploymentData.createDataSet());
			databaseTester.onSetup();
			databaseTester.onTearDown();

			log.info("Database deployed.");
		} catch (Exception e) {
			throw new SlcException("Could not initialize the database", e);
		}
	}

	public DeployedSystem getDeployedSystem() {
		throw new UnsupportedOperationException();
	}

	public void setDbModel(DbModel dbModel) {
		this.dbModel = dbModel;
	}

	public void setDeploymentData(DeploymentData deploymentData) {
		this.deploymentData = (DbUnitDeploymentData) deploymentData;
	}

	public void setTargetData(TargetData targetData) {
		this.jdbcAware = (JdbcAware) targetData;

	}

	public void setDistribution(Distribution distribution) {
		throw new UnsupportedException("Method not supported");
	}

}
