package org.argeo.slc.postgis;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.tasks.SystemCall;
import org.argeo.slc.gdal.OgrProcess;
import org.springframework.core.io.FileSystemResource;

public class ImportShapefile extends OgrProcess {

	private Boolean createIndex = true;
	private String mode = "-d";
	private String tableName;
	private String dbHost = "localhost";
	private Integer dbPort = 5432;
	private String dbName;
	private String dbUser;

	@Override
	public void run() {
		try {
			File shapefile;
			if (runOgr2Ogr()) {
				shapefile = File.createTempFile(
						FilenameUtils.getBaseName(getSource().getName()),
						FilenameUtils.getExtension(getSource().getName()));
				setTarget(shapefile);
				// do OGR pre-processing
				super.run();
			} else {
				shapefile = getSource();
			}

			if (tableName == null)
				tableName = FilenameUtils.getBaseName(getSource().getName());

			SystemCall shp2pgsql = new SystemCall("shp2pgsql");
			if (createIndex)
				shp2pgsql.arg("-I");
			if (getTargetSrs() != null) {
				if (getTargetSrs().toUpperCase().startsWith("EPSG:")) {
					String srid = getTargetSrs().toUpperCase().substring(
							"EPSG:".length());
					shp2pgsql.arg("-s", srid);
				} else {
					throw new SlcException("Cannot interpret SRS "
							+ getTargetSrs());
				}
			}
			shp2pgsql.arg(mode);
			shp2pgsql.arg(tableName);
			shp2pgsql.arg(shapefile.getAbsolutePath());

			// TODO use pipes
			File sqlOutput = File.createTempFile(
					"shp2psql-"
							+ FilenameUtils.getBaseName(getSource().getName()),
					".sql");
			shp2pgsql.setStdOutFile(new FileSystemResource(sqlOutput));
			shp2pgsql.setRedirectStdOut(true);

			shp2pgsql.run();

			SystemCall psql = new SystemCall("psql");
			psql.arg("-h", dbHost);
			psql.arg("-p", dbPort.toString());
			psql.arg("-d", dbName);
			psql.arg("-U", dbUser);
			psql.setStdInFile(new FileSystemResource(sqlOutput));
			psql.run();

		} catch (Exception e) {
			throw new SlcException("Cannot import " + getSource(), e);
		}
	}

	public void setCreateIndex(Boolean createIndex) {
		this.createIndex = createIndex;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public void setDbPort(Integer dbPort) {
		this.dbPort = dbPort;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

}
