package org.argeo.slc.support.deploy.db;

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
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;

public class DbUnitDeployment implements Deployment {
	private static Log log = LogFactory.getLog(DbUnitDeployment.class);

	private JdbcAware mxDatabase;
	private DbUnitDeploymentData deploymentData;
	private DbModel dbModel;

	public void run() {
		try {
			IDatabaseTester databaseTester = new DataSourceDatabaseTester(
					mxDatabase.getDataSource());
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
		// TODO: think of a more generic approach. MxDtaabse deployed system?
		// (with deployment id etc.)
		throw new UnsupportedException("Method not supported");
	}

	public void setDbModel(DbModel dbModel) {
		this.dbModel = dbModel;
	}

	public void setDeploymentData(DeploymentData deploymentData) {
		this.deploymentData = (DbUnitDeploymentData) deploymentData;
	}

	public void setTargetData(TargetData targetData) {
		this.mxDatabase = (JdbcAware) targetData;

	}

	public void setDistribution(Distribution distribution) {
		throw new UnsupportedException("Method not supported");
	}

}
