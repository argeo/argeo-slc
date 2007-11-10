package org.argeo.slc.unit;

import java.io.InputStream;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.hibernate.tool.hbm2ddl.SchemaExport;

import org.argeo.slc.core.SlcException;

public class IndependentDbTestCase extends SpringBasedTestCase {
	private IDatabaseTester databaseTester;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		databaseTester = new DataSourceDatabaseTester(getDataSource());
		databaseTester.setSetUpOperation(new DatabaseOperation() {

			@Override
			public void execute(IDatabaseConnection connection, IDataSet dataSet)
					throws DatabaseUnitException, SQLException {
				DbModel dbModel = getDbModel();
				SchemaExport schemaExport = dbModel
						.createSchemaExport(connection.getConnection());
				schemaExport.create(true, true);

				DatabaseOperation.INSERT.execute(connection, dataSet);
			}

		});
		databaseTester.setDataSet(createDataSet());
		databaseTester.onSetup();
	}

	@Override
	protected void tearDown() throws Exception {
		if (databaseTester != null) {
			databaseTester.onTearDown();
		}
		super.tearDown();
	}

	protected DataSource getDataSource() {
		return (DataSource) getApplicationContext().getBean(
				getDataSourceBeanName());
	}

	protected String getDataSourceBeanName() {
		return "dataSource";
	}

	protected IDataSet createDataSet() {
		try {
			InputStream in = getDataSetResource().getInputStream();
			IDataSet dataSet = new FlatXmlDataSet(in);
			in.close();
			return dataSet;
		} catch (Exception e) {
			throw new SlcException("Cannot create data set", e);
		}
	}

	protected Resource getDataSetResource() {
		return new ClassPathResource(getDataSetResourceLocation());
	}

	protected String getDataSetResourceLocation() {
		return inPackage("dataSet.xml");
	}

	protected DbModel getDbModel() {
		return (DbModel) getApplicationContext().getBean(getDbModelBeanName());
	}

	protected String getDbModelBeanName() {
		return "dbModel";
	}
}
